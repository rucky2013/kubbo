/**
 * 
 */
package com.sogou.map.kubbo.common.http;


public enum Method {
    GET, 
    PUT, 
    POST, 
    OPTIONS, 
    HEAD, 
    DELETE, 
    TRACE, 
    CONNECT;


    public static boolean isBodyAble(Method method){
        return method == PUT || method == POST;
    }
}
