package jp.co.topgate.asada.web.exception;

/**
 * CSVファイルが見つからない、読み書きできない時に発生する例外
 *
 * @author asada
 */
public class CsvRuntimeException extends RuntimeException {
    public CsvRuntimeException(String message) {
        super(message);
    }
}