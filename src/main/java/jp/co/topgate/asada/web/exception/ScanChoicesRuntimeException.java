package jp.co.topgate.asada.web.exception;

/**
 * Created by yusuke-pc on 2017/04/19.
 */
public class ScanChoicesRuntimeException extends RuntimeException {
    @Override
    public String getMessage() {
        return "想定されていない文字が入力されました";
    }
}
