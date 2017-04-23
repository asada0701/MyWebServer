package jp.co.topgate.asada.web.exception;

/**
 * Created by yusuke-pc on 2017/04/20.
 */
public class ResourceFileException extends RuntimeException {
    @Override
    public String getMessage() {
        return "存在しないファイルかもしくはディレクトリを指定されました";
    }
}
