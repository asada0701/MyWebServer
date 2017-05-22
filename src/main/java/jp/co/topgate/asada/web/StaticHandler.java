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
     * @return レスポンスメッセージの状態行(StatusLine)を返す
     */
    @Override
    public final StatusLine doRequestProcess() {
        String method = requestMessage.getMethod();
        String uri = requestMessage.getUri();
        String protocolVersion = requestMessage.getProtocolVersion();

        return StaticHandler.getStatusLine(method, uri, protocolVersion);
    }

    /**
     * リクエストメッセージのmethod,uri,protocolVersionから、レスポンスのステータスコードを決定するメソッド
     * 1.プロトコルバージョンがHTTP/1.1以外の場合は505:HTTP Version Not Supported
     * 2.GET,POST以外のメソッドの場合は501:Not Implemented
     * 3.URIで指定されたファイルがリソースフォルダにない、もしくはディレクトリの場合は404:Not Found
     * 4.1,2,3でチェックして問題がなければ200:OK
     *
     * @param method          リクエストメッセージのメソッドを渡す
     * @param uri             URIを渡す
     * @param protocolVersion プロトコルバージョンを渡す
     * @return レスポンスメッセージの状態行(StatusLine)を返す
     */
    static StatusLine getStatusLine(String method, String uri, String protocolVersion) {
        if (!"HTTP/1.1".equals(protocolVersion)) {
            return StatusLine.HTTP_VERSION_NOT_SUPPORTED;

        } else if (!"GET".equals(method) && !"POST".equals(method)) {
            return StatusLine.NOT_IMPLEMENTED;

        } else {
            File file = new File(Handler.FILE_PATH + uri);
            if (!file.exists() || !file.isFile()) {
                return StatusLine.NOT_FOUND;
            }
        }
        return StatusLine.OK;
    }

    /**
     * レスポンスの処理を行うメソッド
     *
     * @param os SocketのOutputStreamを渡す
     * @param sl ステータスラインの列挙型を渡す
     */
    @Override
    public void doResponseProcess(OutputStream os, StatusLine sl) {
        ResponseMessage rm = new ResponseMessage();
        String path = Handler.FILE_PATH + requestMessage.getUri();

        if (sl.equals(StatusLine.OK)) {
            ContentType ct = new ContentType(path);
            rm.addHeader("Content-Type", ct.getContentType());
            rm.addHeader("Content-Length", String.valueOf(new File(path).length()));
        } else {
            rm.addHeader("Content-Type", "text/html; charset=UTF-8");
        }

        rm.returnResponse(os, sl, path);
    }

    //テスト用
    RequestMessage getRequestMessage() {
        return this.requestMessage;
    }
}
