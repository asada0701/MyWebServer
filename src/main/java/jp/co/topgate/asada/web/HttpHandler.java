package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.exception.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * HTTPのハンドラークラス
 *
 * @author asada
 */
public class HttpHandler {

    /**
     * リクエスト処理メソッド
     *
     * @param is ソケットの入力ストリーム
     */
    public void comesRequest(InputStream is, RequestLine rl) throws IOException {
        if (is == null) {
            throw new IOException("引数のどちらかがnullだった");
        }
        ResourceFile rf = null;
        RequestMessage requestMessage;

        int statusCode;
        try {
            requestMessage = new RequestMessage(is);

            String method = requestMessage.getMethod();
            String uri = requestMessage.getUri();
            String protocolVersion = requestMessage.getProtocolVersion();

            if (!"HTTP/1.1".equals(protocolVersion)) {
                statusCode = ResponseMessage.HTTP_VERSION_NOT_SUPPORTED;

            } else if (!"GET".equals(method) && !"POST".equals(method)) {
                statusCode = ResponseMessage.NOT_IMPLEMENTED;

            } else {
                rf = new ResourceFile(uri);
                if (!rf.exists() || !rf.isFile()) {
                    statusCode = ResponseMessage.NOT_FOUND;
                } else {
                    statusCode = ResponseMessage.OK;
                }
            }

        } catch (RequestParseException e) {
            statusCode = ResponseMessage.BAD_REQUEST;
        } finally {
            if (rf == null) {
                rf = new ResourceFile("");
            }
        }
    }

    public void returnResponse(OutputStream os, int statusCode) {
        try {
            new ResponseMessage(os, statusCode, null);
        } catch (IOException e) {

        }
    }
}
