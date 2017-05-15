package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.app.Handler;
import jp.co.topgate.asada.web.app.StatusLine;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

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

    /**
     * リクエストの処理を行うメソッド
     *
     * @return StatusLineを返す
     */
    @Override
    public StatusLine requestComes() {
        String method = requestMessage.getMethod();
        String uri = requestMessage.getUri();
        String protocolVersion = requestMessage.getProtocolVersion();

        return StatusLine.getStatusLine(method, uri, protocolVersion);
    }

    /**
     * レスポンスを返すときに呼び出すメソッド
     *
     * @param os SocketのOutputStream
     * @param sl StatusLineを渡す
     * @throws NullPointerException 引数がnull
     */
    @Override
    public void returnResponse(OutputStream os, StatusLine sl) throws NullPointerException {
        Objects.requireNonNull(os);
        Objects.requireNonNull(sl);

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
