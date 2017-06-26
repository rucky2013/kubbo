package com.sogou.map.kubbo.remote.heartbeat;

import java.util.Collection;

import com.sogou.map.kubbo.common.Version;
import com.sogou.map.kubbo.common.logger.Logger;
import com.sogou.map.kubbo.common.logger.LoggerFactory;
import com.sogou.map.kubbo.remote.Channel;
import com.sogou.map.kubbo.remote.Client;
import com.sogou.map.kubbo.remote.session.Request;


/**
 * @author liufuliang
 */
public final class HeartBeatTask implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(HeartBeatTask.class);

    private ChannelProvider channelProvider;

    private int heartbeat;

    private int heartbeatTimeout;

    public HeartBeatTask( ChannelProvider provider, int heartbeat, int heartbeatTimeout ) {
        this.channelProvider = provider;
        this.heartbeat = heartbeat;
        this.heartbeatTimeout = heartbeatTimeout;
    }

    @Override
    public void run() {
        try {
            long now = System.currentTimeMillis();
            for ( Channel channel : channelProvider.getChannels() ) {
                if (channel.isClosed()) {
                    continue;
                }
                try {
                    Long lastRead = (Long) channel.getAttribute(HeartbeatHandler.KEY_READ_TIMESTAMP);
                    Long lastWrite = (Long) channel.getAttribute(HeartbeatHandler.KEY_WRITE_TIMESTAMP);
                    if ( ( lastRead != null && now - lastRead > heartbeat )
                            || ( lastWrite != null && now - lastWrite > heartbeat ) ) {
                        Request req = new Request();
                        req.setVersion(Version.getVersion());
                        req.setTwoWay(true);
                        req.setEvent(Request.HEARTBEAT_EVENT);
                        channel.send(req);
                        if ( logger.isDebugEnabled() ) {
                            logger.debug( "Heartbeat to remote " + channel.getRemoteAddress()
                                                  + ", because the channel has no data-transmission in " + heartbeat + "ms" );
                        }
                    }
                    if ( lastRead != null && now - lastRead > heartbeatTimeout ) {
                        logger.warn( "Close channel " + channel + ", because heartbeat timed out with " + heartbeatTimeout + "ms" );
                        if (channel instanceof Client) {
                            // reconnect channel on client side
                            try {
                                ((Client)channel).reconnect();
                            }catch (Exception e) {
                                //do nothing
                            }
                        } else {
                            // close channel on server side
                            channel.close();
                        }
                    }
                } catch ( Throwable t ) {
                    logger.warn( "Exception when heartbeat to remote " + channel.getRemoteAddress(), t );
                }
            }
        } catch ( Throwable t ) {
            logger.warn( "Unhandled exception when heartbeat to remote, cause: " + t.getMessage(), t );
        }
    }

    public interface ChannelProvider {
        Collection<Channel> getChannels();
    }

}

