package jp.co.topgate.asada.web;

import java.util.ArrayList;

/**
 * Created by yusuke-pc on 2017/04/15.
 */
public class ResourceFileType {
    private ArrayList<String> txtFile = new ArrayList<>();
    private ArrayList<String> imgFile = new ArrayList<>();

    public ResourceFileType() {
        txtFile.add("html");
        txtFile.add("css");
        txtFile.add("js");

        imgFile.add("jpeg");
        imgFile.add("png");
        imgFile.add("gif");
    }
    public boolean isTxt(String uri) {
        boolean result = false;
        for(String str : txtFile) {
            if(uri.endsWith(str)){
                result = true;
            }
        }
        return result;
    }
    public boolean isImg(String uri) {
        boolean result = false;
        for(String str : imgFile) {
            if(uri.endsWith(str)){
                result = true;
            }
        }
        return result;
    }
}
