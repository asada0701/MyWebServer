package jp.co.topgate.asada.web;

import com.google.common.base.Strings;
import jp.co.topgate.asada.web.exception.CipherRuntimeException;
import jp.co.topgate.asada.web.exception.CsvRuntimeException;
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
     * @throws CsvRuntimeException    CSVファイルの読み込み中か、読み込む段階で例外が発生した
     * @throws CipherRuntimeException 読み込んだデータの復号に失敗した
     */
    static List<Message> read() throws CsvRuntimeException, CipherRuntimeException {

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
        } catch (IOException e) {
            throw new CsvRuntimeException(e.getMessage());

        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException |
                InvalidAlgorithmParameterException | BadPaddingException | IllegalBlockSizeException e) {

            throw new CipherRuntimeException(e.getMessage());
        }
        return list;
    }

    /**
     * 現在のMessageListを、CSVファイルに書き出すメソッド
     *
     * @param list CSVに書き込むメッセージクラスのListを渡す。
     * @throws CsvRuntimeException    CSVファイルの読み込み中か、読み込む段階で例外が発生した
     * @throws CipherRuntimeException 読み込んだデータの復号に失敗した
     */
    static void write(List<Message> list) throws CsvRuntimeException, CipherRuntimeException {

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

        } catch (IOException e) {
            throw new CsvRuntimeException(e.getMessage());

        } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException |
                InvalidAlgorithmParameterException | BadPaddingException | IllegalBlockSizeException e) {

            throw new CipherRuntimeException(e.getMessage());
        }
    }

    /**
     * テスト用ファイルパスのセッター
     */
    static void setFilePath(String path) {
        filePath = path;
    }
}
