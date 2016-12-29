/**
 * 
 */
package com.sogou.map.kubbo.common.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import com.sogou.map.kubbo.common.json.JSONArray;
import com.sogou.map.kubbo.common.json.JSONException;
import com.sogou.map.kubbo.common.json.JSONObject;

/**
 * @author liufuliang
 *
 */
public abstract class AbstractHttpResponse implements HttpResponse{
    int statusCode;
    String responseMessage;
    HttpConnection connection;
    InputStream inputStream;
    ChunkedHandler chunkedHandler;
    
    
    protected AbstractHttpResponse(HttpConnection connection, 
            int statusCode,
            String responseMessage,
            InputStream inputStream,
            ChunkedHandler chunkedHandler) {
        this.connection = connection;
        this.statusCode = statusCode;
        this.responseMessage = responseMessage;
        this.inputStream = inputStream;
        this.chunkedHandler = chunkedHandler;
    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }
    
    @Override
    public boolean isSuccess() {
        return (statusCode / 100) == 2; // 200, 201, 204, ...
    }

    @Override
    public String getResponseMessage() {
        return responseMessage;
    }
    
    @Override
    public InputStream getInputStream(){
        return inputStream;
    }
    /**
     * 
     */
    @Override
    public void setAutoDisconnect(){
        if(! (this.inputStream instanceof AutoDisconnectInputStream)){
            this.inputStream = new AutoDisconnectInputStream(connection, inputStream);
        }
    }
    
    /**
     * A shortcut to check for successful status codes and throw exception in
     * case of non-2xx status codes. <br>
     * there might be cases where you want to inspect the response-object
     * first (check header values) and then have a short exit where the
     * response-code is not suitable for further normal processing.
     */
    public AbstractHttpResponse success() throws KubboHttpException {
        if (!isSuccess()) {
            throw new KubboHttpException("Request failed: " + statusCode + " "
                    + responseMessage);
        }
        return this;
    }
    
    
    @SuppressWarnings("unchecked")
    public <T> T asType(Class<T> type) throws KubboHttpException{

        if (this.inputStream == null || type == Void.class) {
            return null;
        } else if (type == InputStream.class) {
            return (T) inputStream;
        }

        try{
            T result = null;
            byte[] responseBody = Https.readBytes(inputStream);
            // we are ignoring headers describing the content type of the response, instead
            // try to force the content based on the type the client is expecting it (clazz)
            if (type == String.class) {
                result = (T) new String(responseBody, Const.UTF8);
            } else if (type == Const.BYTE_ARRAY_CLASS) {
                result = (T) responseBody;
            } else if (type == JSONObject.class) {
                String json = new String(responseBody, Const.UTF8);
                result = (T) new JSONObject(json);	
            } else if (type == JSONArray.class) {
                String json = new String(responseBody, Const.UTF8);
                result = (T) new JSONArray(json);
            }
            return result;
        } catch(UnsupportedEncodingException e){
            throw new KubboHttpException(e);
        } catch (IOException e) {
            throw new KubboHttpException(e);
        } catch (JSONException e) {
            throw new KubboHttpException("payload is not a valid JSON object", e);
        } finally {
            try {
                this.inputStream.close();
            } catch (IOException e) {
            }
        }
    }

}
