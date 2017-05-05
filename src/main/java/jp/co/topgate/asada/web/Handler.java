package jp.co.topgate.asada.web;

import java.io.*;

/**
 * Created by yusuke-pc on 2017/05/01.
 */
public abstract class Handler {

    protected int statusCode;
    protected RequestLine requestLine;

    public void requestComes(BufferedInputStream bis) {
        if (requestLine != null) {
            String method = requestLine.getMethod();        //サーバーをスタートする前にアクセスすると、ここでヌルポする
            String uri = requestLine.getUri();
            String protocolVersion = requestLine.getProtocolVersion();

            if (!"HTTP/1.1".equals(protocolVersion)) {
                statusCode = ResponseMessage.HTTP_VERSION_NOT_SUPPORTED;

            } else if (!"GET".equals(method) && !"POST".equals(method)) {
                statusCode = ResponseMessage.NOT_IMPLEMENTED;

            } else {
                File file = new File(HandlerFactory.getFilePath(uri));
                if (!file.exists() || !file.isFile()) {
                    statusCode = ResponseMessage.NOT_FOUND;
                } else {
                    statusCode = ResponseMessage.OK;
                }
            }
        }
    }

    public void returnResponse(OutputStream os) {
        try {
            String path = "";
            if (requestLine != null) {
                path = HandlerFactory.getFilePath(requestLine.getUri());
            }
            new ResponseMessage(os, statusCode, path);
        } catch (IOException e) {
            //レスポンスメッセージ書き込み中のエラー、挽回無理
        }
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setRequestLine(RequestLine requestLine) {
        this.requestLine = requestLine;
    }
}
