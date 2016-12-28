package com.sogou.map.kubbo.remote.transport.netty4;

import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.common.logger.Logger;
import com.sogou.map.kubbo.common.logger.LoggerFactory;
import com.sogou.map.kubbo.common.utils.NamedThreadFactory;
import com.sogou.map.kubbo.common.utils.NetUtils;
import com.sogou.map.kubbo.remote.Channel;
import com.sogou.map.kubbo.remote.ChannelHandler;
import com.sogou.map.kubbo.remote.RemotingException;
import com.sogou.map.kubbo.remote.Server;
import com.sogou.map.kubbo.remote.transport.AbstractServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.internal.SystemPropertyUtil;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

/**
 * NettyServer
 * 
 * @author liufuliang
 */
public class NettyServer extends AbstractServer implements Server {

    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private Map<String, Channel>  channels; // <ip:port, channel>

    private io.netty.channel.Channel serverChannel;

    private EventLoopGroup bossGroup;

    private EventLoopGroup workerGroup;    
    
    public NettyServer(URL url, ChannelHandler handler) throws RemotingException{
        super(url, wrapChannelHandler(url, handler));
    }
    
    private static final int DEFAULT_EVENT_LOOP_THREADS;

    static {
        DEFAULT_EVENT_LOOP_THREADS = Math.max(1, SystemPropertyUtil.getInt("io.netty.eventLoopThreads", Constants.DEFAULT_IO_THREADS));

        if (logger.isDebugEnabled()) {
            logger.debug("-Dio.netty.eventLoopThreads: " + DEFAULT_EVENT_LOOP_THREADS);
        }
    }

    @Override
    protected void doOpen() throws Throwable {
        NettyLoggerAdapter.setNettyLoggerFactory();
        
        final NettyHandler nettyHandler = new NettyHandler(getUrl(), NettyServer.this);
        channels = nettyHandler.getChannels();

        bossGroup = new NioEventLoopGroup(1, new NamedThreadFactory("NettyServerBoss", true));
        workerGroup = new NioEventLoopGroup(getUrl().getPositiveParameter(Constants.IO_THREADS_KEY, DEFAULT_EVENT_LOOP_THREADS), new NamedThreadFactory("NettyServerEventLoopGroup", true));
        ServerBootstrap bootstrap = new ServerBootstrap()
        		.group(bossGroup, workerGroup)
        		.channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 100)
        		.childOption(ChannelOption.TCP_NODELAY, true)
        		.childHandler(new ChannelInitializer<SocketChannel>() {
        				public void initChannel(SocketChannel ch) {			                
			                NettyTransportEncoder encoder = new NettyTransportEncoder(getCodec(), getUrl(), NettyServer.this);
			                NettyTransportDecoder decoder = new NettyTransportDecoder(getCodec(), getUrl(), NettyServer.this);
			                ChannelPipeline channelPipeline = ch.pipeline();
			                channelPipeline.addLast("decoder", decoder);
			                channelPipeline.addLast("encoder", encoder);
			                channelPipeline.addLast("handler", nettyHandler);
			            }
        });

        // bind
        ChannelFuture channelFuture = bootstrap.bind(getBindAddress());
        serverChannel = channelFuture.awaitUninterruptibly().channel();
    }

    @Override
    protected void doClose() throws Throwable {
        try {
            if (serverChannel != null) {
                // unbind.
                serverChannel.close().syncUninterruptibly();
            }
        } catch (Throwable e) {
            logger.warn(e.getMessage(), e);
        }
        try {
            Collection<com.sogou.map.kubbo.remote.Channel> channels = getChannels();
            if (channels != null && channels.size() > 0) {
                for (com.sogou.map.kubbo.remote.Channel channel : channels) {
                    try {
                        channel.close();
                    } catch (Throwable e) {
                        logger.warn(e.getMessage(), e);
                    }
                }
            }
        } catch (Throwable e) {
            logger.warn(e.getMessage(), e);
        }

        try {
            // and then shutdown the thread pools
            if (bossGroup != null) {
                bossGroup.shutdownGracefully();
            }
            if (workerGroup != null) {
                workerGroup.shutdownGracefully();
            }
        } catch (Throwable e) {
            logger.warn(e.getMessage(), e);
        }

        try {
            if (channels != null) {
                channels.clear();
            }
        } catch (Throwable e) {
            logger.warn(e.getMessage(), e);
        }
    }
    
    @Override
    public Collection<Channel> getChannels() {
        Collection<Channel> chs = new HashSet<Channel>();
        for (Channel channel : this.channels.values()) {
            if (channel.isConnected()) {
                chs.add(channel);
            } else {
                channels.remove(NetUtils.toAddressString(channel.getRemoteAddress()));
            }
        }
        return chs;
    }

    @Override
    public Channel getChannel(InetSocketAddress remoteAddress) {
        return channels.get(NetUtils.toAddressString(remoteAddress));
    }

    @Override
    public boolean isBound() {
        return serverChannel.isRegistered();
    }

}