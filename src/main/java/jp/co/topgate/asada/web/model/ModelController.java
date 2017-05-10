package jp.co.topgate.asada.web.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * modelパッケージのクラス群を管理するクラス
 *
 * @author asada
 */
public class ModelController {

    /**
     * 書き込まれたメッセージのリスト
     */
    private static List<Message> messageList = new ArrayList<>();

    /**
     * メッセージIDのインデックス
     */
    private static int messageID = 1;

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
            messageID = messageList.get(messageList.size() - 1).getMessageID() + 1;
        }
    }

    /**
     * messageListにメッセージクラスを追加する
     */
    public static void addMessage(String name, String title, String text, String password) {
        Message message = new Message();
        message.setMessageID(messageID);
        message.setName(name);
        message.setTitle(title);
        message.setText(text);
        message.setPassword(password);
        message.setDate(getNowDate());
        messageList.add(message);

        messageID++;
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
    public static Message findMessage(int messageID) {
        for (Message m : messageList) {
            if (m.getMessageID() == messageID) {
                return m;
            }
        }
        return null;
    }

    /**
     * messageListの中のname属性が同じものを返す
     *
     * @param messageID 探したいメッセージのIDを渡す
     * @return 見つからない場合はnull、見つかった場合はListで返す
     */
    public static List<Message> findSameNameMessage(int messageID) {
        List<Message> al = new ArrayList<>();

        Message message = findMessage(messageID);
        if (message == null) {
            return null;
        }

        for (Message m : messageList) {
            if (message.getName().equals(m.getName())) {
                al.add(m);
            }
        }
        return al;
    }

    /**
     * メッセージリストからメッセージを削除する
     *
     * @param messageID 削除したいメッセージのID
     * @param password  削除したいメッセージのPW
     * @return 削除に成功するとtrueを返す
     */
    public static boolean deleteMessage(int messageID, String password) {
        if (password == null) {
            return false;
        }
        for (Message m : messageList) {
            if (messageID == m.getMessageID() && password.equals(m.getPassword())) {
                messageList.remove(m);
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
    private static String getNowDate() {
        LocalDateTime ldt = LocalDateTime.now();
        String s = String.valueOf(ldt.getYear()) + "/" + ldt.getMonthValue() + "/" + ldt.getDayOfMonth() +
                " " + ldt.getHour() + ":" + ldt.getMinute();
        return s;
    }

    /**
     * テスト用、メッセージIDのゲッター
     */
    static int getMessageID() {
        return messageID;
    }

    /**
     * テスト用、現在格納しているメッセージの総数を返す
     */
    static int size() {
        return messageList.size();
    }
}
