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
    private ResourceFileType rft = null;
    private RequestMessage requestMessage = null;
    private ResponseMessage responseMessage = null;
    private File resource = null;

    public void requestComes(InputStream is, OutputStream os){
        requestMessage = new RequestMessage();
        responseMessage = new ResponseMessage();
//        String method = null;

        String statusCode;
        if(requestMessage.parse(is)){
//            method = requestMessage.getMethod();
            resource = new File(FILE_PATH + requestMessage.getUri());
            if(resource.exists()){
                if(resource.isFile()){
                    //ここでファイルの拡張子が登録されているかのチェック
                    rft = new ResourceFileType(requestMessage.getUri());
                    if(rft.isRegistered()){
                        statusCode = STATUS_OK;
                    }else{
                        //登録されていない拡張子が要求された
                        statusCode = null;
                    }
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
                responseMessage.addHeader("Content-Type", rft.getContentType());
                responseMessage.returnResponse(os, rft);
                break;

            case STATUS_BAD_REQUEST:

                responseMessage.setProtocolVersion("HTTP/1.1");
                responseMessage.setStatusCode("400");
                responseMessage.setReasonPhrase("Bad Request");
                responseMessage.addHeader("Content-Type", "text/html");
                s = "<html><head><title>400 Bad Request</title></head>" +
                        "<body><h1>Bad Request</h1>" +
                        "<p>Your browser sent a request that this server could not understand.<br /></p></body></html>";
                if(responseMessage.returnResponse(os, s)){

                }else{
                    System.out.println("returnResponseメソッドの引数Stringがnullだった");
                }
                break;

            case STATUS_NOT_FOUND:

                responseMessage.setProtocolVersion("HTTP/1.1");
                responseMessage.setStatusCode("404");
                responseMessage.setReasonPhrase("Not Found");
                responseMessage.addHeader("Content-Type", "text/html");
                s = "<html><head><title>404 Not Found</title></head>" +
                        "<body><h1>Not Found</h1>" +
                        "<p>お探しのページは見つかりませんでした。<br /></p></body></html>";
                if(!responseMessage.returnResponse(os, s)){

                }else{
                    System.out.println("returnResponseメソッドの引数Stringがnullだった");
                }
                responseMessage.returnResponse(os, s);
                break;

            default:
                System.out.println("ResoureFileTypeクラスに存在しないファイルの拡張子が要求されました");
        }
    }
}
