package jp.co.topgate.asada.web.exception;

/**
 * NoSuchPaddingException
 * InvalidAlgorithmParameterException
 * NoSuchAlgorithmException
 * IllegalBlockSizeException
 * BadPaddingException
 * InvalidKeyException
 * IOException
 * 以上の例外が、パスワードの暗号化したときに発生する例外。
 * Threadを継承したrunメソッド内で使用する
 *
 * @author asada
 */
public class CsvRuntimeException extends RuntimeException {
    @Override
    public String getMessage() {
        return "CSVファイルに書き込めできませんでした。CSVファイルを確認してください。";
    }
}
