package jp.co.topgate.asada.web.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Modelパッケージのクラス群を管理するクラス
 *
 * @author asada
 */
public class ModelController {

    /**
     *
     */
    private static List<Message> messageList = new ArrayList<>();

    private static int score = 1;

    /**
     * メッセージリストにメッセージクラスを追加する
     *
     * @param message
     */
    public static void addMessage(Message message) {
        if (message == null) {
            throw new NullPointerException();
        }
        message.setMessageID(score);
        message.setDate(getNowDate());
        messageList.add(message);

        score++;
    }

    public static Message findMessage(int messageID) {
        for (Message m : messageList) {
            if (m.getMessageID() == messageID) {
                return m;
            }
        }
        return null;
    }

    /**
     * メッセージリストからメッセージを削除する
     *
     * @param message
     */
    public static void deleteMessage(Message message) {
        if (message == null) {
            throw new NullPointerException();
        }
        for (Message m : messageList) {
            if (m.getMessageID() == message.getMessageID()) {
                if (m.getPassword().equals(message.getPassword())) {
                    messageList.remove(m);
                }
            }
        }
    }

    private static String getNowDate() {
        LocalDateTime ldt = LocalDateTime.now();
        String s = String.valueOf(ldt.getYear()) + "/" + ldt.getMonthValue() + "/" + ldt.getDayOfMonth() +
                " " + ldt.getHour() + ":" + ldt.getMinute();
        return s;
    }
}
