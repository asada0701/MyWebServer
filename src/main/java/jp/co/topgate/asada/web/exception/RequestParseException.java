package jp.co.topgate.asada.web.exception;

/**
 * Created by yusuke-pc on 2017/04/19.
 */
public class RequestParseException extends RuntimeException {
    @Override
    public String getMessage() {
        return "不正なリクエストメッセージをパースしようとしました";
    }
}
