package jp.co.topgate.asada.web;

import com.google.common.base.Strings;
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
    private static String filePath = "./src/main/resources/data/message.csv";

    /**
     * CSVファイルの項目を分割する
     */
    private static final String CSV_SEPARATOR = ",";

    /**
     * 過去のMessageListを、CSVファイルから読み出すメソッド
     *
     * @return 過去に投稿された文をメッセージクラスのListに格納して返す
     * @throws IOException                        読み出し中の例外
     * @throws FileNotFoundException              CSVファイルが存在しない
     * @throws NoSuchAlgorithmException           ある暗号アルゴリズムが現在の環境で使用できない場合発生する
     * @throws NoSuchPaddingException             あるパディング・メカニズムが現在の環境で使用できない場合発生する
     * @throws InvalidKeyException                無効な鍵に対する例外
     * @throws IllegalBlockSizeException          提供されたデータの長さが暗号のブロック・サイズと一致しない場合発生する
     * @throws BadPaddingException                データが適切にパディングされない場合に発生する(暗号キーと複合キーが同じかチェックすること
     * @throws InvalidAlgorithmParameterException 無効なアルゴリズム・パラメータの例外
     */
    static List<Message> read() throws IOException, NoSuchPaddingException,
            InvalidAlgorithmParameterException, NoSuchAlgorithmException, IllegalBlockSizeException,
            BadPaddingException, InvalidKeyException {

        List<Message> list = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(new File(filePath)))) {
            String str;
            while (!Strings.isNullOrEmpty(str = br.readLine())) {

                String[] s = str.split(CSV_SEPARATOR);
                if (s.length == 6) {
                    Message message = new Message();
                    message.setMessageID(Integer.parseInt(s[0]));

                    message.setPassword(CipherHelper.decrypt(s[1]));    //パスワードの複合

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
     * 現在のMessageListを、CSVファイルに書き出すメソッド
     *
     * @param list CSVに書き込むメッセージクラスのListを渡す。
     * @throws IOException                        ファイルに書き込み中に例外発生
     * @throws FileNotFoundException              CSVファイルが存在しない
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
            for (Message m : list) {
                String messageID = String.valueOf(m.getMessageID());

                String password = CipherHelper.encrypt(m.getPassword());    //パスワード暗号化

                String name = m.getName();
                String title = m.getTitle();
                String text = m.getText();
                String date = m.getDate();

                String[] strings = {messageID, password, name, title, text, date};

                String line = String.join(CSV_SEPARATOR, strings) + "\n";

                os.write(line.getBytes());
            }
            os.flush();

        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("CSVファイルが見つかりません。");
        }
    }

    /**
     * テスト用ファイルパスのセッター
     */
    static void setFilePath(String path) {
        filePath = path;
    }
}
