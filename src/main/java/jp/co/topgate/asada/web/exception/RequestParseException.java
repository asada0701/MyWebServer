package jp.co.topgate.asada.web.exception;

/**
 * リクエストメッセージの例外
 * リクエストメッセージのパースに失敗した場合に発生する
 *
 * @author asada
 */
public class RequestParseException extends RuntimeException {
    public RequestParseException(String msg) {
        super(msg);
    }
}
