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
        }

        try {
            //returnResponseメソッドの共通化、メソッドが共通だと、ミスにも早く気づける
            ResponseMessage responseMessage = new ResponseMessage();
            if (rf != null && statusCode == ResponseMessage.OK) {
                responseMessage.returnResponse(os, statusCode, rf);
            } else {
                responseMessage.returnErrorResponse(os, statusCode);
            }
        } catch (IOException e) {
        }
    }
}
