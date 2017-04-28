package jp.co.topgate.asada.web;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yusuke-pc on 2017/04/28.
 */
public class HttpHandlerFactory {
    public static Map<String, String> urlPattern = new HashMap<>();
    static {
        urlPattern.put("/program/board/", "");
    }

    public static HttpHandler getHttpHandler(String uri){
        switch (uri){
            case urlPattern:
                break;
            default:
                return new StaticHttpHandler();
        }
    }
}
