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
public class HTTPHandler {
    /**
     * リソースファイルのパス
     */
    private static final String FILE_PATH = "./src/main/resources";

    /**
     * リクエストがきた時に呼び出すメソッド
     * HTTPステータスコードをwriteResponseメソッドに渡す
     *
     * @param is ソケットの入力ストリーム
     * @param os ソケットの出力ストリーム
     */
    public void requestComes(InputStream is, OutputStream os) {
        ResourceFile rf = null;
        int statusCode;
        try {
            RequestMessage requestMessage = new RequestMessage(is);
            rf = new ResourceFile((FILE_PATH + requestMessage.getUri()));
            statusCode = ResponseMessage.OK;

        } catch (NotImplementedRuntimeException e) {
            statusCode = ResponseMessage.NOT_IMPLEMENTED;

        } catch (RequestParseRuntimeException e) {
            statusCode = ResponseMessage.BAD_REQUEST;

        } catch (NullPointerException | ResourceFileRuntimeException e) {
            statusCode = ResponseMessage.NOT_FOUND;

        } catch (HttpVersionNotSupportedRuntimeException e) {
            statusCode = ResponseMessage.HTTP_VERSION_NOT_SUPPORTED;
        }
        writeResponse(os, statusCode, rf);
    }

    /**
     * レスポンスメッセージをソケットの出力ストリームに書き出すメソッド
     *
     * @param os         ソケットの出力ストリーム
     * @param statusCode HTTPステータスコード
     * @param rf         ResourceFileのオブジェクト
     */
    private void writeResponse(OutputStream os, int statusCode, ResourceFile rf) {
        ResponseMessage responseMessage = new ResponseMessage();
        try {
            if (statusCode == ResponseMessage.OK) {
                responseMessage.returnResponse(os, statusCode, rf);
            } else {
                responseMessage.returnErrorResponse(os, statusCode);
            }
        } catch (IOException e) {
            //例外握り潰し！！
            //F5連打されると潰れる。ソケットサーバーが悪！！ワシは知らん！！
        }

    }
}
