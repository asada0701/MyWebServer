package jp.co.topgate.asada.web.exception;

/**
 * リクエストメッセージの例外
 * リクエストメッセージのパースに失敗した場合に発生する
 *
 * @author asada
 */
public class RequestParseException extends RuntimeException {
    @Override
    public String getMessage() {
        return "不正なリクエストメッセージをパースしようとしました";
    }
}
