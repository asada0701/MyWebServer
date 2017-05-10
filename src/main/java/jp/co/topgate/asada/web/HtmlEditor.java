package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.model.Message;

import java.io.*;
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
        path = Handler.getFilePath(requestLine.getUri());

        indexHtml = getHtml(indexPath);
        searchHtml = getHtml(searchPath);
        deleteHtml = getHtml(deletePath);
    }

    private String getHtml(String path) throws IOException {
        StringBuffer buffer = new StringBuffer();
        try (BufferedReader br = new BufferedReader(new FileReader(new File(path)))) {
            String str;
            while ((str = br.readLine()) != null) {
                buffer.append(str).append("\n");
            }
        }
        return buffer.toString();
    }

    /**
     * 投稿するメソッド
     *
     * @param list
     */
    void contribution(List<Message> list) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(new File(path)))) {
            String str;
            StringBuffer buffer = new StringBuffer();
            while ((str = br.readLine()) != null) {
                if (str.endsWith("<div id=\"log\">")) {
                    buffer.append(str).append("\n");
                    do {
                        str = br.readLine();
                        buffer.append(str).append("\n");
                    } while (!str.endsWith("</tr>"));

                    for (int i = list.size() - 1; i > -1; i--) {
                        buffer.append(getContribution(list.get(i)));
                        buffer.append(str).append("\n");
                    }
                }
                buffer.append(str).append("\n");
            }
            File file = new File(path);
            if (!file.delete()) {
                throw new IOException("存在しないファイルを編集しようとしました。");
            }

            try (OutputStream os = new FileOutputStream(new File(path))) {
                os.write(buffer.toString().getBytes());
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
    private String getContribution(Message message) {
        if (message.getText().contains("\r\n")) {
            message.setText(message.getText().replaceAll("\r\n", "<br>"));
        } else if (message.getText().contains("\n")) {
            message.setText(message.getText().replaceAll("\n", "<br>"));
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
    void search(List<Message> al) throws IOException {
        path = Handler.getFilePath(requestLine.getUri());
        //search.htmlを初期化
        searchInitialization();

        try (BufferedReader br = new BufferedReader(new FileReader(new File(path)))) {
            String str;
            StringBuffer buffer = new StringBuffer();
            while ((str = br.readLine()) != null) {
                if (str.endsWith("<div id=\"log\">")) {
                    buffer.append(str).append("\n");
                    do {
                        str = br.readLine();
                        buffer.append(str).append("\n");
                    } while (!str.endsWith("</tr>"));

                    for (int i = al.size() - 1; i > -1; i--) {
                        buffer.append(getContribution(al.get(i)));
                        buffer.append(str).append("\n");
                    }
                }
                buffer.append(str).append("\n");
            }

            try (OutputStream os = new FileOutputStream(new File(path))) {
                os.write(buffer.toString().getBytes());
                os.flush();
            }
        }
    }

    void delete1(Message message) throws IOException {
        path = Handler.getFilePath(requestLine.getUri());
        deleteInitialization();

        try (BufferedReader br = new BufferedReader(new FileReader(new File(path)))) {
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
            File file = new File(path);
            if (!file.delete()) {
                throw new IOException("存在しないファイルを編集しようとしました。");
            }

            try (OutputStream os = new FileOutputStream(new File(path))) {
                os.write(buffer.toString().getBytes());
                os.flush();
            }
        }
    }


    private String getDelete(Message message) {
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

    void indexInitialization() throws IOException {
        try (OutputStream os = new FileOutputStream(new File(indexPath))) {
            os.write(indexHtml.getBytes());
            os.flush();
        }
    }

    void searchInitialization() throws IOException {
        try (OutputStream os = new FileOutputStream(new File(searchPath))) {
            os.write(searchHtml.getBytes());
            os.flush();
        }
    }

    void deleteInitialization() throws IOException {
        try (OutputStream os = new FileOutputStream(new File(deletePath))) {
            os.write(deleteHtml.getBytes());
            os.flush();
        }
    }
}
