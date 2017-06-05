package jp.co.topgate.asada.web.exception;

/**
 * リソースフォルダ以外のファイルをリクエストした場合に発生する例外
 *
 * @author asada
 */
public class FileForbiddenException extends Exception {
    public FileForbiddenException(String message) {
        super(message);
    }
}
