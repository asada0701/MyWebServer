package jp.co.topgate.asada.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.HashMap;
import java.util.regex.PatternSyntaxException;

/**
 * Created by yusuke-pc on 2017/04/12.
 */
public class RequestMessage {
    public static final String REQUEST_LINE_SPACE = " ";    //リクエストラインのスペース
    public static final int REQUEST_LINE_LENGTH = 3;        //リクエスト行の項目数
    public static final String HEADER_BODY_COLON = ":";     //ヘッダーボディのコロン（その後のスペースは自由のため注意）
    public static final int HEADER_BODY_LENGTH = 2;         //１つのヘッダーボディの項目数
    public static final String URI_QUESTION_MARK = "\\?";     //URIのクエリー前のクエスチョンマーク
    public static final String URI_QUERY_AMPERSAND = "&";   //URIのクエリー内のアンパサンド
    public static final String URI_QUERY_EQUAL = "=";       //URIのクエリーの中のイコール
    public static final int URI_QUERY = 2;                  //URIのクエリーの項目数
    private String method = null;
    private String uri = null;
    private HashMap<String, String> uriQuery = new HashMap<>();
    private String protocolVersion = null;
    private HashMap<String, String> headerFieldUri = new HashMap<>();
    private HashMap<String, String> messageBodyUri = new HashMap<>();

    public boolean parse(InputStream requestMessage) {
        boolean isParseOK = true;
        BufferedReader br = new BufferedReader(new InputStreamReader(requestMessage));
        try{
            String str = br.readLine(); //一行目は先に読んでしまう。
            String[] requestLine = str.split(REQUEST_LINE_SPACE);
            if(requestLine.length == REQUEST_LINE_LENGTH){
                method = requestLine[0];
                uri = requestLine[1];
                protocolVersion = requestLine[2];
                while((str = br.readLine()) != null){
                    String[] header = str.split(HEADER_BODY_COLON);
                    if(header.length == HEADER_BODY_LENGTH){    //ヘッダーフィールドを一行ずつ処理
                        if(header[1].charAt(0) == ' '){
                            header[1] = header[1].substring(1,header[1].length());
                        }
                        headerFieldUri.put(header[0],header[1]);
                    }else{
                        /*400バッドリクエスト対象、ヘッダーフィールドに誤りがあった*/
                        isParseOK = false;
                    }
                }

                //URIのパース処理（ここはメソッドにするか悩ましいけどなんども呼ばれる訳じゃないのでメッソドにしない）
                String[] s1 = null;
                try{
                    s1 = uri.split(URI_QUESTION_MARK);             //s1でクエリーがあるかの判断となる
                }catch(PatternSyntaxException e){
                    e.printStackTrace();
                }

                uri = s1[0];     //これで純粋なuriになった
                if(s1.length > 0){
                    //クエリーがあった場合
                    for(int i = 1; i < s1.length; i++) {
                        String[] s2 = s1[i].split(URI_QUERY_AMPERSAND);     //s2でクエリーが複数かの判断となる
                        for(int k = 0; k < s2.length; k++){
                            String[] s3 = s2[k].split(URI_QUERY_EQUAL);     //クエリーの最小化に成功
                            if(s3.length == URI_QUERY){
                                uriQuery.put(s3[0], s3[1]);
                            }else{
                                /*400バッドリクエスト対象、イコールが含まれないクエリーが存在してしまった*/
                                isParseOK = false;
                            }
                        }
                    }
                }

            }else{
                /*400バッドリクエスト対象、リクエストラインに誤りがあった*/
                isParseOK = false;
            }
        }catch(IOException e){
            e.printStackTrace();
            /*400バッドリクエスト対象、br.readLine()ができなかった*/
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
        String result = uriQuery.get(name);
        return result;
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
