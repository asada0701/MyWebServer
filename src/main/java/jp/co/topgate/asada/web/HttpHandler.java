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
     * リソースファイルのパス
     */
    private static final String FILE_PATH = "./src/main/resources";

    /**
     * コンストラクタ
     * レスポンス生成中に発生したエラーはここで消す
     *
     * @param is ソケットの入力ストリーム
     * @param os ソケットの出力ストリーム
     */
    public HttpHandler(InputStream is, OutputStream os) {
        ResourceFile rf = null;
        RequestMessage requestMessage;

        int statusCode = -1;
        try {
            requestMessage = new RequestMessage(is);

            if (!"GET".equals(requestMessage.getMethod()) && !"POST".equals(requestMessage.getMethod())) {
                statusCode = ResponseMessage.NOT_IMPLEMENTED;
            }

            if (!"HTTP/1.1".equals(requestMessage.getProtocolVersion())) {
                statusCode = ResponseMessage.HTTP_VERSION_NOT_SUPPORTED;
            }

            rf = new ResourceFile((FILE_PATH + requestMessage.getUri()));
            if (!rf.exists() || !rf.isFile()) {
                statusCode = ResponseMessage.NOT_FOUND;
            }

            if (statusCode == -1) {
                statusCode = ResponseMessage.OK;
            }
        } catch (RequestParseException e) {
            statusCode = ResponseMessage.BAD_REQUEST;
        }

        try {
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
