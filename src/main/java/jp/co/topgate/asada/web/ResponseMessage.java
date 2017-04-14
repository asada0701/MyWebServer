package jp.co.topgate.asada.web;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by yusuke-pc on 2017/04/12.
 */
public class ResponseMessage {
    public static final String HEADER_FIELD_COLON = ": ";
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

    public void setMessageBody(File messageBody){
        this.messageBody = messageBody;
    }

    private void createResponse(OutputStream os){
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
    }

    public void returnResponseChar(OutputStream os) {
        DataOutputStream dos = new DataOutputStream(os);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(messageBody));
            String str;
            while((str = br.readLine()) != null){
                dos.writeBytes(str);
            }
        }catch(IOException e){
            e.printStackTrace();
        }finally {
            try{
                br.close();
                dos.close();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    public void returnResponseImage(OutputStream os) {
        createResponse(os);
    }

    public void returnResponse(String stringMessageBody, OutputStream os) {
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
            dos.writeBytes(stringMessageBody);
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
