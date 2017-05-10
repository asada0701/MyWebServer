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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CSVファイルの読み書きを行うクラス
 *
 * @author asada
 */
class CsvWriter {

    /**
     * CSVファイルのパス
     */
    private static Map<CsvMode, String> filePath = new HashMap<>();

    static {
        filePath.put(CsvMode.MESSAGE_MODE, "./src/main/resources/data/message.csv");
    }

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
     * @throws CsvRuntimeException    CSVファイルの読み込み中か、読み込む段階で例外が発生した
     * @throws CipherRuntimeException 読み込んだデータの復号に失敗した
     */
    static List<Message> read(CsvMode csvMode) throws CsvRuntimeException, CipherRuntimeException {
        List<Message> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(new File(filePath.get(csvMode))))) {
            String str;
            while (!Strings.isNullOrEmpty(str = br.readLine())) {

                String[] s = str.split(CSV_SEPARATOR);

                switch (csvMode) {
                    case MESSAGE_MODE:
                        if (s.length == MESSAGE_NUM_ITEMS) {
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
                        break;

                    default:
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
     * @param list CSVに書き込みたいListを渡す
     * @throws CsvRuntimeException    CSVファイルの読み込み中か、読み込む段階で例外が発生した
     * @throws CipherRuntimeException 読み込んだデータの復号に失敗した
     */
    static void write(CsvMode csvMode, List<?> list) throws CsvRuntimeException, CipherRuntimeException {
        try (OutputStream os = new FileOutputStream(new File(filePath.get(csvMode)))) {
            switch (csvMode) {
                case MESSAGE_MODE:
                    List<Message> messagesList = autoCast(list);
                    for (Message m : messagesList) {
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

                default:
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
     * 戻り値の型に合わせてキャストするメソッド
     *
     * @param obj キャストしたいオブジェクト
     * @param <T> 目的の型
     * @return キャストされたオブジェクト
     */
    @SuppressWarnings("unchecked")
    private static <T> T autoCast(Object obj) {
        return (T) obj;
    }

    /**
     * テスト用ファイルパスのセッター
     */
    static void setFilePath(String path) {
        filePath.put(CsvMode.MESSAGE_MODE, path);
    }
}
