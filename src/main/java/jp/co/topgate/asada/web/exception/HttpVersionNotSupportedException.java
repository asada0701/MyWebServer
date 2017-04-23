package jp.co.topgate.asada.web.exception;

/**
 * Created by yusukenakashima0701 on 2017/04/22.
 */
public class HttpVersionNotSupportedException extends RuntimeException {
    @Override
    public String getMessage() {
        return "HTTP/1.1以外のプロトコルバージョンでリクエストされました";
    }
}
