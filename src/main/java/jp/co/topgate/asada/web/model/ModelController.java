package jp.co.topgate.asada.web.model;

import org.jetbrains.annotations.Contract;
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
public final class ModelController {

    /**
     * 書き込まれたメッセージのリスト
     */
    private static List<Message> messageList = new ArrayList<>();

    /**
     * メッセージIDのインデックス
     */
    private static int nextMessageID = 1;

    /**
     * コンストラクタ
     * インスタンス化禁止
     */
    private ModelController() {
    }

    /**
     * messageListに初期値を与える
     *
     * @param messageList CSVファイルから読み込んだデータ
     */
    public static void setMessageList(List<Message> messageList) {
        ModelController.messageList = messageList;

        if (messageList.size() > 0) {
            nextMessageID = messageList.get(messageList.size() - 1).getMessageID() + 1;
        }
    }

    /**
     * messageListにメッセージクラスを追加する
     */
    public static void addMessage(String name, String title, String text, String password) {
        Message message = new Message();
        message.setMessageID(nextMessageID);
        message.setName(name);
        message.setTitle(title);
        message.setText(text);
        message.setPassword(new BCryptPasswordEncoder().encode(password));  //パスワードハッシュ化
        message.setDate(getNowDate());
        messageList.add(message);

        nextMessageID++;
    }

    /**
     * 全てのメッセージを返すメソッド
     *
     * @return このクラスがもつ、メッセージリストを返す
     */
    public static List<Message> getAllMessage() {
        return messageList;
    }

    /**
     * 登録されているかメッセージを探すメソッド
     *
     * @param messageID 探したいメッセージのIDを渡す
     * @return ターゲットのメッセージを返す
     */
    public static Message findMessagebyID(int messageID) {
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
     * @return 見つからない場合はnull、見つかった場合はListで返す
     */
    public static List<Message> findMessageByName(String name) {
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
     */
    public static boolean deleteMessage(int messageID, String password) {
        for (Message message : messageList) {
            BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();
            if (messageID == message.getMessageID() && bcrypt.matches(password, message.getPassword())) {
                messageList.remove(message);
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
     */
    @Nullable
    public static String getName(int messageID) {
        for (Message message : messageList) {
            if (messageID == message.getMessageID()) {
                return message.getName();
            }
        }
        return null;
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

    /**
     * テスト用、メッセージIDのゲッター
     */
    @Contract(pure = true)
    static int getMessageID() {
        return nextMessageID;
    }

    /**
     * テスト用、現在格納しているメッセージの総数を返す
     */
    static int size() {
        return messageList.size();
    }
}
