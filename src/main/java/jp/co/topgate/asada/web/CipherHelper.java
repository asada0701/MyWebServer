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
class CipherHelper {
    /**
     * 暗号化方式「AES(Advanced Encryption Standard)」
     */
    private static final String algorithm = "AES";

    /**
     * 暗号化方式「AES」の場合、キーは16文字
     */
    private static final String secretKey = "1234567890123456";

    /**
     * 暗号化方式「AES」の場合、初期化ベクトルは16文字
     */
    private static final String initializationVector = "abcdefghijklmnop";

    private static final String FORMAT_OF_TRANSFORMATION = "%s/CBC/PKCS5Padding";

    /**
     * 暗号化メソッド
     *
     * @param original 暗号化したい文字
     * @return 暗号化された文字
     * @throws NoSuchAlgorithmException           ある暗号アルゴリズムが現在の環境で使用できない場合発生する
     * @throws NoSuchPaddingException             あるパディング・メカニズムが現在の環境で使用できない場合発生する
     * @throws InvalidKeyException                無効な鍵に対する例外
     * @throws IllegalBlockSizeException          提供されたデータの長さが暗号のブロック・サイズと一致しない場合発生する
     * @throws BadPaddingException                データが適切にパディングされない場合に発生する(暗号キーと複合キーが同じかチェックすること
     * @throws InvalidAlgorithmParameterException 無効なアルゴリズム・パラメータの例外
     */
    static String encrypt(String original)
            throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
            BadPaddingException, InvalidAlgorithmParameterException {

        byte[] originalBytes = original.getBytes();
        byte[] encryptBytes = CipherHelper.cipher(originalBytes, true);
        return Base64.getEncoder().encodeToString(encryptBytes);
    }

    /**
     * 複合化メソッド
     *
     * @param encrypt 複合化したい文字
     * @return 複合化された文字
     * @throws NoSuchAlgorithmException           ある暗号アルゴリズムが現在の環境で使用できない場合発生する
     * @throws NoSuchPaddingException             あるパディング・メカニズムが現在の環境で使用できない場合発生する
     * @throws InvalidKeyException                無効な鍵に対する例外
     * @throws IllegalBlockSizeException          提供されたデータの長さが暗号のブロック・サイズと一致しない場合発生する
     * @throws BadPaddingException                データが適切にパディングされない場合に発生する(暗号キーと複合キーが同じかチェックすること
     * @throws InvalidAlgorithmParameterException 無効なアルゴリズム・パラメータの例外
     */
    static String decrypt(String encrypt) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException,
            IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {

        byte[] encryptBytes = Base64.getDecoder().decode(encrypt);
        byte[] originalBytes = CipherHelper.cipher(encryptBytes, false);
        return new String(originalBytes);
    }

    /**
     * encryptメソッドとdecryptメソッドの共通部分
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
