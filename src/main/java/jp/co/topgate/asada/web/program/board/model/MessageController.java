package jp.co.topgate.asada.web.program.board.model;

import jp.co.topgate.asada.web.exception.CsvRuntimeException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * modelパッケージのクラス群を管理するクラス
 *
 * @author asada
 */
public final class MessageController {

    private static final int INITIAL_MESSAGE_ID = 1;

    /**
     * コンストラクタ
     * インスタンス化禁止
     */
    private MessageController() {
    }

    public static List<Message> getAllMessage() throws CsvRuntimeException {
        return CsvHelper.readMessage();
    }

    /**
     * messageListにメッセージクラスを追加する
     *
     * @throws CsvRuntimeException {@link CsvHelper}を参照
     */
    public static void addMessage(String name, String title, String text, String password, String timeID) throws CsvRuntimeException {
        List<Message> messageList = CsvHelper.readMessage();
        int nextMessageID = INITIAL_MESSAGE_ID;
        if (messageList.size() > 0) {
            nextMessageID = messageList.get(messageList.size() - 1).getMessageID() + 1;
        }

        Message message = new Message();
        message.setMessageID(nextMessageID);
        message.setName(name);
        message.setTitle(title);
        message.setText(text);
        message.setPassword(new BCryptPasswordEncoder().encode(password));  //パスワードハッシュ化
        message.setDate(getNowDate());
        message.setTimeID(timeID);
        messageList.add(message);

        CsvHelper.writeMessage(messageList);
    }

    /**
     * messageListの中のmessageIDが同じものを返す
     *
     * @param messageID 探したいメッセージのIDを渡す
     * @return ターゲットのメッセージを返す
     * @throws CsvRuntimeException {@link CsvHelper}を参照
     */
    public static Message findMessageByID(int messageID) throws CsvRuntimeException {
        List<Message> messageList = CsvHelper.readMessage();
        for (Message message : messageList) {
            if (message.getMessageID() == messageID) {
                return message;
            }
        }
        return null;
    }

    /**
     * messageListの中のname属性が同じものを返す
     *
     * @param name 探したい投稿者の名前を渡す
     * @return メッセージのリストを返す。見つからない場合はListを空で返す
     * @throws CsvRuntimeException {@link CsvHelper}を参照
     */
    public static List<Message> findMessageByName(String name) throws CsvRuntimeException {
        List<Message> messageList = CsvHelper.readMessage();
        List<Message> result = new ArrayList<>();
        for (Message message : messageList) {
            if (message.getName().equals(name)) {
                result.add(message);
            }
        }
        return result;
    }

    /**
     * メッセージリストからメッセージを削除する
     *
     * @param messageID 削除したいメッセージのID
     * @param password  削除したいメッセージのPW
     * @return 削除に成功するとtrueを返す
     * @throws CsvRuntimeException {@link CsvHelper}を参照
     */
    public static boolean deleteMessage(int messageID, String password) throws CsvRuntimeException {
        List<Message> messageList = CsvHelper.readMessage();

        for (Message message : messageList) {
            BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
            if (messageID == message.getMessageID() && bcrypt.matches(password, message.getPassword())) {
                messageList.remove(message);

                CsvHelper.writeMessage(messageList);
                return true;
            }
        }
        return false;
    }

    /**
     * メッセージIDからメッセージを投稿した人の名前を取得する
     *
     * @param messageID 探したいメッセージID
     * @return 投稿した人の名前
     * @throws CsvRuntimeException {@link CsvHelper}を参照
     */
    @Nullable
    public static String getName(int messageID) throws CsvRuntimeException {
        List<Message> messageList = CsvHelper.readMessage();

        for (Message message : messageList) {
            if (messageID == message.getMessageID()) {
                return message.getName();
            }
        }
        return null;
    }

    /**
     * 二重リクエスト対策のメソッド
     * messageListに登録してあるメッセージがもつtimeIDと同じ場合は追加しない
     *
     * @param timeID 比較したい文字列
     * @return trueの場合は存在する。falseの場合は存在しない。
     * @throws CsvRuntimeException {@link CsvHelper}を参照
     */
    public static boolean isExist(String timeID) throws CsvRuntimeException {
        List<Message> messageList = CsvHelper.readMessage();

        for (Message message : messageList) {
            if (message.getTimeID().equals(timeID)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 現在の日付をフォーマットして文字列で返す
     *
     * @return （例)2017/5/5 17:20
     */
    @NotNull
    public static String getNowDate() {
        LocalDateTime ldt = LocalDateTime.now();
        return String.valueOf(ldt.getYear()) + "/" + ldt.getMonthValue() + "/" + ldt.getDayOfMonth() +
                " " + ldt.getHour() + ":" + ldt.getMinute();
    }
}
