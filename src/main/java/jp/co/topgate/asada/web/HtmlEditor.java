package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.model.Message;
import jp.co.topgate.asada.web.model.ModelController;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HTMLを編集するクラス
 *
 * @author asada
 */
class HtmlEditor {

    private static final int INDEX_HTML = 0;
    private static final int SEARCH_HTML = 1;
    private static final int DELETE_HTML = 2;

    /**
     * 編集したいhtmlファイルのパスのリスト
     */
    private static Map<Integer, String> filePath = new HashMap<>();

    /**
     * 編集するhtmlの初期状態を記録するリスト
     */
    private static Map<Integer, String> htmlContent = new HashMap<>();

    static {
        filePath.put(INDEX_HTML, "./src/main/resources/2/index.html");
        filePath.put(SEARCH_HTML, "./src/main/resources/2/search.html");
        filePath.put(DELETE_HTML, "./src/main/resources/2/delete.html");
    }

    /**
     * コンストラクタ
     *
     * @throws IOException
     */
    HtmlEditor() throws IOException {
        //編集する前のページを取得し、初期化する時に使う
        setInitialHtml();
    }

    /**
     * HTMLの初期状態を保存するメソッド
     *
     * @throws IOException 書き込み中に発生した例外
     */
    private void setInitialHtml() throws IOException {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < filePath.size(); i++) {
            try (BufferedReader br = new BufferedReader(new FileReader(new File(filePath.get(i))))) {
                String str;
                while ((str = br.readLine()) != null) {
                    buffer.append(str).append("\n");
                }
                htmlContent.put(i, buffer.toString());

                buffer = new StringBuffer();
            }
        }
    }

    /**
     * 投稿するメソッド
     */
    static void contribution() throws IOException {
        List<Message> list = ModelController.getAllMessage();
        String path = filePath.get(INDEX_HTML);

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
    private static String getContribution(Message message) {
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
    static void search(int messageID) throws IOException {
        List<Message> list = ModelController.findSameNameMessage(messageID);

        String path = filePath.get(SEARCH_HTML);

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
                        buffer.append(getSearch(list.get(i)));
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

    /**
     * 投稿する文字をHTMLに編集する
     *
     * @param message
     * @return
     */
    private static String getSearch(Message message) {
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
                "                        <input type=\"hidden\" name=\"param\" value=\"delete1\">\n" +
                "                        <input type=\"hidden\" name=\"number\" value=\"" + message.getMessageID() + "\">\n" +
                "                        <input type=\"submit\" value=\"このコメントを削除する\">\n" +
                "                    </form>\n" +
                "                </td>\n";
        return str;
    }

    /**
     * 削除確認画面に削除したいメッセージを表示するメソッド
     *
     * @param message
     * @throws IOException
     */
    static void delete(Message message) throws IOException {
        String path = filePath.get(DELETE_HTML);

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

            try (OutputStream os = new FileOutputStream(new File(path))) {
                os.write(buffer.toString().getBytes());
                os.flush();
            }
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

    /**
     * 登録されているHTML全部を初期化する
     *
     * @throws IOException 書き込み中に発生した例外
     */
    void allInitialization() throws IOException {
        for (int i = 0; i < filePath.size(); i++) {
            try (OutputStream os = new FileOutputStream(new File(filePath.get(i)))) {
                os.write(htmlContent.get(i).getBytes());
                os.flush();
            }
        }
    }
}
