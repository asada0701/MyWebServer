package jp.co.topgate.asada.web;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by yusuke-pc on 2017/04/15.
 */
public class ResourceFileType {
    private static final String UIR_DOT = "\\.";
    private static final int UIR_DOT_LENGTH = 2;
    private HashMap<String, String> charFile = new HashMap<>();
    private HashMap<String, String> byteFile = new HashMap<>();

    public ResourceFileType() {
        charFile.put("htm","text/html");
        charFile.put("html","text/html");
        charFile.put("css","text/css");
        charFile.put("js","text/javascript");

        byteFile.put("jpg","image/jpg");
        byteFile.put("jpeg","image/jpeg");
        byteFile.put("png","image/png");
        byteFile.put("gif","image/gif");
    }
    public boolean isChar(String uri) {
        boolean result = false;
        if(uri != null) {
            String[] s = uri.split(UIR_DOT);
            if (s.length == UIR_DOT_LENGTH) {
                result = charFile.containsKey(s[1]);
            }
        }
        return result;
    }
    public String getCharContentType(String uri){
        String result = null;
        if(uri != null){
            String[] s = uri.split(UIR_DOT);
            if (s.length == UIR_DOT_LENGTH) {
                result = charFile.get(uri);
            }

        }
        return result;
    }
    public boolean isByte(String uri) {
        boolean result = false;
        if(uri != null){
            for(String s : byteFile.keySet()) {
                result = uri.endsWith(s);
            }
            String[] s = uri.split(UIR_DOT);
            if (s.length == UIR_DOT_LENGTH) {
                result = byteFile.containsKey(s[1]);
            }
        }
        return result;
    }
    public String getByteContentType(String uri){
        String result = null;
        if(uri != null){
            String[] s = uri.split(UIR_DOT);
            if(s.length == UIR_DOT_LENGTH){
                result = byteFile.get(uri);
            }
        }
        return result;
    }
}
