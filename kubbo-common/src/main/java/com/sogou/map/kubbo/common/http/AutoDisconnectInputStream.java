/**
 * 
 */
package com.sogou.map.kubbo.common.http;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author liufuliang
 *
 */
/**
 * Disconnect the underlying <code>HttpURLConnection</code> on close.
 */
public class AutoDisconnectInputStream extends FilterInputStream {

    /**
     * The underlying <code>HttpURLConnection</code>.
     */
    private final HttpConnection connection;

    /**
     * Creates an <code>AutoDisconnectInputStream</code>
     * by assigning the  argument <code>in</code>
     * to the field <code>this.in</code> so as
     * to remember it for later use.
     * @param connection the underlying connection to disconnect on close.
     * @param in the underlying input stream, or <code>null</code> if
     * this instance is to be created without an underlying stream.
     */
    public AutoDisconnectInputStream(final HttpConnection connection, final InputStream in) {
        super(in);
        this.connection = connection;
    }

    @Override
    public void close() throws IOException {
        try {
            if(in != null){
                super.close();
            }
        } finally {
            connection.close();
        }
    }
}
