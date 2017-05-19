package jp.co.topgate.asada.web.app;

import com.google.common.base.Strings;
import jp.co.topgate.asada.web.exception.CsvRuntimeException;
import jp.co.topgate.asada.web.model.Message;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CSVファイルの読み書きを行うクラス
 *
 * @author asada
 */
public class CsvHelper {

    /**
     * ファイルパス
     */
    private static final String csvPath = "./src/main/resources/data/message.csv";

    /**
     * CSVファイルの項目を分割する
     */
    private static final String CSV_SEPARATOR = ",";

    /**
     * CSVに書き込むメッセージの項目数
     */
    private static final int MESSAGE_NUM_ITEMS = 6;

    /**
     * 過去のMessageListを、CSVファイルから読み出すメソッド
     *
     * @return 過去に投稿された文をメッセージクラスのListに格納して返す
     * @throws CsvRuntimeException CSVファイルの中身が規定の形になっていないもしくはファイル読み込みに失敗した
     */
    public static List<Message> readMessage() throws CsvRuntimeException {
        List<Message> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(new File(csvPath)))) {
            String str;
            while (!Strings.isNullOrEmpty(str = br.readLine())) {

                String[] s = str.split(CSV_SEPARATOR);
                if (s.length == MESSAGE_NUM_ITEMS) {
                    Message m = new Message();
                    int i = 0;
                    m.setMessageID(Integer.parseInt(s[i++]));
                    m.setPassword(s[i++]);
                    m.setName(s[i++]);
                    m.setTitle(s[i++]);
                    m.setText(s[i++]);
                    m.setDate(s[i]);

                    list.add(m);
                } else {
                    throw new CsvRuntimeException("指定されたCSVが規定の形にそっていないため読み込めません。");
                }
            }
        } catch (IOException e) {
            throw new CsvRuntimeException(e.getMessage());
        }
        return list;
    }

    /**
     * MessageListを、CSVファイルに書き出すメソッド
     *
     * @param list CSVに書き込みたいListを渡す
     * @throws CsvRuntimeException CSVファイルの書き込み中に失敗した
     */
    public static void writeMessage(List<Message> list) throws CsvRuntimeException {
        try (OutputStream os = new FileOutputStream(new File(csvPath))) {
            for (Message m : list) {
                String messageID = String.valueOf(m.getMessageID());
                String password = m.getPassword();
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
        }
    }
}

