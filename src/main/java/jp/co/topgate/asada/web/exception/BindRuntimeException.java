package jp.co.topgate.asada.web.exception;

/**
 * Created by yusuke-pc on 2017/04/24.
 */
public class BindRuntimeException extends RuntimeException {
    @Override
    public String getMessage() {
        return "the bind error occurred..";
    }
}
