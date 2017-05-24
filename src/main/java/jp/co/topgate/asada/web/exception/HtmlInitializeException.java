package jp.co.topgate.asada.web.exception;

/**
 * Htmlの初期化に失敗。要求されたHtmlが存在しない
 *
 * @author asada
 */
public class HtmlInitializeException extends RuntimeException {
    public HtmlInitializeException(String message, Throwable cause) {
        super(message, cause);
    }
}
