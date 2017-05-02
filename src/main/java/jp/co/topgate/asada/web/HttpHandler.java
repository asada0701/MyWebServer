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
public abstract class HttpHandler {

    /**
     * コンストラクタ
     * レスポンス生成中に発生したエラーはここで消す
     *
     * @param is ソケットの入力ストリーム
     * @param os ソケットの出力ストリーム
     */
    public HttpHandler(InputStream is, OutputStream os) throws IOException {
        if (is == null || os == null) {
            throw new IOException("引数のどちらかがnullだった");
        }
        ContentType ct = null;
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
                ct = new ContentType(uri);
                if (!ct.exists() || !ct.isFile()) {
                    statusCode = ResponseMessage.NOT_FOUND;
                } else {
                    statusCode = ResponseMessage.OK;
                }
            }

        } catch (RequestParseException e) {
            statusCode = ResponseMessage.BAD_REQUEST;
        } finally {
            if (ct == null) {
                ct = new ContentType("");
            }
        }


    }

    public void comesRequest(InputStream is) {
        try {
            new ResponseMessage(os, statusCode);
        } catch (IOException e) {

        }
    }
}
