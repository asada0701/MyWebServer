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

    public void returnResponseChar(OutputStream os) {
        PrintWriter pw = null;
        BufferedReader br = null;
        try {
            pw = new PrintWriter(os, true);
            StringBuilder builder = new StringBuilder();
            builder.append(protocolVersion + " " + statusCode + " " + reasonPhrase).append("\n");
            for(String s : headerField) {
                builder.append(s).append("\n");
            }
            builder.append("\n");
            br = new BufferedReader(new FileReader(messageBody));           //FileNotFoundExceptionが出てしまう
            String str;
            while((str = br.readLine()) != null) {
                builder.append(str);
            }
            pw.println(builder.toString());
        }catch(IOException e){
            e.printStackTrace();
        }finally {
            try{
                if(br != null){
                    br.close();
                }
                if(pw != null){
                    pw.close();
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    public void returnResponseImage(OutputStream os) {
        //createResponse(os);
    }

    public void returnResponse(String stringMessageBody, OutputStream os) {
        PrintWriter pw = null;
        pw = new PrintWriter(os, true);
        StringBuilder builder = new StringBuilder();
        builder.append(protocolVersion + " " + statusCode + " " + reasonPhrase).append("\n");
        for(String s : headerField) {
            builder.append(s).append("\n");
        }
        builder.append("\n");
        builder.append(stringMessageBody);
        pw.println(builder.toString());
        pw.close();
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
