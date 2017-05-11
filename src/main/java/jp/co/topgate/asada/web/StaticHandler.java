package jp.co.topgate.asada.web;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 静的なコンテンツの配信を行うハンドラー
 *
 * @author asada
 */
public class StaticHandler extends Handler {

    /**
     * レスポンスを返すときに呼び出すメソッド
     *
     * @param os SocketのOutputStream
     */
    @Override
    public void returnResponse(OutputStream os) {
        try {
            String path = "";
            if (requestLine != null) {
                path = Handler.getFilePath(requestLine.getUri());
            }
            new ResponseMessage(os, statusCode, path);

        } catch (IOException e) {

        }
    }
}
