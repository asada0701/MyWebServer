package jp.co.topgate.asada.web;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by yusuke-pc on 2017/04/12.
 */
public class HTTPHandler {
    public static final String FILE_PATH = "./src/main/java/jp/co/topgate/asada/web/Documents";

    public void requestComes(InputStream is, OutputStream os){
        RequestMessage requestMessage = new RequestMessage();
        ResponseMessage responseMessage = new ResponseMessage();
        if(requestMessage.parse(is)){
            //リクエストメッセージには問題なし
            responseMessage.setProtocolVersion("HTTP/1.1");
            responseMessage.setStatusCode("200");
            responseMessage.setReasonPhrase("OK");
            responseMessage.addHeader("Content-Type", "text/html;");

            responseMessage.setMessageBody(new File(FILE_PATH + requestMessage.getUri()));
            if(requestMessage.isRequestImage()){
                //画像だよ
                responseMessage.returnResponseImage(os);
            }else{
                //文字列だよ
                responseMessage.returnResponseChar(os);
            }
        }else{
            //400バッドリクエスト対象、リクエストメッセージ関連での異常
            responseMessage.setProtocolVersion("HTTP/1.1");
            responseMessage.setStatusCode("400");
            responseMessage.setReasonPhrase("Bad Request");
            responseMessage.addHeader("Date", "Fri, 14 Apr 2017 06:10:48 GMT");
            responseMessage.addHeader("Server", "SimpleServer/1.0 (Unix)");
            responseMessage.addHeader("content-Length", "226");
            responseMessage.addHeader("connection", "close");
            responseMessage.addHeader("Content-Type", "text/html; charset=iso-8859-1");

            String s = "<!DOCTYPE HTML><html><head><title>400 Bad Request</title></head>" +
                    "<body><h1>Bad Request</h1>" +
                    "<p>Your browser sent a request that this server could not understand.<br /></p></body></html>";
            responseMessage.returnResponse(s,os);
        }
    }
}
