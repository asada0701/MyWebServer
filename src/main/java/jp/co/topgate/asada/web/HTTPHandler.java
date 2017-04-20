package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.exception.ErrorResponseRuntimeException;
import jp.co.topgate.asada.web.exception.FileNotRegisteredRuntimeException;
import jp.co.topgate.asada.web.exception.RequestParseRuntimeException;
import jp.co.topgate.asada.web.exception.ResourceFileRuntimeException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * HTTPのハンドラークラス
 *
 * @author asada
 */
class HTTPHandler {
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
    void requestComes(InputStream is, OutputStream os) {
        ResourceFile rf = null;
        int statusCode;
        try {
            RequestMessage requestMessage = new RequestMessage(is);
            if (ResourceFile.isRegistered(FILE_PATH + requestMessage.getUri())) {
                //登録済み
                rf = new ResourceFile(FILE_PATH + requestMessage.getUri());
                statusCode = ResponseMessage.STATUS_OK;
            } else {
                //登録されていない
                statusCode = -1;
            }

        } catch (FileNotRegisteredRuntimeException e) {
            e.printStackTrace();
            //未定
            statusCode = -1;

        } catch (NullPointerException | ResourceFileRuntimeException e) {
            statusCode = ResponseMessage.STATUS_NOT_FOUND;

        } catch (IOException | RequestParseRuntimeException e) {
            statusCode = ResponseMessage.STATUS_BAD_REQUEST;
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
        if (statusCode == ResponseMessage.STATUS_OK) {
            try {
                responseMessage.returnResponse(os, statusCode, rf);
            } catch (IOException e) {
                e.printStackTrace();                                                //err処理どうする？
            }
        } else if (statusCode == -1) {

        } else {
            try {
                responseMessage.returnErrorResponse(os, statusCode);
            } catch (ErrorResponseRuntimeException e) {
                e.printStackTrace();                                                //err
            }
        }
    }
}
