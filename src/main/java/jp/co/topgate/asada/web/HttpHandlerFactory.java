package jp.co.topgate.asada.web;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yusuke-pc on 2017/04/28.
 */
public class HttpHandlerFactory {
    public static List<String> urlPattern = new ArrayList<>();
    static {
        urlPattern.add("/program/board");
    }

    public static HttpHandler getHttpHandler(String uri){
        for(String s : urlPattern){
            if(s.equals(uri)){
                return new WebAppHandler();
            }
        }
        return new StaticHttpHandler();
    }
}
