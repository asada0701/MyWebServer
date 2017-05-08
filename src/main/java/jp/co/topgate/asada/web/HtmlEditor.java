package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.model.Message;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * HTMLを編集するクラス
 *
 * @author asada
 */
class HtmlEditor {

    private RequestLine requestLine;
    private String path;

    private String indexPath = "./src/main/resources/2/index.html";
    private String indexHtml;
    private String searchPath = "./src/main/resources/2/search.html";
    private String searchHtml;
    private String deletePath = "./src/main/resources/2/delete.html";
    private String deleteHtml;

    HtmlEditor(RequestLine requestLine) throws IOException {
        this.requestLine = requestLine;
        path = HandlerFactory.getFilePath(requestLine.getUri());

        indexHtml = getHtml(indexPath);
        searchHtml = getHtml(searchPath);
        deleteHtml = getHtml(deletePath);
    }

    private String getHtml(String path) throws IOException {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(new File(path)))) {
            String str;
            while ((str = br.readLine()) != null) {
                builder.append(str).append("\n");
            }
        }
        return builder.toString();
    }

    /**
     * 投稿するメソッド
     *
     * @param list
     */
    void contribution(List<Message> list) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(new File(path)))) {
            String str;
            StringBuilder builder = new StringBuilder();
            while ((str = br.readLine()) != null) {
                if (str.endsWith("<div id=\"log\">")) {
                    builder.append(str).append("\n");
                    do {
                        str = br.readLine();
                        builder.append(str).append("\n");
                    } while (!str.endsWith("</tr>"));

                    for (int i = list.size() - 1; i > -1; i--) {
                        builder.append(getContribution(list.get(i)));
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
            }
        }
    }

    /**
     * 投稿する文字をHTMLに編集する
     *
     * @param message
     * @return
     */
    String getContribution(Message message) {
        if (message.getText().contains("\n")) {
            message.setText(message.getText().replaceAll("\n", "<br>"));    //改行文字\nを<br>に変換する
        }

        String str = "            <tr id=\"No." + message.getMessageID() + "\">\n" +
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
                "                        <input type=\"hidden\" name=\"param\" value=\"delete1\">\n" +
                "                        <input type=\"hidden\" name=\"number\" value=\"" + message.getMessageID() + "\">\n" +
                "                        <input type=\"submit\" value=\"このコメントを削除する\">\n" +
                "                    </form>\n" +
                "                </td>\n";
        return str;
    }

    /**
     * 投稿した人で抽出するメソッド
     */
    void search(ArrayList<Message> al) throws IOException {
        path = HandlerFactory.getFilePath(requestLine.getUri());
        //search.htmlを初期化
        searchInitialization();

        try (BufferedReader br = new BufferedReader(new FileReader(new File(path)))) {
            String str;
            StringBuilder builder = new StringBuilder();
            while ((str = br.readLine()) != null) {
                if (str.endsWith("<div id=\"log\">")) {
                    builder.append(str).append("\n");
                    do {
                        str = br.readLine();
                        builder.append(str).append("\n");
                    } while (!str.endsWith("</tr>"));

                    for (int i = al.size() - 1; i > -1; i--) {
                        builder.append(getContribution(al.get(i)));
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
            }
        }
    }

    void delete1(Message message) throws IOException {
        path = HandlerFactory.getFilePath(requestLine.getUri());
        deleteInitialization();

        try (BufferedReader br = new BufferedReader(new FileReader(new File(path)))) {
            String str;
            StringBuilder builder = new StringBuilder();
            while ((str = br.readLine()) != null) {
                if (str.endsWith("<div id=\"log\">")) {
                    builder.append(str).append("\n");
                    do {
                        str = br.readLine();
                        builder.append(str).append("\n");
                    } while (!str.endsWith("</tr>"));

                    builder.append(getDelete(message));
                }
                if (str.endsWith("<input type=\"hidden\" name=\"number\" value=\"\">")) {
                    builder.append("            <input type=\"hidden\" name=\"number\" value=\"");
                    builder.append(message.getMessageID()).append("\">").append("\n");

                    str = br.readLine();
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
            }
        }
    }


    String getDelete(Message message) {
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

    void delete2(Message message) {
        try (BufferedReader br = new BufferedReader(new FileReader(new File(path)))) {
            String str;
            StringBuilder builder = new StringBuilder();
            while ((str = br.readLine()) != null) {
                if (str.endsWith("<tr id=\"No." + message.getMessageID() + "\">")) {
                    do {
                        str = br.readLine();
                    } while (!str.endsWith("</tr>"));
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

    void indexInitialization() throws IOException {
        File file = new File(indexPath);
        if (!file.delete()) {
            throw new IOException("存在しないファイルを編集しようとしました。");
        }

        try (OutputStream os = new FileOutputStream(new File(indexPath))) {
            os.write(indexHtml.getBytes());
            os.flush();
        }
    }

    void searchInitialization() throws IOException {
        File file = new File(searchPath);
        if (!file.delete()) {
            throw new IOException("存在しないファイルを編集しようとしました。");
        }

        try (OutputStream os = new FileOutputStream(new File(searchPath))) {
            os.write(searchHtml.getBytes());
            os.flush();
        }
    }

    void deleteInitialization() throws IOException {
        File file = new File(deletePath);
        if (!file.delete()) {
            throw new IOException("存在しないファイルを編集しようとしました。");
        }

        try (OutputStream os = new FileOutputStream(new File(deletePath))) {
            os.write(deleteHtml.getBytes());
            os.flush();
        }
    }
}
