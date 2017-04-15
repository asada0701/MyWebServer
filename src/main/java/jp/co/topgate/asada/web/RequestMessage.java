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
    public static final String URI_QUESTION_MARK = "\\?";   //URIのクエリー前のクエスチョンマーク
    public static final String URI_QUERY_AMPERSAND = "&";   //URIのクエリー内のアンパサンド
    public static final String URI_QUERY_EQUAL = "=";       //URIのクエリーの中のイコール
    public static final int URI_QUERY_LENGTH = 2;           //URIのクエリーの項目数
    public static final String HEADER_FIELD_COLON = ":";    //ヘッダーフィールドのコロン（その後のスペースは自由のため注意）
    public static final int HEADER_FIELD_LENGTH = 2;        //ヘッダーフィールドの項目数
    public static final String MESSAGE_BODY_AMPERSAND = "&";//メッセージボディのアンパサンド
    public static final String MESSAGE_BODY_EQUAL = "=";    //メッセージボディのイコール
    public static final int MESSAGE_BODY_LENGTH = 2;        //メッセージボディの項目数
    private String method = null;
    private String uri = null;
    private HashMap<String, String> uriQuery = new HashMap<>();
    private String protocolVersion = null;
    private HashMap<String, String> headerFieldUri = new HashMap<>();
    private HashMap<String, String> messageBody = new HashMap<>();

    public boolean parse(InputStream requestMessage) {
        boolean isParseOK = true;
        BufferedReader br = new BufferedReader(new InputStreamReader(requestMessage));
        try{
            String str = br.readLine();                                             //一行目は先に読んでしまう。
            if(str != null){                                                        //nullチェック
                String[] requestLine = str.split(REQUEST_LINE_SPACE);               //リクエストライン
                if(requestLine.length == REQUEST_LINE_LENGTH){
                    method = requestLine[0];
                    uri = requestLine[1];
                    if(uri.equals("/")){                                            //"/"を要求された場合の処理
                        uri = "/index.html";
                    }
                    protocolVersion = requestLine[2];
                    //System.out.println(method + " " + uri + " " + protocolVersion);
                    while(!(str = br.readLine()).equals("")){
                        //System.out.println(str);
                        String[] header = str.split(HEADER_FIELD_COLON);            //ヘッダーフィールド
                        if(header.length == HEADER_FIELD_LENGTH) {
                            if (header[1].charAt(0) == ' ') {
                                header[1] = header[1].substring(1, header[1].length());
                            }
                            headerFieldUri.put(header[0],header[1]);                //ポート番号の処理
                        }else if(header.length > HEADER_FIELD_LENGTH){
                            if (header[1].charAt(0) == ' ') {
                                header[1] = header[1].substring(1, header[1].length());
                            }
                            headerFieldUri.put(header[0],header[1] + HEADER_FIELD_COLON + header[2]);
                        }else{
                            /*400バッドリクエスト対象、ヘッダーフィールドにコロンが入っていない*/
                            isParseOK = false;
                        }
                    }

                    //ここからは動的なページの対応処理

                    if("HOST".equals(getMethod())) {
                        //メソッドがHOSTの場合メッセージボディの処理を行う
                        while(!(str = br.readLine()).equals("")) {
                            String[] s1 = str.split(MESSAGE_BODY_AMPERSAND);
                            for (int i = 0; i < s1.length; i++) {
                                String[] s2 = s1[i].split(MESSAGE_BODY_EQUAL);
                                if (s2.length == MESSAGE_BODY_LENGTH) {
                                    messageBody.put(s2[0], s2[1]);
                                } else {
                                /*400バッドリクエスト対象、ヘッダーフィールドにコロンが入っていない*/
                                    isParseOK = false;
                                }
                            }
                        }
                    }else if("GET".equals(getMethod())){
                        String[] s1 = uri.split(URI_QUESTION_MARK);                 //uriのパース
                        uri = s1[0];     //これで純粋なuriになった
                        if(uri.equals("/")){                                        //"/"を要求された場合の処理
                            uri = "/index.html";
                        }
                        if(s1.length > 1){
                            String[] s2 = s1[1].split(URI_QUERY_AMPERSAND);
                            for(int k = 0; k < s2.length; k++){
                                String[] s3 = s2[k].split(URI_QUERY_EQUAL);
                                if(s3.length == URI_QUERY_LENGTH){
                                    uriQuery.put(s3[0], s3[1]);
                                }else{
                                    /*400バッドリクエスト、イコールが含まれないクエリーが存在してしまった*/
                                    isParseOK = false;
                                }
                            }
                        }
                    }

                }else{
                    /*400バッドリクエスト、リクエストラインに誤りがあった*/
                    isParseOK = false;
                }
            }else{
                /*400バッドリクエスト、リクエストメッセージが空なんですが？*/
                isParseOK = false;
            }
        }catch(IOException e){
            e.printStackTrace();
            /*400バッドリクエスト、br.readLine()ができなかった*/
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
        String result = messageBody.get(key);
        return result;
    }
}
