package com.sogou.map.kubbo.remote.transport.netty4;

import java.util.concurrent.TimeUnit;
import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.common.logger.Logger;
import com.sogou.map.kubbo.common.logger.LoggerFactory;
import com.sogou.map.kubbo.common.util.NamedThreadFactory;
import com.sogou.map.kubbo.common.util.NetUtils;
import com.sogou.map.kubbo.remote.ChannelHandler;
import com.sogou.map.kubbo.remote.RemotingException;
import com.sogou.map.kubbo.remote.transport.AbstractClient;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.internal.SystemPropertyUtil;

/**
 * NettyClient.
 * 
 * @author liufuliang
 */
public class NettyClient extends AbstractClient {

    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private Bootstrap bootstrap;

    private volatile Channel channel; // volatile, please copy reference to use

    public NettyClient(final URL url, final ChannelHandler handler) throws RemotingException{
        super(url, wrapChannelHandler(url, handler));
    }

    private static final int DEFAULT_EVENT_LOOP_THREADS;

    static {
        DEFAULT_EVENT_LOOP_THREADS = Math.max(1, SystemPropertyUtil.getInt("io.netty.eventLoopThreads", Constants.DEFAULT_IO_THREADS));

        if (logger.isDebugEnabled()) {
            logger.debug("-Dio.netty.eventLoopThreads: " + DEFAULT_EVENT_LOOP_THREADS);
        }
    }

    private static final EventLoopGroup WORKER_GROUP = new NioEventLoopGroup(DEFAULT_EVENT_LOOP_THREADS, new NamedThreadFactory("NettyClientEventLoopGroup", true));
    
    @Override
    protected void doOpen() throws Throwable {
        NettyLoggerAdapter.setNettyLoggerFactory();
        bootstrap = new Bootstrap();
        // config
        bootstrap.channel(NioSocketChannel.class)
                 .group(WORKER_GROUP)
                 .option(ChannelOption.SO_KEEPALIVE, true)
                 .option(ChannelOption.TCP_NODELAY, true)
                 .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, getConnectTimeout())
                 .handler(new ChannelInitializer<SocketChannel>() {
                             public void initChannel(SocketChannel ch) {
                                NettyTransportEncoder encoder = new NettyTransportEncoder(getCodec(), getUrl(), NettyClient.this);
                                NettyTransportDecoder decoder = new NettyTransportDecoder(getCodec(), getUrl(), NettyClient.this);
                                ChannelPipeline channelPipeline = ch.pipeline();
                                channelPipeline.addLast("decoder", decoder);
                                channelPipeline.addLast("encoder", encoder);
                                channelPipeline.addLast("handler", new NettyHandler(getUrl(), NettyClient.this));
                             }
                 });
        
    }

    protected void doConnect() throws Throwable {
        long start = System.currentTimeMillis();
        ChannelFuture future = bootstrap.connect(getConnectAddress());
        try{
            boolean ret = future.awaitUninterruptibly(getConnectTimeout(), TimeUnit.MILLISECONDS);
            
            if (ret && future.isSuccess()) {
                Channel newChannel = future.channel();

                try {
                    // 关闭旧的连接
                    Channel oldChannel = NettyClient.this.channel; // copy reference
                    if (oldChannel != null) {
                        try {
                            if (logger.isInfoEnabled()) {
                                logger.info("Close old Netty channel " + oldChannel + " on create new Netty channel " + newChannel);
                            }
                            oldChannel.close().syncUninterruptibly();
                        } finally {
                            NettyChannel.removeChannelIfDisconnected(oldChannel);
                        }
                    }
                } finally {
                    if (NettyClient.this.isClosed()) {
                        try {
                            if (logger.isInfoEnabled()) {
                                logger.info("Close new Netty channel " + newChannel + ", because the client closed.");
                            }
                            newChannel.close().syncUninterruptibly();
                        } finally {
                            NettyClient.this.channel = null;
                            NettyChannel.removeChannelIfDisconnected(newChannel);
                        }
                    } else {
                        NettyClient.this.channel = newChannel;
                    }
                }
            } 
            else if (future.cause() != null) {
                throw new RemotingException(this, "client(url: " + getUrl() + ") failed to connect to server "
                        + getRemoteAddress() + ", error message is:" + future.cause().getMessage(), future.cause());
            } 
            else {
                throw new RemotingException(this, "client(url: " + getUrl() + ") failed to connect to server "
                        + getRemoteAddress() + " client-side timeout "
                        + getConnectTimeout() + "ms (elapsed: " + (System.currentTimeMillis() - start) + "ms) from Netty client "
                        + NetUtils.getLocalHost());
            }
        }finally{
            if (! isConnected()) {
                future.cancel(true);
            }
        }
    }

    @Override
    protected void doDisConnect() throws Throwable {
        try {
            NettyChannel.removeChannelIfDisconnected(channel);
        } catch (Throwable t) {
            logger.warn(t.getMessage());
        }
    }
    
    @Override
    protected void doClose() throws Throwable {
        //WORKER_GROUP.shutdownGracefully();
    }

    @Override
    protected com.sogou.map.kubbo.remote.Channel getChannel() {
        Channel c = channel;
        return NettyChannel.getOrAddChannel(c, getUrl(), this);
    }

}