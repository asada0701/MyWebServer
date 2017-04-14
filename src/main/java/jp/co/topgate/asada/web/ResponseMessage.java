package jp.co.topgate.asada.web;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by yusuke-pc on 2017/04/12.
 */
public class ResponseMessage {
    public static final String HEADER_FIELD_COLON = ": ";
    public static final String FILE_PATH = "./src/test/java/jp/co/topgate/asada/web/Documents";
    private String protocolVersion = null;
    private String statusCode = null;
    private String reasonPhrase = null;
    private ArrayList<String> headerField = new ArrayList<>();
    private File messageBody = null;

    public void setProtocolVersion(String protocolVersion){
        this.protocolVersion = protocolVersion;
    }

    public void setStatusCode(String statusCode){
        this.statusCode = statusCode;
    }

    public void setReasonPhrase(String reasonPhrase){
        this.reasonPhrase = reasonPhrase;
    }

    public void addHeader(String name,String value){
        headerField.add(name + HEADER_FIELD_COLON +  value);
    }

    public boolean setMessageBody(String uri){
        //文字ストリームなのかバイトストリームなのかを特定しても結局使うのはバイトストリームじゃん！！
        messageBody = new File(FILE_PATH + uri);
        return false;
    }

    public OutputStream getResposeMessage(OutputStream os){
        DataOutputStream dos = new DataOutputStream(os);
        try{
            dos.writeBytes(protocolVersion);
            dos.writeBytes(" ");
            dos.writeBytes(statusCode);
            dos.writeBytes(" ");
            dos.writeBytes(reasonPhrase);
            dos.writeChar('\n');        //レスポンスラインの改行
            for(String s : headerField) {
                dos.writeBytes(s);
                dos.writeChar('\n');    //ヘッダーフィールドの改行でもありメッセージボディ前の空行でもある
            }
            dos.flush();
        }catch(IOException e){
            e.printStackTrace();
        }finally {
            try {
                dos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return os;
    }

    //テスト用ゲッター
    String getProtocolVersion(){
        return this.protocolVersion;
    }
    String getStatusCode(){
        return this.statusCode;
    }
    String getReasonPhrase(){
        return this.reasonPhrase;
    }
    ArrayList<String> getHeaderField() {
        return this.headerField;
    }
}
