package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.app.Handler;
import jp.co.topgate.asada.web.app.StatusLine;

import java.io.File;
import java.io.OutputStream;

/**
 * 静的なコンテンツの配信を行うハンドラー
 *
 * @author asada
 */
public class StaticHandler extends Handler {

    /**
     * リクエストメッセージ
     */
    private RequestMessage requestMessage;

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
     * @return レスポンスラインの状態行(StatusLine)を返す
     */
    @Override
    public final StatusLine doRequestProcess() {
        String method = requestMessage.getMethod();
        String uri = requestMessage.getUri();
        String protocolVersion = requestMessage.getProtocolVersion();

        return Handler.getStatusLine(method, uri, protocolVersion);
    }

    /**
     * レスポンスの処理を行うメソッド
     *
     * @param os SocketのOutputStream
     * @param sl ステータスラインの列挙型
     */
    @Override
    public void doResponseProcess(OutputStream os, StatusLine sl) {
        ResponseMessage rm;

        if (sl.equals(StatusLine.OK)) {
            String path = Handler.FILE_PATH + requestMessage.getUri();

            rm = new ResponseMessage(os, sl, path);
            ContentType ct = new ContentType(path);
            rm.addHeader("Content-Type", ct.getContentType());
            rm.addHeader("Content-Length", String.valueOf(new File(path).length()));

        } else {
            rm = new ResponseMessage(os, sl);
            rm.addHeader("Content-Type", "text/html; charset=UTF-8");
        }

        rm.returnResponse();
    }

    //テスト用
    RequestMessage getRequestMessage() {
        return this.requestMessage;
    }
}
