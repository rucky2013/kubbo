package com.sogou.map.kubbo.rpc.echo;

/**
 * Echo service.
 * 
 * @author liufuliang
 */
public interface EchoService {

    /**
     * echo.
     * 
     * @param message message.
     * @return message.
     */
    Object $echo(Object message);

}