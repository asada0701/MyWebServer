package jp.co.topgate.asada.web.exception;

/**
 * Created by yusuke-pc on 2017/04/20.
 */
public class FileNotRegisteredRuntimeException extends RuntimeException {
    @Override
    public String getMessage() {
        return "ResourceFileクラスに登録されていない拡張子のファイルです";
    }
}
