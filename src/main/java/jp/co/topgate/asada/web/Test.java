package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.model.Message;

import java.io.*;

/**
 * @author asada
 */
public class Test {
    private static String deletePath = "./src/main/resources/2/delete.html";

    public static void main(String[] args) {
        Message message = new Message();
        message.setMessageID(2);
        message.setText("テスト");

        try {
            deleteInitialization();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedReader br = new BufferedReader(new FileReader(new File(deletePath)))) {
            String str;
            StringBuffer buffer = new StringBuffer();
            while ((str = br.readLine()) != null) {
                if (str.endsWith("<div id=\"log\">")) {
                    buffer.append(str).append("\n");
                    do {
                        str = br.readLine();
                        buffer.append(str).append("\n");
                    } while (!str.endsWith("</tr>"));

                    buffer.append(getDelete(message));
                }
                if (str.endsWith("<input type=\"hidden\" name=\"number\" value=\"\">")) {
                    buffer.append("            <input type=\"hidden\" name=\"number\" value=\"");
                    buffer.append(message.getMessageID()).append("\">").append("\n");

                    str = br.readLine();
                }
                buffer.append(str).append("\n");
            }
            File file = new File(deletePath);
            if (!file.delete()) {
                throw new IOException("存在しないファイルを編集しようとしました。");
            }

            try (OutputStream os = new FileOutputStream(new File(deletePath))) {
                os.write(buffer.toString().getBytes());
                os.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getDelete(Message message) {
        if (message.getText().contains("\n")) {
            message.setText(message.getText().replaceAll("\n", "<br>"));    //改行文字\nを<br>に変換する
        }

        String str = "            <tr id=\"No." + message.getMessageID() + "\">\n" +
                "                <td>No." + message.getMessageID() + "</td>\n" +
                "                <td>" + message.getTitle() + "</td>\n" +
                "                <td>" + message.getText() + "</td>\n" +
                "                <td>" + message.getName() + "</td>\n" +
                "                <td>" + message.getDate() + "</td>\n";
        return str;
    }

    private static void deleteInitialization() throws IOException {
        File file = new File(deletePath);
        if (!file.delete()) {
            throw new IOException("存在しないファイルを編集しようとしました。");
        }

        String deleteHtml = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "\n" +
                "<head>\n" +
                "    <meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">\n" +
                "    <link rel=\"stylesheet\" type=\"text/css\" href=\"./css/deleteStyle.css\">\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                "<center>\n" +
                "    <div id=\"header\">\n" +
                "        <h1>掲示板-LightBoard</h1>\n" +
                "    </div>\n" +
                "    <div id=\"log\">\n" +
                "        <h2>削除するメッセージ</h2>\n" +
                "        <table border=\"1\">\n" +
                "            <tr>\n" +
                "                <th>ナンバー</th>\n" +
                "                <th>タイトル</th>\n" +
                "                <th>本文</th>\n" +
                "                <th>ユーザー名</th>\n" +
                "                <th>日付</th>\n" +
                "            </tr>\n" +
                "        </table>\n" +
                "    </div>\n" +
                "    <div id=\"form\">\n" +
                "        <p>投稿した時に入力したパスワードを入力してください。</p>\n" +
                "        <form action=\"/program/board/\" method=\"post\">\n" +
                "            <p>\n" +
                "                パスワード<input type=\"password\" name=\"pw\" size=\"10\" required>\n" +
                "            </p>\n" +
                "            <input type=\"hidden\" name=\"number\" value=\"\">\n" +
                "            <input type=\"hidden\" name=\"param\" value=\"delete2\">\n" +
                "            <input type=\"submit\" value=\"削除する\">\n" +
                "        </form>\n" +
                "    </div>\n" +
                "    <div id=\"back\">\n" +
                "        <form action=\"/program/board/\" method=\"get\">\n" +
                "            <input type=\"hidden\" name=\"param\" value=\"back\">\n" +
                "            <input type=\"submit\" value=\"戻る\">\n" +
                "        </form>\n" +
                "    </div>\n" +
                "</center>\n" +
                "</body>\n" +
                "\n" +
                "</html>\n";

        try (OutputStream os = new FileOutputStream(new File(deletePath))) {
            os.write(deleteHtml.getBytes());
            os.flush();
        }
    }
}
