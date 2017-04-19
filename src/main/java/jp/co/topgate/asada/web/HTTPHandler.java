package jp.co.topgate.asada.web;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by yusuke-pc on 2017/04/12.
 */
class HTTPHandler {
    /**
     * リソースファイルのパス
     */
    private static final String FILE_PATH = "./src/main/resources/";

    /**
     * リクエストがきた場合に呼び出すメソッド
     *
     * @param is InputStream
     * @param os OutputStream
     */
    void requestComes(InputStream is, OutputStream os) {
        File resource = null;
        ResourceFileType rft = null;
        int statusCode;
        try {
            RequestMessage requestMessage = new RequestMessage(is);
            resource = new File(FILE_PATH + requestMessage.getUri());
            rft = new ResourceFileType(requestMessage.getUri());
            if (resource.exists() && resource.isFile() && rft.isRegistered()) {
                statusCode = ResponseMessage.STATUS_OK;
            } else {
                statusCode = ResponseMessage.STATUS_NOT_FOUND;
            }
        } catch (RequestParseRuntimeException e) {
            statusCode = ResponseMessage.STATUS_BAD_REQUEST;
        } catch (IOException e) {
            statusCode = ResponseMessage.STATUS_BAD_REQUEST;
        }

        ResponseMessage responseMessage = new ResponseMessage();
        if (statusCode == ResponseMessage.STATUS_OK) {
            responseMessage.returnResponse(os, statusCode, resource, rft);
        } else {
            responseMessage.returnErrorResponse(os, statusCode);
        }
    }
}
