/**
 * 
 */
package com.sogou.map.kubbo.common.http;

import java.io.IOException;

/**
 * @author liufuliang
 *
 */
public interface HttpRequest {
    HttpResponse execute() throws IOException;
}
