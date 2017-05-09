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
     * AES=高度な暗号化標準
     * 共通鍵暗号方式
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
     * Base64(文字列エンコード)
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
     * 複合メソッド
     *
     * @param encrypt 複合したい文字
     * @return 複合された文字
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
     * IvParameterSpecのコンストラクは初期化ベクトルを返す。
     * 初期化ベクトルとは、、
     * ①ターゲットの文字列を、あるバイト数毎に分解する。
     * ②分解したものを一つずつ、暗号化キーを使って、暗号化する
     * このとき前のブロックの暗号化したデータが次のブロックの暗号化に使われる。
     * 一番最初のブロックには前のブロックの暗号化データがないので、初期化ベクトルを用意する。
     *
     * @param source    対象となるバイト列
     * @param isEncrypt trueの場合、暗号化。falseの場合、複合。
     */
    private static byte[] cipher(byte[] source, boolean isEncrypt) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {

        //秘密鍵の構築
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), algorithm);

        //初期化ベクトルを返す。
        IvParameterSpec iv = new IvParameterSpec(initializationVector.getBytes());

        //Cipher.getInstanceメソッドは指定された変換を実装するCipherオブジェクトを返す。
        Cipher cipher = Cipher.getInstance(String.format(FORMAT_OF_TRANSFORMATION, secretKeySpec.getAlgorithm()));

        if (isEncrypt) {
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv);
        } else {
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, iv);
        }

        return cipher.doFinal(source);
    }
}
