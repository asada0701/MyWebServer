package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.app.Handler;
import jp.co.topgate.asada.web.app.StatusLine;

import java.io.File;
import java.io.OutputStream;
import java.util.Objects;

/**
 * 静的なコンテンツの配信を行うハンドラー
 *
 * @author asada
 */
public class StaticHandler extends Handler {

    /**
     * リソースファイルのパス
     */
    private static final String FILE_PATH = "./src/main/resources";

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
     *
     * @param method          リクエストメッセージのメソッドを渡す
     * @param uri             URIを渡す
     * @param protocolVersion プロトコルバージョンを渡す
     * @return StatusLineを返す
     */
    static StatusLine getStatusLine(String method, String uri, String protocolVersion) {
        if (!"HTTP/1.1".equals(protocolVersion)) {
            return StatusLine.HTTP_VERSION_NOT_SUPPORTED;

        } else if (!"GET".equals(method) && !"POST".equals(method)) {
            return StatusLine.NOT_IMPLEMENTED;

        } else {
            File file = new File(StaticHandler.FILE_PATH + uri);
            if (!file.exists() || !file.isFile()) {
                return StatusLine.NOT_FOUND;
            } else {
                return StatusLine.OK;
            }
        }
    }

    /**
     * レスポンスを返すときに呼び出すメソッド
     *
     * @param os SocketのOutputStream
     * @param sl ステータスラインの列挙型
     * @throws NullPointerException 引数がnull
     */
    @Override
    public void doResponseProcess(OutputStream os, StatusLine sl) throws NullPointerException {
        Objects.requireNonNull(os);
        Objects.requireNonNull(sl);

        ResponseMessage rm;

        if (sl.equals(StatusLine.OK)) {
            String path = StaticHandler.FILE_PATH + requestMessage.getUri();
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
}
