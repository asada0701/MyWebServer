package jp.co.topgate.asada.web;

import org.jetbrains.annotations.Contract;

/**
 * ステータスラインのEnum
 * ステータスコードとリーズンフレーズを用意する
 *
 * @author asada
 */
public enum StatusLine {
    /**
     * HTTPステータスコード:200
     */
    OK(200, "OK"),

    /**
     * HTTPステータスコード:400
     * リクエストが不正な場合
     */
    BAD_REQUEST(400, "Bad Request"),

    /**
     * HTTPステータスコード:404
     * リクエストで要求されたパスにファイルが存在しない
     */
    NOT_FOUND(404, "Not Found"),

    /**
     * HTTPステータスコード:500
     * サーバー内部のエラー（メッセージを保存していたCSVファイルがないなど）
     */
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),

    /**
     * HTTPステータスコード:501
     * リクエストのメソッドに対応していない
     */
    NOT_IMPLEMENTED(501, "Not Implemented"),

    /**
     * HTTPステータスコード:505
     * リクエストのプロトコルバージョンに対応していない
     */
    HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version Not Supported");

    private final int statusCode;

    private final String reasonPhrase;

    StatusLine(int statusCode, String reasonPhrase) {
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
    }

    /**
     * ステータスコードを取得するメソッド
     *
     * @return ステータスコードを返す
     */
    @Contract(pure = true)
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * リーズンフレーズを取得するメソッド
     *
     * @return リーズンフレーズを返す
     */
    @Contract(pure = true)
    public String getReasonPhrase() {
        return reasonPhrase;
    }
}
