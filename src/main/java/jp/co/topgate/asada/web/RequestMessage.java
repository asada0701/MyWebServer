package jp.co.topgate.asada.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * Created by yusuke-pc on 2017/04/12.
 */
public class RequestMessage {
    public static final int REQUEST_LINE_LENGTH = 3;    //リクエスト行の項目数
    public static final int HEADER_BODY_LENGTH = 2;     //１つのヘッダーボディの項目数
    String method = null;
    String uri = null;
    String uriQuery = null;
    String protocolVersion = null;
    HashMap<String, String> headerFieldUri = new HashMap<>();
    HashMap<String, String> messageBodyUri = new HashMap<>();

    public boolean parse(InputStream requestMessage) throws IOException{
        boolean isParseOK = true;
        BufferedReader br = new BufferedReader(new InputStreamReader(requestMessage));
        String str = br.readLine(); //一行目は先に読んでしまう。
        String[] requestLine = str.split(" ");  //一行目はスペースで分割する
        if(requestLine.length == REQUEST_LINE_LENGTH){
            method = requestLine[0];
            uri = requestLine[1];
            protocolVersion = requestLine[2];
            while((str = br.readLine()) != null){
                String[] header = str.split(": ");
                if(header.length == HEADER_BODY_LENGTH){
                    headerFieldUri.put(header[0],header[1]);
                }else{
                    System.out.println("不正なメッセージが送られました");
                    isParseOK = false;
                }
            }
        }else{
            System.out.println("不正なリクエストメッセージが送られました");
            isParseOK = false;
        }
        return isParseOK;
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String findUriQuery(String name) {
        return uriQuery;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public String findHeaderByName(String fieldName) {
        String result = headerFieldUri.get(fieldName);
        return result;
    }

    public String findMessageBody(String key) {
        String result = messageBodyUri.get(key);
        return result;
    }
}
