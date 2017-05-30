package jp.co.topgate.asada.web;

/**
 * サーバーの状態をユーザーに伝える時に使うメッセージのEnum
 */
enum ServerMessage {

    /**
     * サーバーを立ち上げた
     */
    START("start up http server.."),

    /**
     * サーバーがすでに立ち上がっていたので、立ち上げれなかった
     */
    ALREADY_RUNNING("http server is already running.."),

    /**
     * サーバーを停止した
     */
    STOP("http server stops.."),

    /**
     * サーバーがすでに停止していたので、停止できなかった
     */
    ALREADY_STOP("http server is not running.."),

    /**
     * サーバーがクライアントにレスポンスをしている途中であるため、停止できなかった
     */
    CAN_NOT_STOP("wait a second, http server is returning a response.."),

    /**
     * サーバーを終了した
     */
    END("bye..");

    private final String message;

    ServerMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
