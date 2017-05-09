package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.model.Message;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * CSVファイルの読み書きを行うクラス
 *
 * @author asada
 */
class CsvWriter {

    /**
     * CSVファイルのパス
     */
    private static final String filePath = "./src/main/resources/data/message.csv";

    /**
     * CSVファイルから過去の投稿された文を読み出すメソッド
     *
     * @return 過去に投稿された文をメッセージクラスのListに格納して返す
     * @throws IOException                        読み出し中の例外
     * @throws NoSuchAlgorithmException           ある暗号アルゴリズムが現在の環境で使用できない場合発生する
     * @throws NoSuchPaddingException             あるパディング・メカニズムが現在の環境で使用できない場合発生する
     * @throws InvalidKeyException                無効な鍵に対する例外
     * @throws IllegalBlockSizeException          提供されたデータの長さが暗号のブロック・サイズと一致しない場合発生する
     * @throws BadPaddingException                データが適切にパディングされない場合に発生する(暗号キーと複合キーが同じかチェックすること
     * @throws InvalidAlgorithmParameterException 無効なアルゴリズム・パラメータの例外
     */
    static List<Message> read() throws IOException, NoSuchPaddingException, InvalidAlgorithmParameterException,
            NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        List<Message> list = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(new File(filePath)))) {
            String str;
            while ((str = br.readLine()) != null) {

                String[] s = str.split(",");
                if (s.length == 6) {
                    Message message = new Message();
                    message.setMessageID(Integer.parseInt(s[0]));

                    //複合
                    String decryptedResult = CipherHelper.decrypt(s[1]);
                    message.setPassword(decryptedResult);

                    message.setName(s[2]);
                    message.setTitle(s[3]);
                    message.setText(s[4]);
                    message.setDate(s[5]);

                    list.add(message);
                } else {
                    throw new IOException("指定されたCSVが規定の形にそっていないため読み込めません。");
                }
            }
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("CSVファイルが見つかりません。");
        }
        return list;
    }

    /**
     * CSVファイルに投稿された文を書き出すメソッド
     *
     * @param list CSVに書き込むメッセージクラスのListを渡す。
     * @throws IOException                        存在しないファイルを編集しようとした場合に発生する
     * @throws NoSuchAlgorithmException           ある暗号アルゴリズムが現在の環境で使用できない場合発生する
     * @throws NoSuchPaddingException             あるパディング・メカニズムが現在の環境で使用できない場合発生する
     * @throws InvalidKeyException                無効な鍵に対する例外
     * @throws IllegalBlockSizeException          提供されたデータの長さが暗号のブロック・サイズと一致しない場合発生する
     * @throws BadPaddingException                データが適切にパディングされない場合に発生する(暗号キーと複合キーが同じかチェックすること
     * @throws InvalidAlgorithmParameterException 無効なアルゴリズム・パラメータの例外
     */
    static void write(List<Message> list) throws IOException, NoSuchPaddingException, InvalidAlgorithmParameterException,
            NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {

        try (OutputStream os = new FileOutputStream(new File(filePath))) {
            StringBuffer buffer = new StringBuffer();
            for (Message m : list) {
                buffer.append(m.getMessageID()).append(",");

                //暗号化
                String original = String.valueOf(m.getPassword());
                String result = CipherHelper.encrypt(original);
                buffer.append(result).append(",");

                buffer.append(m.getName()).append(",");
                buffer.append(m.getTitle()).append(",").append(m.getText()).append(",").append(m.getDate()).append("\n");
            }
            os.write(buffer.toString().getBytes());
            os.flush();
        }
    }
}
