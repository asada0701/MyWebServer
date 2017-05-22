package jp.co.topgate.asada.web;

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
     */
    @Override
    public final void doRequestProcess() {
        String method = requestMessage.getMethod();
        String uri = requestMessage.getUri();
        String protocolVersion = requestMessage.getProtocolVersion();

        this.statusLine = StaticHandler.getStatusLine(method, uri, protocolVersion);
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
     * @param outputStream SocketのOutputStreamを渡す
     */
    @Override
    public void doResponseProcess(OutputStream outputStream) {
        ResponseMessage responseMessage = new ResponseMessage();
        String path = Handler.FILE_PATH + requestMessage.getUri();

        if (statusLine.equals(StatusLine.OK)) {
            ContentType ct = new ContentType(path);
            responseMessage.addHeader("Content-Type", ct.getContentType());
            responseMessage.addHeader("Content-Length", String.valueOf(new File(path).length()));
        } else {
            responseMessage.addHeader("Content-Type", "text/html; charset=UTF-8");
        }

        responseMessage.returnResponse(outputStream, statusLine, path);
    }

    //テスト用
    RequestMessage getRequestMessage() {
        return this.requestMessage;
    }
}
