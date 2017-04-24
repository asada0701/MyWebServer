package jp.co.topgate.asada.web.exception;

/**
 * BindExceptionが発生した時に発生させる例外
 * BindExceptionをオーバーライドしたrunメソッド内で渡すため、Runtimeを継承したこの例外でラップ
 *
 * @author asada
 */
public class BindRuntimeException extends RuntimeException {
    @Override
    public String getMessage() {
        return "the bind error occurred..";
    }
}
