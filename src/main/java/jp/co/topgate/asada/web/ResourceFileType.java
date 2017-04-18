package jp.co.topgate.asada.web;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by yusuke-pc on 2017/04/15.
 */
public class ResourceFileType {
    private static final String URI_DOT = "\\.";
    private static final int URI_DOT_LENGTH = 2;
    private static final String EXTENSION_SLASH = "/";
    private HashMap<String, String> fileType = new HashMap<>();
    private ArrayList<String> byteType = new ArrayList<>();
    private String uri_extension;

    public ResourceFileType(String uri) {
        if(uri != null){
            String[] s = uri.split(URI_DOT);
            if(s.length == URI_DOT_LENGTH) {
                uri_extension = s[1];
            }
            fileType.put("htm","text/html");
            fileType.put("html","text/html");
            fileType.put("css","text/css");
            fileType.put("js","text/javascript");

            fileType.put("txt","text/plain");

            fileType.put("jpg","image/jpg");
            fileType.put("jpeg","image/jpeg");
            fileType.put("png","image/png");
            fileType.put("gif","image/gif");

            //byteTypeにaddしておけば画像以外のバイトファイルを追加しても問題ない
            byteType.add("image");
        }
    }
    public boolean isRegistered(){
        return fileType.containsKey(uri_extension);
    }
    public boolean isByteFile() {
        boolean result = false;
        for (String s1 : fileType.keySet()) {
            String[] s2 = fileType.get(s1).split(EXTENSION_SLASH);
            for (String s3 : byteType){
                if(s3.equals(s2[0])){
                    result = true;
                }
            }
        }
        return result;
    }
    public String getContentType(){
        String result = fileType.get(uri_extension);
        return result;
    }
}
