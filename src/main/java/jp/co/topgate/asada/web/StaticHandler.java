package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.app.Handler;
import jp.co.topgate.asada.web.exception.RequestParseException;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 静的なコンテンツの配信を行うハンドラー
 *
 * @author asada
 */
public class StaticHandler extends Handler {

    /**
     * コンストラクタ
     *
     * @param requestMessage リクエストメッセージのオブジェクト
     */
    public StaticHandler(RequestMessage requestMessage) {
        this.requestMessage = requestMessage;
    }

    @Override
    public StatusLine requestComes() {
        try {
            String method = requestMessage.getMethod();
            String uri = requestMessage.getUri();
            String protocolVersion = requestMessage.getProtocolVersion();

            if (!"HTTP/1.1".equals(protocolVersion)) {
                return StatusLine.HTTP_VERSION_NOT_SUPPORTED;

            } else if (!"GET".equals(method) && !"POST".equals(method)) {
                return StatusLine.NOT_IMPLEMENTED;

            } else {
                File file = new File(Handler.getFilePath(uri));
                if (!file.exists() || !file.isFile()) {
                    return StatusLine.NOT_FOUND;
                } else {
                    return StatusLine.OK;
                }
            }
        } catch (RequestParseException e) {
            return StatusLine.BAD_REQUEST;
        }
    }

    /**
     * レスポンスを返すときに呼び出すメソッド
     *
     * @param os SocketのOutputStream
     */
    @Override
    public void returnResponse(OutputStream os, StatusLine sl) {
        try {
            String path = "";
            if (requestMessage != null) {
                path = Handler.getFilePath(requestMessage.getUri());
            }
            new ResponseMessage(os, sl, path);

        } catch (IOException e) {

        }
    }
}
