package jp.co.topgate.asada.web;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

/**
 * Created by yusuke-pc on 2017/04/12.
 */
public class RequestMessage {
    String method = null;
    String uri = null;
    String uriQuery = null;
    String protocolVersion = null;
    HashMap<String, String> headerFielduri = null;
    HashMap<String, String> messageBodyuri = null;

    public void parse(InputStream requestMessage) throws IOException{
        System.out.println("パースをします");
        System.out.println(requestMessage.read());
        headerFielduri.put("例えば","こんな感じ");
    }
    public String getMethod() {
        return method;
    }
    public String getUri() {
        return uri;
    }
    public String findUriQuery(String name) {
        return uriQuery;
    }
    public String getProtocolVersion(String name) {
        return protocolVersion;
    }
    public String findHeaderByName(String fieldName) {
        String result = headerFielduri.get(fieldName);
        return result;
    }
    public String findMessageBody(String key) {
        String result = messageBodyuri.get(key);
        return result;
    }
}
