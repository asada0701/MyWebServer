package jp.co.topgate.asada.web;

import jdk.internal.util.xml.impl.Input;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by yusuke-pc on 2017/04/12.
 */
public class ResponseMessage {
    private static final String HEADER_FIELD_COLON = ": ";
    private String protocolVersion = null;
    private String statusCode = null;
    private String reasonPhrase = null;
    private ArrayList<String> headerField = new ArrayList<>();
    private File messageBody = null;

    public void setProtocolVersion(String protocolVersion){
        if(protocolVersion != null){
            this.protocolVersion = protocolVersion;
        }
    }

    public void setStatusCode(String statusCode){
        if(statusCode != null){
            this.statusCode = statusCode;
        }
    }

    public void setReasonPhrase(String reasonPhrase){
        if(reasonPhrase != null){
            this.reasonPhrase = reasonPhrase;
        }
    }

    public void addHeader(String name,String value){
        if(name != null && value != null){
            headerField.add(name + HEADER_FIELD_COLON +  value);
        }
    }

    public void setMessageBody(File messageBody){
        if(messageBody != null && messageBody.exists() && messageBody.isFile()){
            this.messageBody = messageBody;
        }
    }

    public void returnResponse(OutputStream os, ResourceFileType rft) {
        BufferedReader br = null;
        InputStream in = null;
        StringBuilder builder = new StringBuilder();
        try{
            builder.append(protocolVersion + " " + statusCode + " " + reasonPhrase).append("\n");
            for(String s : headerField) {
                builder.append(s).append("\n");
            }
            builder.append("\n");
            if(rft.isByteFile()){
                os.write(builder.toString().getBytes());
                in = new FileInputStream(messageBody);
                int num;
                while((num = in.read()) != -1) {
                    os.write(num);
                }
            }else{
                br = new BufferedReader(new FileReader(messageBody));
                String str;
                while((str = br.readLine()) != null){
                    builder.append(str);
                }
                os.write(builder.toString().getBytes());
            }
            os.flush();
        }catch(IOException e){
            e.printStackTrace();
        }finally {
            try{
                if(in != null){
                    in.close();
                }
                if(br != null){
                    br.close();
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    public boolean returnResponse(OutputStream os, String stringMessageBody) {
        boolean result = false;
        if(stringMessageBody != null){
            PrintWriter pw = new PrintWriter(os, true);
            StringBuilder builder = new StringBuilder();
            builder.append(protocolVersion + " " + statusCode + " " + reasonPhrase).append("\n");
            for(String s : headerField) {
                builder.append(s).append("\n");
            }
            builder.append("\n");
            builder.append(stringMessageBody);
            pw.println(builder.toString());
            pw.close();
            result = true;
        }
        return result;
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
    File getMessageBody(){
        return this.messageBody;
    }
}
