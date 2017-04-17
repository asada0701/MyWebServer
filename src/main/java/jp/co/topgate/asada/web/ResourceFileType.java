package jp.co.topgate.asada.web;

import java.util.ArrayList;

/**
 * Created by yusuke-pc on 2017/04/15.
 */
public class ResourceFileType {
    private ArrayList<String> charFile = new ArrayList<>();
    private ArrayList<String> byteFile = new ArrayList<>();

    public ResourceFileType() {
        charFile.add("htm");
        charFile.add("html");
        charFile.add("css");
        charFile.add("js");
        charFile.add("txt");

        byteFile.add("jpg");
        byteFile.add("jpeg");
        byteFile.add("png");
        byteFile.add("gif");
    }
    public boolean isChar(String uri) {
        boolean result = false;
        for(String str : charFile) {
            if(uri != null && uri.endsWith(str)){
                result = true;
            }
        }
        return result;
    }
    public boolean isByte(String uri) {
        boolean result = false;
        for(String str : byteFile) {
            if(uri != null && uri.endsWith(str)){
                result = true;
            }
        }
        return result;
    }
}
