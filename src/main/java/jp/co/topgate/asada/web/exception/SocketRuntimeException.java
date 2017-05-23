package jp.co.topgate.asada.web.exception;

/**
 * ServerSocketクラスやSocketクラスで発生したIOException
 *
 * @author asada
 */
public class SocketRuntimeException extends RuntimeException {
    public SocketRuntimeException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
