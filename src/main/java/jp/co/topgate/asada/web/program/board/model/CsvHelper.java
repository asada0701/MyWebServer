package jp.co.topgate.asada.web.program.board.model;

import com.google.common.base.Strings;
import jp.co.topgate.asada.web.exception.CsvRuntimeException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * CSVファイルの読み書きを行うクラス
 *
 * @author asada
 */
class CsvHelper {

    /**
     * CSVのファイルパス
     */
    private static final String CSV_FILE_PATH = "./csv/message.csv";

    /**
     * CSVファイルの項目を分割する
     */
    private static final String CSV_SEPARATOR = ",";

    /**
     * CSVに書き込むメッセージの項目数
     */
    private static final int MESSAGE_NUM_ITEMS = 7;

    /**
     * 過去のMessageListを、CSVファイルから読み出すメソッド
     *
     * @return 過去に投稿された文をメッセージクラスのListに格納して返す
     * @throws CsvRuntimeException CSVファイルの中身が規定の形になっていないもしくはファイル読み込みに失敗した
     */
    static List<Message> readMessage() throws CsvRuntimeException {

        File file = new File(CSV_FILE_PATH);
        try {
            file.createNewFile();
            
        } catch (IOException e) {
            throw new CsvRuntimeException(e.getMessage(), e.getCause());
        }

        List<Message> messageList = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line;
            while (!Strings.isNullOrEmpty(line = bufferedReader.readLine())) {

                String[] s = line.split(CSV_SEPARATOR);

                if (s.length != MESSAGE_NUM_ITEMS) {
                    throw new CsvRuntimeException("指定されたCSVが規定の形にそっていないため読み込めません。");
                }

                Message m = new Message();
                m.setMessageID(Integer.parseInt(s[0]));
                m.setPassword(s[1]);
                m.setName(s[2]);
                m.setTitle(s[3]);
                m.setText(s[4]);
                m.setDate(s[5]);
                m.setTimeID(s[6]);

                messageList.add(m);
            }
        } catch (IOException e) {
            throw new CsvRuntimeException(e.getMessage(), e.getCause());
        }
        return messageList;
    }

    /**
     * MessageListを、CSVファイルに書き出すメソッド
     *
     * @param messageList CSVに書き込みたいListを渡す
     * @throws CsvRuntimeException CSVファイルの書き込み中に失敗したもしくは書き込みに失敗した
     */
    static void writeMessage(List<Message> messageList) throws CsvRuntimeException {
        try (OutputStream outputStream = new FileOutputStream(new File(CSV_FILE_PATH))) {
            for (Message m : messageList) {
                String messageID = String.valueOf(m.getMessageID());
                String password = m.getPassword();
                String name = m.getName();
                String title = m.getTitle();
                String text = m.getText();
                String date = m.getDate();
                String timeID = m.getTimeID();

                String[] strings = {messageID, password, name, title, text, date, timeID};

                String line = String.join(CSV_SEPARATOR, strings) + "\n";

                outputStream.write(line.getBytes());
            }
            outputStream.flush();

        } catch (IOException e) {
            throw new CsvRuntimeException(e.getMessage(), e.getCause());
        }
    }
}

