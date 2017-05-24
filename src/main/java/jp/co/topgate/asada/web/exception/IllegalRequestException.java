package jp.co.topgate.asada.web.exception;

/**
 * リクエストに対する処理の途中で発生する例外
 *
 * @author asada
 */
public class IllegalRequestException extends RuntimeException {
    public IllegalRequestException(String msg) {
        super(msg);
    }
}
