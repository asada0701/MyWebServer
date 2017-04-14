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
    private HashMap<String, String> messageBodyUri = new HashMap<>();
    private boolean isRequestImage = false;

    public boolean parse(InputStream requestMessage) {
        boolean isParseOK = true;
        BufferedReader br = new BufferedReader(new InputStreamReader(requestMessage));
        try{
            String str = br.readLine(); //一行目は先に読んでしまう。
            if(str != null){
                String[] requestLine = str.split(REQUEST_LINE_SPACE);
                if(requestLine.length == REQUEST_LINE_LENGTH){
                    method = requestLine[0];
                    uri = requestLine[1];
                    protocolVersion = requestLine[2];
                    boolean isMessageBody = false;
                    while((str = br.readLine()) != null){
                        if(str.equals("")){
                            isMessageBody = true;   //空行に到達
                        }else{
                            if(!isMessageBody){
                                //ヘッダーフィールドのパース
                                String[] header = str.split(HEADER_FIELD_COLON);
                                if(header.length == HEADER_FIELD_LENGTH) {    //ヘッダーフィールドを一行ずつ処理
                                    if (header[1].charAt(0) == ' ') {
                                        header[1] = header[1].substring(1, header[1].length());
                                    }
                                    headerFieldUri.put(header[0],header[1]);        //ポート番号の処理
                                }else if(header.length > HEADER_FIELD_LENGTH){
                                    if (header[1].charAt(0) == ' ') {
                                        header[1] = header[1].substring(1, header[1].length());
                                    }
                                    headerFieldUri.put(header[0],header[1] + HEADER_FIELD_COLON + header[2]);
                                }else{
                                    /*400バッドリクエスト対象、ヘッダーフィールドにコロンが入っていない*/
                                    isParseOK = false;
                                }
                            }else{
                                //メッセージボディのパース
                                String[] s1 = str.split(MESSAGE_BODY_AMPERSAND);
                                for(int i = 0; i > s1.length; i++){
                                    String[] s2 = str.split(MESSAGE_BODY_EQUAL);
                                    if(s2.length == MESSAGE_BODY_LENGTH){
                                        messageBodyUri.put(s2[0], s2[1]);
                                    }else{
                                        /*400バッドリクエスト対象、ヘッダーフィールドにコロンが入っていない*/
                                        isParseOK = false;
                                    }
                                }
                            }
                        }
                    }

                    //URIのパース処理（ここはメソッドにするか悩ましいけどなんども呼ばれる訳じゃないのでメッソドにしない）
                    String[] s1 = uri.split(URI_QUESTION_MARK);             //s1でクエリーがあるかの判断となる
                    uri = s1[0];     //これで純粋なuriになった
                    if(uri.equals("/")){                                    //"/"を要求された場合の処理
                        uri = "/index.html";
                    }
                    if(s1.length > 1){
                        //クエリーがあった場合
                        String[] s2 = s1[1].split(URI_QUERY_AMPERSAND);     //s2でクエリーが複数かの判断となる
                        for(int k = 0; k < s2.length; k++){
                            String[] s3 = s2[k].split(URI_QUERY_EQUAL);     //クエリーの最小化に成功
                            if(s3.length == URI_QUERY_LENGTH){
                                uriQuery.put(s3[0], s3[1]);
                            }else{
                            /*400バッドリクエスト対象、イコールが含まれないクエリーが存在してしまった*/
                                isParseOK = false;
                            }
                        }
                    }

                }else{
                /*400バッドリクエスト対象、リクエストラインに誤りがあった*/
                    isParseOK = false;
                }
            }else{
                //リクエストメッセージが空なんですが？
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

    public boolean isRequestImage() {
        //URIが何で終わるのかをチェックすれば画像かどうかだってわかるはず
        String[] s = {"jpeg","png","gif"};
        for(String str : s) {
            if(uri.endsWith(str)){
                isRequestImage = true;
            }
        }
        return isRequestImage;
    }
}
