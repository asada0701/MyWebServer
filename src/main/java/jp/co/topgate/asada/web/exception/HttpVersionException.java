package jp.co.topgate.asada.web.exception;

/**
 * 送られてきたリクエストメッセージのバージョンがHTTP/1.1以外である場合に発生する例外
 */
public class HttpVersionException extends RuntimeException {
    private String protocolVersion;

    public HttpVersionException(String protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

    @Override
    public String getMessage() {
        return "このサーバーが対応しているのはHTTP/1.1のみです。リクエストメッセージのプロトコルバージョン:" + protocolVersion;
    }
}
