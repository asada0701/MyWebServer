package jp.co.topgate.asada.web;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yusuke-pc on 2017/04/25.
 */
public class Test {
    public static void main(String[] args){
        Map<String, String> fileType = new HashMap<>();

        fileType.put("htm", "text/html; charset=UTF-8");
        fileType.put("html", "text/html; charset=UTF-8");
        fileType.put("css", "text/css");
        fileType.put("js", "text/javascript");

        fileType.put("txt", "text/plain");

        fileType.put("jpg", "image/jpg");
        fileType.put("jpeg", "image/jpeg");
        fileType.put("png", "image/png");
        fileType.put("gif", "image/gif");

        String str = "....f.s.f.txt";

        for(String key : fileType.keySet()){
            if(str.endsWith(key)){
                System.out.println(fileType.get(key));
            }
        }
    }
}
