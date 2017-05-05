package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.exception.RequestParseException;
import jp.co.topgate.asada.web.model.Message;
import jp.co.topgate.asada.web.model.User;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * WebAppの処理を行うハンドラークラス
 *
 * @author asada
 */
public class WebAppHandler extends Handler {
    private static int score = 1;
    private static List<User> userList = new ArrayList<>();
    private static List<Message> messageList = new ArrayList<>();

    /**
     * リクエストが来たときに呼び出すメソッド
     *
     * @param bis SocketのInputStreamをBufferedInputStreamにラップして渡す
     */
    @Override
    public void requestComes(BufferedInputStream bis) {
        super.requestComes(bis);
        try {
            if (statusCode == ResponseMessage.OK) {
                if ("POST".equals(requestLine.getMethod())) {
                    //POSTの時のみHTMLを編集する
                    try {
                        editHtml(new RequestMessage(bis, requestLine));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (RequestParseException e) {
            statusCode = ResponseMessage.BAD_REQUEST;
        }
    }

    /**
     * HTMLを編集するメソッド
     *
     * @param requestMessage
     * @throws IOException
     */
    private void editHtml(RequestMessage requestMessage) throws IOException {
        if (requestLine.getUri().startsWith("/program/board/")) {
            String param = requestMessage.findMessageBody("param");
            if (param != null) {
                Message message;
                User user;

                switch (param) {
                    case "contribution":
                        user = new User();
                        user.setName(requestMessage.findMessageBody("name"));
                        user.setEmail(requestMessage.findMessageBody("email"));

                        message = new Message();
                        message.setTitle(requestMessage.findMessageBody("title"));
                        message.setText(requestMessage.findMessageBody("text"));

                        userList.add(user);
                        messageList.add(message);
                        contribution(message, user);
                        break;

                    case "search":
                        user = new User();
                        user.setName(requestMessage.findMessageBody("name"));

                        search(user);
                        break;

                    case "delete1":
                        requestLine.setUri("/program/board/delete.html");
                        break;

                    case "delete2":
                        //メールで認証（未実装）
                        user = new User();
                        user.setEmail(requestMessage.findMessageBody("email"));

                        Message message2 = new Message();
                        message2.setMessageID(Integer.parseInt(requestMessage.findMessageBody("number")));

                        delete(message2);
                        break;

                    case "back":
                        requestLine.setUri("/program/board/index.html");
                        break;

                    default:
                        requestLine.setUri("/program/board/index.html");
                }
            }
        }
    }

    /**
     * 投稿するメソッド
     *
     * @param message
     * @param user
     */
    private void contribution(Message message, User user) {

        String path = HandlerFactory.getFilePath(requestLine.getUri());

        try (BufferedReader br = new BufferedReader(new FileReader(new File(path)))) {
            String str;
            StringBuilder builder = new StringBuilder();
            while ((str = br.readLine()) != null) {
                if (str.endsWith("<div id=\"log\">")) {
                    builder.append(str).append("\n");
                    for (int i = 0; i < 9; i++) {
                        str = br.readLine();
                        builder.append(str).append("\n");
                    }
                    builder.append(setData(user.getName(), message.getTitle(), message.getText()));
                }
                builder.append(str).append("\n");
            }
            File file = new File(path);
            if (!file.delete()) {
                throw new IOException("存在しないファイルを編集しようとしました。");
            }

            try (OutputStream os = new FileOutputStream(new File(path))) {
                os.write(builder.toString().getBytes());
                os.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        score++;
    }

    /**
     * 投稿した人で抽出するメソッド
     *
     * @param user
     */
    private void search(User user) {
        for (User u : userList) {
            if (user.getName().equals(u.getName())) {
                System.out.println(user.getName());
            }
        }
    }

    /**
     * 削除ボタンが押された時のメソッド
     *
     * @param message
     */
    private void delete(Message message) {
        String trID = "            <tr id=\"No." + message.getMessageID() + "\">";

        String path = HandlerFactory.getFilePath(requestLine.getUri());

        try (BufferedReader br = new BufferedReader(new FileReader(new File(path)))) {
            String str;
            StringBuilder builder = new StringBuilder();
            while ((str = br.readLine()) != null) {
                if (trID.equals(str)) {
                    for (int i = 0; i < 14; i++) {
                        str = br.readLine();
                    }
                }
                builder.append(str).append("\n");
            }
            File file = new File(path);
            if (!file.delete()) {
                throw new IOException("存在しないファイルを編集しようとしました。");
            }

            try (OutputStream os = new FileOutputStream(new File(path))) {
                os.write(builder.toString().getBytes());
                os.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 投稿する文字をHTMLに編集する
     *
     * @param name
     * @param title
     * @param text
     * @return
     */
    private String setData(String name, String title, String text) {
        if (text.contains("\n")) {
            text = text.replaceAll("\n", "<br>");
        }
        String str =
                "            <tr id=\"No." + score + "\">\n" +
                        "                <td>No." + score + "</td>\n" +
                        "                <td>" + title + "</td>\n" +
                        "                <td>" + text + "</td>\n" +
                        "                <td>" + name + "</td>\n" +
                        "                <td>" + getNowDate() + "</td>\n" +
                        "                <td>\n" +
                        "                    <form action=\"/program/board/\" method=\"post\">\n" +
                        "                        <input type=\"hidden\" name=\"param\" value=\"delete1\">\n" +
                        "                        <input type=\"hidden\" name=\"number\" value=\"" + score + "\">\n" +
                        "                        <input type=\"submit\" value=\"このコメントを削除する\">\n" +
                        "                    </form>\n" +
                        "                </td>\n";
        return str;
    }

    /**
     * 現在日時を返す
     *
     * @return 2017/5/5 17:55
     */
    private String getNowDate() {
        LocalDateTime ldt = LocalDateTime.now();
        String s = String.valueOf(ldt.getYear()) + "/" + ldt.getMonthValue() + "/" + ldt.getDayOfMonth() +
                " " + ldt.getHour() + ":" + ldt.getMinute();
        return s;
    }
}
