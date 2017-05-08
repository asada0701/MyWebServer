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

    private static List<Message> messageList = new ArrayList<>();

    private static int score = 1;

    public ModelController(List<Message> messageList) {
        ModelController.messageList = messageList;

        if (messageList.size() > 0) {
            score = messageList.get(messageList.size() - 1).getMessageID() + 1;
        }
    }

    /**
     * メッセージリストにメッセージクラスを追加する
     */
    public static void addMessage(String name, String title, String text, String password) {
        Message message = new Message();
        message.setMessageID(score);
        message.setName(name);
        message.setTitle(title);
        message.setText(text);
        message.setPassword(password);
        message.setDate(getNowDate());
        messageList.add(message);

        score++;
    }

    public static List<Message> getAllMessage() {
        return messageList;
    }

    public static Message findMessage(int messageID) {
        for (Message m : messageList) {
            if (m.getMessageID() == messageID) {
                return m;
            }
        }
        return null;
    }

    public static ArrayList<Message> findMessageByID(int messageID) {
        ArrayList<Message> al = new ArrayList<>();

        Message message = findMessage(messageID);

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
     * @param message
     */
    public static void deleteMessage(Message message) throws Exception {
        if (message == null) {
            throw new NullPointerException();
        }
        for (int i = 0; i < messageList.size(); i++) {
            if (message.getMessageID() == messageList.get(i).getMessageID()) {
                messageList.remove(messageList.get(i));
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
