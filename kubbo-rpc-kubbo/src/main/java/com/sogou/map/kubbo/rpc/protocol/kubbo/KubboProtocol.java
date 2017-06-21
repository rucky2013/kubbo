package com.sogou.map.kubbo.rpc.protocol.kubbo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.sogou.map.kubbo.common.Constants;
import com.sogou.map.kubbo.common.URL;
import com.sogou.map.kubbo.remote.Channel;
import com.sogou.map.kubbo.remote.RemotingException;
import com.sogou.map.kubbo.remote.session.SessionChannel;
import com.sogou.map.kubbo.remote.session.SessionClient;
import com.sogou.map.kubbo.remote.session.SessionHandler;
import com.sogou.map.kubbo.remote.session.SessionServer;
import com.sogou.map.kubbo.remote.session.SessionLayers;
import com.sogou.map.kubbo.remote.session.handler.SessionHandlerAdapter;
import com.sogou.map.kubbo.rpc.Exporter;
import com.sogou.map.kubbo.rpc.Invocation;
import com.sogou.map.kubbo.rpc.Invoker;
import com.sogou.map.kubbo.rpc.Result;
import com.sogou.map.kubbo.rpc.RpcException;
import com.sogou.map.kubbo.rpc.protocol.AbstractProtocol;
import com.sogou.map.kubbo.rpc.protocol.kubbo.codec.KubboCodec;
import com.sogou.map.kubbo.rpc.utils.RpcHelper;

/**
 * kubbo protocol support.
 *
 * @author liufuliang
 */
public class KubboProtocol extends AbstractProtocol {

    public static final String NAME = "kubbo";
    
    public static final int DEFAULT_PORT = 40660;
        
    private final Map<String, SessionServer> serverMap = new ConcurrentHashMap<String, SessionServer>(); // <host:port,SessionServer>
    
    private final Map<String, ReferenceCountSessionClient> referenceClientMap = new ConcurrentHashMap<String, ReferenceCountSessionClient>(); // <host:port,SessionClient>
    
    private SessionHandler requestHandler = new SessionHandlerAdapter() {
        @Override
        public Object reply(SessionChannel channel, Object message) throws RemotingException {
            if (message instanceof Invocation) {
                Invocation inv = (Invocation) message;
                Invoker<?> invoker = getInvoker(channel, inv);
                Result result = invoker.invoke(inv);
                return result;
            }
            throw new RemotingException(channel, 
                    "Unsupported request: " + message == null ? null : (message.getClass().getName() + ": " + message) 
                    + ", channel: consumer: " + channel.getRemoteAddress() + " -> provider: " + channel.getLocalAddress());
        }
    };
    
    Invoker<?> getInvoker(Channel channel, Invocation inv) throws RemotingException{
        String group = inv.getAttachment(Constants.GROUP_KEY);
        String path = inv.getAttachment(Constants.PATH_KEY);
        String interfaceType = inv.getAttachment(Constants.INTERFACE_KEY);
        String version = inv.getAttachment(Constants.VERSION_KEY);
        String serviceKey = RpcHelper.serviceKey(group, path, interfaceType, version);        
        KubboExporter<?> exporter = (KubboExporter<?>) exporterMap.get(serviceKey);
        
        if (exporter == null)
            throw new RemotingException(channel, 
                    "Not found exported service: " + serviceKey + " in " + exporterMap.keySet() 
                    + ", may be (group, path, version) mismatch " 
                    + ", channel: consumer: " + channel.getRemoteAddress() + " -> provider: " + channel.getLocalAddress() 
                    + ", message:" + inv);

        return exporter.getInvoker();
    }
    
    public Collection<Invoker<?>> getInvokers() {
        return Collections.unmodifiableCollection(invokers);
    }
    
    public Collection<SessionServer> getServers() {
        return Collections.unmodifiableCollection(serverMap.values());
    }


    @Override
    public int getDefaultPort() {
        return DEFAULT_PORT;
    }

    @Override
    public <T> Exporter<T> export(Invoker<T> invoker) throws RpcException {
        String key = serviceKey(invoker);
        KubboExporter<T> exporter = new KubboExporter<T>(invoker, key, exporterMap);
        exporterMap.put(key, exporter);

        //open server
        openServer(invoker.getUrl());
        
        return exporter;
    }
    
    private void openServer(URL url) {
        String address = url.getAddress();
        SessionServer server = serverMap.get(address);
        if (server == null) {
            serverMap.put(address, createServer(url));
        } else {
            server.reset(url);
        }
    }
    
    private SessionServer createServer(URL url) {
        //默认开启server关闭时发送readonly事件
        url = url.addParameterIfAbsent(Constants.CHANNEL_SEND_READONLYEVENT_KEY, Constants.TRUE);        
        //默认开启heartbeat
        url = url.addParameterIfAbsent(Constants.HEARTBEAT_KEY, String.valueOf(Constants.DEFAULT_HEARTBEAT));
        //使用kubbocodec
        url = url.addParameter(Constants.CODEC_KEY, KubboCodec.NAME);
        
        //session bind
        try {
            SessionServer server = SessionLayers.bind(url, requestHandler);
            return server;
        } catch (RemotingException e) {
            throw new RpcException("Fail to start sesseion server(" + url + ") " + e.getMessage(), e);
        }
    }

    @Override
    public <T> Invoker<T> refer(Class<T> serviceType, URL url) throws RpcException {
        // create rpc invoker.
        KubboInvoker<T> invoker = new KubboInvoker<T>(serviceType, url, getClients(url), invokers);
        invokers.add(invoker);
        return invoker;
    }
    
    private SessionClient[] getClients(URL url){
        //是否共享连接
        boolean serviceShareConnection = false;
        //如果connections不配置，则共享连接，否则每服务每连接
        int connections = url.getParameter(Constants.CONNECTIONS_KEY, Constants.DEFAULT_CONNECTIONS);
        if (connections == 0){
            serviceShareConnection = true;
            connections = 1;
        }
        
        SessionClient[] clients = new SessionClient[connections];
        for (int i = 0; i < clients.length; i++) {
            if (serviceShareConnection){
                clients[i] = getSharedClient(url);
            } else {
                clients[i] = initClient(url);
            }
        }
        return clients;
    }
    
    /**
     *获取共享连接 
     * @param url server url
     * @return SessionClient
     */
    private SessionClient getSharedClient(URL url){
        String key = url.getAddress();
        ReferenceCountSessionClient client = referenceClientMap.get(key);
        if ( client != null ){
            if ( !client.isClosed()){
                client.incrementAndGetCount();
                return client;
            } else {
                referenceClientMap.remove(key);
            }
        }
        SessionClient exchagneclient = initClient(url);
        
        client = new ReferenceCountSessionClient(exchagneclient);
        referenceClientMap.put(key, client);
        return client; 
    }

    /**
     * 创建新连接.
     * @param url server url
     * @return SessionClient
     */
    private SessionClient initClient(URL url) {
        //默认开启heartbeat
        url = url.addParameterIfAbsent(Constants.HEARTBEAT_KEY, String.valueOf(Constants.DEFAULT_HEARTBEAT));

        //使用kubbocodec
        url = url.addParameter(Constants.CODEC_KEY, KubboCodec.NAME);
        
        //open client
        try {
            SessionClient client = SessionLayers.connect(url ,requestHandler);
            return client;
        } catch (RemotingException e) {
            throw new RpcException("Fail to create session client for service(" + url + "): " + e.getMessage(), e);
        }
        
    }

    @Override
    public void destroy() {
        //stop client
        for (String key : new ArrayList<String>(referenceClientMap.keySet())) {
            SessionClient client = referenceClientMap.remove(key);
            if (client != null) {
                try {
                    if (logger.isInfoEnabled()) {
                        logger.info("Close kubbo connection: " + client.getLocalAddress() + "->" + client.getRemoteAddress());
                    }
                    client.close();
                } catch (Throwable t) {
                    logger.warn(t.getMessage(), t);
                }
            }
        }
        //stop server
        for (String key : new ArrayList<String>(serverMap.keySet())) {
            SessionServer server = serverMap.remove(key);
            if (server != null) {
                try {
                    if (logger.isInfoEnabled()) {
                        logger.info("Close kubbo server: " + server.getLocalAddress());
                    }
                    server.close(getServerShutdownTimeout());
                } catch (Throwable t) {
                    logger.warn(t.getMessage(), t);
                }
            }
        }
        super.destroy();
    }
}