package jp.co.topgate.asada.web;

import java.io.File;
import java.io.OutputStream;

/**
 * 静的なコンテンツの配信を行うハンドラー
 *
 * @author asada
 */
public class StaticHandler extends Handler {

    /**
     * HTTPリクエストのプロトコルバージョン
     */
    private static final String PROTOCOL_VERSION = "HTTP/1.1";

    /**
     * HTTPリクエストのメソッド
     */
    private static final String METHOD = "GET";

    private RequestMessage requestMessage;

    /**
     * コンストラクタ
     *
     * @param requestMessage リクエストメッセージのオブジェクト
     */
    StaticHandler(RequestMessage requestMessage) {
        this.requestMessage = requestMessage;
    }

    /**
     * {@link Handler#handleRequest()}を参照
     *
     * @return ResponseMessageのオブジェクトを生成して返す。
     */
    @Override
    public ResponseMessage handleRequest() {
        String method = requestMessage.getMethod();
        String uri = requestMessage.getUri();
        String protocolVersion = requestMessage.getProtocolVersion();

        StatusLine statusLine = StaticHandler.decideStatusLine(method, uri, protocolVersion);

        ResponseMessage responseMessage;

        if (statusLine.equals(StatusLine.OK)) {
            String path = Handler.FILE_PATH + requestMessage.getUri();

            responseMessage = new ResponseMessage(statusLine, path);

            ContentType contentType = new ContentType(path);
            responseMessage.addHeaderWithContentType(contentType.getContentType());
            responseMessage.addHeader("Content-Length", String.valueOf(new File(path).length()));
            
        } else {
            responseMessage = new ResponseMessage(statusLine);
            responseMessage.addHeaderWithContentType(ContentType.ERROR_RESPONSE);
        }

        return responseMessage;
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
    static StatusLine decideStatusLine(String method, String uri, String protocolVersion) {
        if (!StaticHandler.PROTOCOL_VERSION.equals(protocolVersion)) {
            return StatusLine.HTTP_VERSION_NOT_SUPPORTED;
        }
        if (!StaticHandler.METHOD.equals(method)) {
            return StatusLine.NOT_IMPLEMENTED;
        }
        File file = new File(Handler.FILE_PATH + uri);
        if (!file.exists() || !file.isFile()) {
            return StatusLine.NOT_FOUND;
        }
        return StatusLine.OK;
    }

    //テスト用
    RequestMessage getRequestMessage() {
        return this.requestMessage;
    }
}
