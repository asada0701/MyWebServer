package jp.co.topgate.asada.web;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * パスワード暗号化クラス
 *
 * @author asada
 */
public class CipherHelper {
    /**
     * 暗号化方式「AES(Advanced Encryption Standard)」
     */
    private static final String algorithm = "AES";

    /**
     * 暗号化方式「AES」の場合、キーは16文字で
     */
    private static final String secretKey = "1234567890123456";

    /**
     * 暗号化方式「AES」の場合、初期化ベクトルは16文字で
     */
    private static final String initializationVector = "abcdefghijklmnop";

    private static final String FORMAT_OF_TRANSFORMATION = "%s/CBC/PKCS5Padding";

    /**
     * 暗号化メソッド
     *
     * @param originalSource
     * @return
     * @throws NoSuchAlgorithmException           ある暗号アルゴリズムが現在の環境で使用できない場合発生する
     * @throws NoSuchPaddingException             あるパディング・メカニズムが現在の環境で使用できない場合発生する
     * @throws InvalidKeyException                無効な鍵に対する例外
     * @throws IllegalBlockSizeException          提供されたデータの長さが暗号のブロック・サイズと一致しない場合発生する
     * @throws BadPaddingException                データが適切にパディングされない場合に発生する(暗号キーと複合キーが同じかチェックすること
     * @throws InvalidAlgorithmParameterException 無効なアルゴリズム・パラメータの例外
     */
    public static String encrypt(String originalSource)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException, InvalidAlgorithmParameterException {

        byte[] originalBytes = originalSource.getBytes();
        byte[] encryptBytes = CipherHelper.cipher(originalBytes, true);
        return Base64.getEncoder().encodeToString(encryptBytes);
    }

    /**
     * 複合化メソッド
     *
     * @param encryptBytesBase64String
     * @return
     * @throws NoSuchAlgorithmException           ある暗号アルゴリズムが現在の環境で使用できない場合発生する
     * @throws NoSuchPaddingException             あるパディング・メカニズムが現在の環境で使用できない場合発生する
     * @throws InvalidKeyException                無効な鍵に対する例外
     * @throws IllegalBlockSizeException          提供されたデータの長さが暗号のブロック・サイズと一致しない場合発生する
     * @throws BadPaddingException                データが適切にパディングされない場合に発生する(暗号キーと複合キーが同じかチェックすること
     * @throws InvalidAlgorithmParameterException 無効なアルゴリズム・パラメータの例外
     */
    public static String decrypt(String encryptBytesBase64String) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {

        byte[] encryptBytes = Base64.getDecoder().decode(encryptBytesBase64String);
        byte[] originalBytes = CipherHelper.cipher(encryptBytes, false);
        return new String(originalBytes);
    }

    /**
     * 暗号化/複合化の共通部分
     *
     * @throws InvalidAlgorithmParameterException
     */
    private static byte[] cipher(byte[] source, boolean isEncrypt) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {

        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), algorithm);
        IvParameterSpec iv = new IvParameterSpec(initializationVector.getBytes());
        Cipher cipher = Cipher.getInstance(String.format(FORMAT_OF_TRANSFORMATION, secretKeySpec.getAlgorithm()));
        if (isEncrypt) {
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv);
        } else {
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, iv);
        }

        return cipher.doFinal(source);
    }
}
