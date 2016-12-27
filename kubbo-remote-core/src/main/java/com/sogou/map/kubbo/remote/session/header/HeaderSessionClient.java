package com.sogou.map.kubbo.remote.session.header;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.logger.Logger;
import com.sogou.map.kubbo.common.logger.LoggerFactory;
import com.sogou.map.kubbo.common.utils.NamedThreadFactory;
import com.sogou.map.kubbo.remote.Channel;
import com.sogou.map.kubbo.remote.Client;
import com.sogou.map.kubbo.remote.RemotingException;
import com.sogou.map.kubbo.remote.heartbeat.HeartBeatTask;
import com.sogou.map.kubbo.remote.session.SessionChannel;
import com.sogou.map.kubbo.remote.session.SessionClient;
import com.sogou.map.kubbo.remote.session.SessionHandler;
import com.sogou.map.kubbo.remote.session.ResponseFuture;
import com.sogou.map.kubbo.remote.transport.AbstractClientDelegate;

/**
 * HeaderSessionClient
 * 
 * @author liufuliang
 */
public class HeaderSessionClient extends AbstractClientDelegate implements SessionClient {

    private static final Logger logger = LoggerFactory.getLogger( HeaderSessionClient.class );

    private static final ScheduledThreadPoolExecutor scheduled = new ScheduledThreadPoolExecutor(2, new NamedThreadFactory("KubboHeartbeat", true));

    // 心跳定时器
    private ScheduledFuture<?> heatbeatTimer;

    // 心跳超时，毫秒。缺省0，不会执行心跳。
    private int heartbeat;

    private int heartbeatTimeout;
    
    private final SessionChannel sessionChannel;

    public HeaderSessionClient(Client client){
    	super(client);
        if (client == null) {
            throw new IllegalArgumentException("client == NULL");
        }
        this.sessionChannel = new HeaderSessionChannel(client);
        this.heartbeat = client.getUrl().getParameter( Constants.HEARTBEAT_KEY, 0 );
        this.heartbeatTimeout = client.getUrl().getParameter( Constants.HEARTBEAT_TIMEOUT_KEY, heartbeat * 3 );
        if ( heartbeatTimeout < heartbeat * 2 ) {
            throw new IllegalStateException( "heartbeatTimeout < heartbeatInterval * 2" );
        }
        startHeatbeatTimer();
    }
    @Override
    public ResponseFuture request(Object request) throws RemotingException {
        return sessionChannel.request(request);
    }
 
    @Override
    public ResponseFuture request(Object request, int timeout) throws RemotingException {
        return sessionChannel.request(request, timeout);
    }

    @Override
    public SessionHandler getSessionHandler() {
        return sessionChannel.getSessionHandler();
    }

    @Override
    public void close() {
        doClose();
        sessionChannel.close();
    }
    @Override
    public void close(int timeout) {
        doClose();
        sessionChannel.close(timeout);
    }

    private void startHeatbeatTimer() {
        stopHeartbeatTimer();
        if ( heartbeat > 0 ) {
            heatbeatTimer = scheduled.scheduleWithFixedDelay(
                    new HeartBeatTask( new HeartBeatTask.ChannelProvider() {
                        public Collection<Channel> getChannels() {
                            return Collections.<Channel>singletonList( HeaderSessionClient.this );
                        }
                    }, heartbeat, heartbeatTimeout),
                    heartbeat, heartbeat, TimeUnit.MILLISECONDS );
        }
    }

    private void stopHeartbeatTimer() {
        if (heatbeatTimer != null && ! heatbeatTimer.isCancelled()) {
            try {
                heatbeatTimer.cancel(true);
                scheduled.purge();
            } catch ( Throwable e ) {
                if (logger.isWarnEnabled()) {
                    logger.warn(e.getMessage(), e);
                }
            }
        }
        heatbeatTimer =null;
    }

    private void doClose() {
        stopHeartbeatTimer();
    }

	@Override
	public String toString() {
		return "HeaderSessionClient [channel=" + sessionChannel + "]";
	}
}