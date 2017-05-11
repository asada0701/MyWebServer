package jp.co.topgate.asada.web.exception;

/**
 * 暗号化、復号の例外クラス
 * NoSuchPaddingException
 * InvalidAlgorithmParameterException
 * NoSuchAlgorithmException
 * IllegalBlockSizeException
 * BadPaddingException
 * InvalidKeyException
 * 以上の例外が、CipherHelperクラスのメソッドを使い、暗号化、復号した時に発生する可能性がある
 *
 * @author asada
 */
public class CipherRuntimeException extends RuntimeException {
    public CipherRuntimeException(String message) {
        super(message);
    }
}
