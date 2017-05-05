package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.exception.RequestParseException;
import jp.co.topgate.asada.web.model.Message;
import jp.co.topgate.asada.web.model.User;

import java.io.*;
import java.time.LocalDateTime;

/**
 * WebAppの処理を行うハンドラークラス
 *
 * @author asada
 */
public class WebAppHandler extends Handler implements HtmlEditor {
    private static int score = 1;

    /**
     * リクエストが来たときに呼び出すメソッド
     *
     * @param bis SocketのInputStreamをBufferedInputStreamにラップして渡す
     */
    @Override
    public void requestComes(BufferedInputStream bis) {
        try {
            RequestMessage requestMessage = new RequestMessage(bis, requestLine);
            String method = requestLine.getMethod();

            String protocolVersion = requestLine.getProtocolVersion();

            if (!"HTTP/1.1".equals(protocolVersion)) {
                statusCode = ResponseMessage.HTTP_VERSION_NOT_SUPPORTED;

            } else if (!"GET".equals(method) && !"POST".equals(method)) {
                statusCode = ResponseMessage.NOT_IMPLEMENTED;

            } else {
                File file = new File(HandlerFactory.getFilePath(requestLine.getUri()));
                if (!file.exists() || !file.isFile()) {
                    statusCode = ResponseMessage.NOT_FOUND;
                } else {
                    statusCode = ResponseMessage.OK;

                    if ("POST".equals(requestLine.getMethod())) {
                        //POSTの時のみHTMLを編集する
                        try {
                            editHtml(requestMessage);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (RequestParseException e) {
            statusCode = ResponseMessage.BAD_REQUEST;
        }
    }

    @Override
    public void editHtml(RequestMessage requestMessage) throws IOException {
        if (requestLine.getUri().startsWith("/program/board/")) {
            String param = requestMessage.findMessageBody("param");
            if (param != null) {
                switch (param) {
                    case "contribution":
                        Message message1 = new Message();
                        message1.setTitle(requestMessage.findMessageBody("title"));
                        message1.setText(requestMessage.findMessageBody("text"));

                        User user1 = new User();
                        user1.setName(requestMessage.findMessageBody("name"));
                        user1.setEmail(requestMessage.findMessageBody("email"));

                        contribution(message1, user1);
                        break;

                    case "search":
                        search(requestMessage);
                        break;

                    case "delete1":
                        requestLine.setUri("/program/board/delete.html");
                        break;

                    case "delete2":
                        //メールで認証（未実装）
                        User user2 = new User();
                        user2.setEmail(requestMessage.findMessageBody("email"));

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

    private void search(RequestMessage requestMessage) {
        String number = requestMessage.findMessageBody("number");
        System.out.println(number);
    }


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

    private String getNowDate() {
        LocalDateTime ldt = LocalDateTime.now();
        String s = String.valueOf(ldt.getYear()) + "/" + ldt.getMonthValue() + "/" + ldt.getDayOfMonth() +
                " " + ldt.getHour() + ":" + ldt.getMinute();
        return s;
    }
}
