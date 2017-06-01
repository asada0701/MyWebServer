package jp.co.topgate.asada.web.exception;

/**
 * CSVファイルが見つからない、読み書きできない時に発生する例外
 *
 * @author asada
 */
public class CsvRuntimeException extends Exception {
    public CsvRuntimeException(String message) {
        super(message);
    }

    public CsvRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
