package jp.co.topgate.asada.web.exception;

/**
 * POSTの処理を行なっている時に発生する例外
 */
public class DoPostException extends RuntimeException {
    public DoPostException(String msg) {
        super(msg);
    }
}
