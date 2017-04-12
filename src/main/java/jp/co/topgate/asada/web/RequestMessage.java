package jp.co.topgate.asada.web;

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

    public void prarse(InputStream requestMessage){
        System.out.println("パースをします");
    }
    public String getMethod() {
        return null;
    }
    public String getUri() {
        return null;
    }
    public String findUriQuery(String name) {
        return null;
    }
    public String getProtocolVersion(String name) {
        return null;
    }
    public String findHeaderByName(String fieldName) {
        return null;
    }
    public String findMessageBody(String key) {
        return null;
    }
}
