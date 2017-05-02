package jp.co.topgate.asada.web;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yusuke-pc on 2017/05/02.
 */
public class Test2 {

    private static Map<String, String> urlPattern = new HashMap<>();

    static {
        urlPattern.put("/program/board/", "/2/");
        urlPattern.put("/program/", "/test/");
    }

    public static void main(String[] args) {
        String uri = "/program/board/index.html";
        //String uri = "/program/index.html";

        String[] s = uri.split("/");
        int slashNum = s.length;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < slashNum - 1; i++) {
            builder.append(s[i]).append("/");
        }

        if (urlPattern.containsKey(builder.toString())) {
            System.out.println(urlPattern.get(builder.toString()) + s[slashNum - 1]);
        }
    }
}
