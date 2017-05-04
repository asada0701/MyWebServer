package jp.co.topgate.asada.web;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yusuke-pc on 2017/05/03.
 */
public class Test {
    /**
     * リソースファイルのパス
     */
    private static final String FILE_PATH = "./src/main/resources";

    /**
     * URIと実際のパスのハッシュマップ
     */
    private static Map<String, String> urlPattern = new HashMap<>();

    static {
        urlPattern.put("/program/board/", "/2/");
    }

    public static void main(String[] args) {
        String uri = "/index.html";
        //String uri = "/program/boar/css/style.css";

        for (String s : urlPattern.keySet()) {
            String[] s1 = s.split("/");
            String[] s2 = uri.split("/");
            int i1 = s1.length;
            int i2 = s2.length;

            boolean isMatch = true;
            if(i2 >= i1){
                for (int i = 0; i < i1; i++) {
                    if(!s1[i].equals(s2[i])){
                        isMatch = false;
                    }
                }
            }else{
                isMatch = false;
            }


            if (isMatch) {
                StringBuilder builder = new StringBuilder();
                for (int i = i1; i < i2; i++) {
                    if (i == i1) {
                        builder.append(s2[i]);
                    } else {
                        builder.append("/").append(s2[i]);
                    }
                }
                System.out.println(urlPattern.get(s) + builder.toString());
            }
        }
    }
}
