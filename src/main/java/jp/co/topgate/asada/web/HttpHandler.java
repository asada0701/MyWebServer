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
        int statusCode;
        try {
            RequestMessage requestMessage = new RequestMessage(is);
            rf = new ResourceFile((FILE_PATH + requestMessage.getUri()));
            statusCode = ResponseMessage.OK;

        } catch (RequestParseException e) {
            statusCode = ResponseMessage.BAD_REQUEST;

        } catch (NullPointerException | ResourceFileException e) {
            statusCode = ResponseMessage.NOT_FOUND;

        } catch (NotImplementedException e) {
            statusCode = ResponseMessage.NOT_IMPLEMENTED;

        } catch (HttpVersionNotSupportedException e) {
            statusCode = ResponseMessage.HTTP_VERSION_NOT_SUPPORTED;
        }

        try {
            ResponseMessage responseMessage = new ResponseMessage();
            if (statusCode == ResponseMessage.OK) {
                responseMessage.returnResponse(os, statusCode, rf);
            } else {
                responseMessage.returnErrorResponse(os, statusCode);
            }
        } catch (IOException e) {
        }
    }
}
