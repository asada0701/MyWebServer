package jp.co.topgate.asada.web;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by yusuke-pc on 2017/04/12.
 */
public class HTTPHandler {
    public static final String FILE_PATH = "./src/main/java/jp/co/topgate/asada/web/Documents";
    private static final String STATUS_OK = "200";
    private static final String STATUS_BAD_REQUEST = "400";
    private static final String STATUS_NOT_FOUND = "404";
    private static ResourceFileType rft = new ResourceFileType();

    public void requestComes(InputStream is, OutputStream os){
        RequestMessage requestMessage = new RequestMessage();
        ResponseMessage responseMessage = new ResponseMessage();
//        String method = null;
        String uri = null;
        String statusCode;
        File resource = null;

        if(requestMessage.parse(is)){
//            method = requestMessage.getMethod();
            uri = requestMessage.getUri();
            resource = new File(FILE_PATH + uri);
            if(resource.exists()){
                if(resource.isFile()){
                    statusCode = STATUS_OK;
                }else{
                    //ディレクトリを指定してきた
                    statusCode = STATUS_NOT_FOUND;
                }
            }else{
                //存在しないもの要求
                statusCode = STATUS_NOT_FOUND;
            }
        }else{
            //パース失敗
            statusCode = STATUS_BAD_REQUEST;
        }
//        switch (method) {
//            case "GET":
//            case "POST":
//            case "HEAD":
//            case "PUT":
//            case "DELETE":
//            default:
//        }

        String s;
        switch (statusCode) {
            case STATUS_OK:

                responseMessage.setProtocolVersion("HTTP/1.1");
                responseMessage.setStatusCode("200");
                responseMessage.setReasonPhrase("OK");
                responseMessage.setMessageBody(resource);

                if(rft.isChar(uri)){
                    //contentTypeがhtmlとcssで違うのを忘れてた
                    responseMessage.addHeader("Content-Type", rft.getCharContentType(uri));
                    responseMessage.returnResponseChar(os);
                }else if(rft.isByte(uri)){
                    responseMessage.addHeader("Content-Type", rft.getByteContentType(uri));
                    responseMessage.returnResponseByte(os);
                }else{
                    //
                }
                break;

            case STATUS_BAD_REQUEST:

                responseMessage.setProtocolVersion("HTTP/1.1");
                responseMessage.setStatusCode("400");
                responseMessage.setReasonPhrase("Bad Request");
                responseMessage.addHeader("Content-Type", "text/html");
                s = "<html><head><title>400 Bad Request</title></head>" +
                        "<body><h1>Bad Request</h1>" +
                        "<p>Your browser sent a request that this server could not understand.<br /></p></body></html>";
                responseMessage.returnResponse(s,os);
                break;

            case STATUS_NOT_FOUND:

                responseMessage.setProtocolVersion("HTTP/1.1");
                responseMessage.setStatusCode("404");
                responseMessage.setReasonPhrase("Not Found");
                responseMessage.addHeader("Content-Type", "text/html");
                s = "<html><head><title>404 Not Found</title></head>" +
                        "<body><h1>Not Found</h1>" +
                        "<p>お探しのページは見つかりませんでした。<br /></p></body></html>";
                responseMessage.returnResponse(s,os);
                break;
        }
    }
}
