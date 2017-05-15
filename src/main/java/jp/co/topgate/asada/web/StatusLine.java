package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.app.Handler;
import org.jetbrains.annotations.Contract;

import java.io.File;

/**
 * ステータスラインの列挙型
 *
 * @author asada
 */
public enum StatusLine {
    OK(200, "OK"),
    BAD_REQUEST(400, "Bad Request"),
    NOT_FOUND(404, "Not Found"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    NOT_IMPLEMENTED(501, "Not Implemented"),
    HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version Not Supported");

    private final int statusCode;

    private final String reasonPhrase;

    StatusLine(final int statusCode, final String reasonPhrase) {
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
    }

    @Contract(pure = true)
    public int getStatusCode() {
        return statusCode;
    }

    @Contract(pure = true)
    public String getReasonPhrase() {
        return reasonPhrase;
    }

    /**
     * リクエストメッセージのmethod,uri,protocolVersionから、レスポンスのステータスコードを決定するメソッド
     *
     * @param method          メソッド
     * @param uri             URI
     * @param protocolVersion プロトコルバージョン
     * @return StatusLineを返す
     */
    public static StatusLine getStatusLine(String method, String uri, String protocolVersion) {
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
    }
}
