package jp.co.topgate.asada.web;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by yusuke-pc on 2017/04/12.
 */
public class HTTPHandler {
    public static final String FILE_PATH = "./src/main/java/jp/co/topgate/asada/web/Documents";
    private RequestMessage requestMessage = new RequestMessage();
    private ResponseMessage responseMessage = new ResponseMessage();
    private ResourceFileType rft = new ResourceFileType();

    public void requestComes(InputStream is, OutputStream os){
        if(requestMessage.parse(is)){
            File resource = new File(FILE_PATH + requestMessage.getUri());
            if(resource.exists()){
                //200:OK
                responseMessage.setProtocolVersion("HTTP/1.1");
                responseMessage.setStatusCode("200");
                responseMessage.setReasonPhrase("OK");
                responseMessage.setMessageBody(resource);

                if(rft.isTxt(requestMessage.getUri())){
                    responseMessage.addHeader("Content-Type", "text/html");
                    responseMessage.returnResponseChar(os);
                }else if(rft.isImg(requestMessage.getUri())){
                    responseMessage.returnResponseImage(os);
                }
            }else{
                //404:NotFound
                responseMessage.setProtocolVersion("HTTP/1.1");
                responseMessage.setStatusCode("404");
                responseMessage.setReasonPhrase("Not Found");
                responseMessage.addHeader("Content-Type", "text/html");
                String s = "<html><head><title>404 Not Found</title></head>" +
                        "<body><h1>Not Found</h1>" +
                        "<p>お探しのページは見つかりませんでした。<br /></p></body></html>";
                responseMessage.returnResponse(s,os);
            }
        }else{
            //400:BadRequest
            responseMessage.setProtocolVersion("HTTP/1.1");
            responseMessage.setStatusCode("400");
            responseMessage.setReasonPhrase("Bad Request");
            responseMessage.addHeader("Content-Type", "text/html");
            String s = "<html><head><title>400 Bad Request</title></head>" +
                    "<body><h1>Bad Request</h1>" +
                    "<p>Your browser sent a request that this server could not understand.<br /></p></body></html>";
            responseMessage.returnResponse(s,os);
        }
    }
}
