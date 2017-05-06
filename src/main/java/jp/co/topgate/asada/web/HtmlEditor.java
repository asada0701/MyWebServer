package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.model.Message;

import java.io.*;

/**
 * Created by yusukenakashima0701 on 2017/05/06.
 */
public class HtmlEditor {

    private RequestLine requestLine;
    private String path;

    public HtmlEditor(RequestLine requestLine) {
        this.requestLine = requestLine;
        path = HandlerFactory.getFilePath(requestLine.getUri());
    }

    public void initialization() {
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
     * 投稿するメソッド
     *
     * @param message
     */
    public void contribution(Message message) {
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
     * @param message
     * @return
     */
    public String setData(Message message) {
        if (message.getText().contains("\n")) {
            message.setText(message.getText().replaceAll("\n", "<br>"));    //改行文字\nを<br>に変換する
        }

        String str = "<tr id=\"No." + message.getMessageID() + "\">\n" +
                "                <td>No." + message.getMessageID() + "</td>\n" +
                "                <td>" + message.getTitle() + "</td>\n" +
                "                <td>" + message.getText() + "</td>\n" +
                "                <td>" + message.getName() + "</td>\n" +
                "                <td>" + message.getDate() + "</td>\n" +
                "                <td>\n" +
                "                    <form action=\"/program/board/\" method=\"post\">\n" +
                "                        <input type=\"hidden\" name=\"param\" value=\"search\">\n" +
                "                        <input type=\"hidden\" name=\"number\" value=\"" + message.getMessageID() + "\">\n" +
                "                        <input type=\"submit\" value=\"このコメントを投稿した人の他のコメントを見てみる\">\n" +
                "                    </form>\n" +
                "                </td>\n" +
                "                <td>\n" +
                "                    <form action=\"/program/board/\" method=\"post\">\n" +
                "                        <input type=\"hidden\" name=\"param\" value=\"delete\">\n" +
                "                        <input type=\"hidden\" name=\"number\" value=\"" + message.getMessageID() + "\">\n" +
                "                        <input type=\"submit\" value=\"このコメントを削除する\">\n" +
                "                    </form>\n" +
                "                </td>";
        return str;
    }

    /**
     * 投稿した人で抽出するメソッド
     */
    public void search(Message message) {
    }

    public void delete1() {

    }

    /**
     * 削除ボタンが押された時のメソッド
     *
     * @param message
     */
    public void delete2(Message message) {
        String trID = "            <tr id=\"No." + message.getMessageID() + "\">";

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
}
