package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.app.Handler;
import jp.co.topgate.asada.web.exception.RequestParseException;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 静的なコンテンツの配信を行うハンドラー
 *
 * @author asada
 */
public class StaticHandler extends Handler {

    private RequestMessage requestMessage;

    private int statusCode;

    public StaticHandler(RequestMessage requestMessage) {
        this.requestMessage = requestMessage;
    }

    /**
     * リクエストが来たときに呼び出すメソッド
     *
     * @param bis SocketのInputStreamをBufferedInputStreamにラップして渡す
     */
    public void requestComes(BufferedInputStream bis) throws IOException {
        try {
            bis.reset();

            String method = requestMessage.getMethod();
            String uri = requestMessage.getUri();
            String protocolVersion = requestMessage.getProtocolVersion();

            if (!"HTTP/1.1".equals(protocolVersion)) {
                statusCode = ResponseMessage.HTTP_VERSION_NOT_SUPPORTED;

            } else if (!"GET".equals(method) && !"POST".equals(method)) {
                statusCode = ResponseMessage.NOT_IMPLEMENTED;

            } else {
                File file = new File(Handler.getFilePath(uri));
                if (!file.exists() || !file.isFile()) {
                    statusCode = ResponseMessage.NOT_FOUND;
                } else {
                    statusCode = ResponseMessage.OK;
                }
            }
        } catch (RequestParseException e) {
            statusCode = ResponseMessage.BAD_REQUEST;
        }
    }

    /**
     * レスポンスを返すときに呼び出すメソッド
     *
     * @param os SocketのOutputStream
     */
    @Override
    public void returnResponse(OutputStream os) {
        try {
            String path = "";
            if (requestMessage != null) {
                path = Handler.getFilePath(requestMessage.getUri());
            }
            new ResponseMessage(os, statusCode, path);

        } catch (IOException e) {

        }
    }
}
