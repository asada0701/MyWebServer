package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.model.Message;
import jp.co.topgate.asada.web.model.ModelController;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static jp.co.topgate.asada.web.HtmlListToEdit.INDEX_HTML;
import static jp.co.topgate.asada.web.HtmlListToEdit.SEARCH_HTML;
import static jp.co.topgate.asada.web.HtmlListToEdit.DELETE_HTML;

/**
 * HTMLを編集するクラス
 *
 * @author asada
 */
class HtmlEditor {

    /**
     * 編集したいhtmlファイルのパスのリスト
     */
    private static Map<Integer, String> filePath = new HashMap<>();

    static {
        filePath.put(INDEX_HTML.getId(), "./src/main/resources/2/index.html");
        filePath.put(SEARCH_HTML.getId(), "./src/main/resources/2/search.html");
        filePath.put(DELETE_HTML.getId(), "./src/main/resources/2/delete.html");
    }

    /**
     * 編集するhtmlの初期状態を保存するリスト
     */
    private static Map<Integer, String> htmlContent = new HashMap<>();

    /**
     * コンストラクタ
     * HTMLファイルの初期状態を保存する
     *
     * @throws IOException HTMLファイルに書き込み中にエラー発生
     */
    HtmlEditor() throws IOException {
        for (int i = 0; i < filePath.size(); i++) {
            try (BufferedReader br = new BufferedReader(new FileReader(new File(filePath.get(i))))) {
                String str;
                StringBuffer buffer = new StringBuffer();
                while ((str = br.readLine()) != null) {
                    buffer.append(str).append("\n");
                }
                htmlContent.put(i, buffer.toString());
            }
        }
    }

    /**
     * 登録されているHTMLファイルを初期化する
     *
     * @throws IOException HTMLファイルに書き込み中にエラー発生
     */
    void allInitialization() throws IOException {
        for (int i = 0; i < filePath.size(); i++) {
            try (OutputStream os = new FileOutputStream(new File(filePath.get(i)))) {
                os.write(htmlContent.get(i).getBytes());
                os.flush();
            }
        }
    }

    /**
     * メッセージを投稿する
     *
     * @throws IOException HTMLファイルに書き込み中にエラー発生
     */
    static void writeIndexHtml() throws IOException {
        List<Message> list = ModelController.getAllMessage();
        String path = filePath.get(INDEX_HTML.getId());

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
                        builder.append(messageChangeToHtml(INDEX_HTML, list.get(i)));
                        builder.append(str).append("\n");
                    }
                }
                builder.append(str).append("\n");
            }
            writeHtml(path, builder.toString());
        }
    }

    /**
     * 投稿した人で抽出する
     *
     * @param messageID 抽出したいメッセージのIDを渡す
     * @throws IOException HTMLファイルに書き込み中にエラー発生
     */
    static void writeSearchHtml(int messageID) throws IOException {
        List<Message> list = ModelController.findSameNameMessage(messageID);

        String path = filePath.get(SEARCH_HTML.getId());

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

                    assert list != null;
                    for (int i = list.size() - 1; i > -1; i--) {
                        builder.append(messageChangeToHtml(SEARCH_HTML, list.get(i)));
                        builder.append(str).append("\n");
                    }
                }
                builder.append(str).append("\n");
            }
            writeHtml(path, builder.toString());
        }
    }

    /**
     * 削除確認画面に削除したいメッセージを表示する
     *
     * @param message メッセージのオブジェクト
     * @throws IOException HTMLファイルに書き込み中にエラー発生
     */
    static void writeDeleteHtml(Message message) throws IOException {
        String path = filePath.get(DELETE_HTML.getId());

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

                    builder.append(messageChangeToHtml(DELETE_HTML, message));
                }
                if (str.endsWith("<input type=\"hidden\" name=\"number\" value=\"\">")) {
                    builder.append("            <input type=\"hidden\" name=\"number\" value=\"");
                    builder.append(message.getMessageID()).append("\">").append("\n");

                    str = br.readLine();
                }

                builder.append(str).append("\n");
            }
            writeHtml(path, builder.toString());
        }
    }

    /**
     * htmlに沿った文字列にするメソッド
     *
     * @param htmlListToEdit どのHTMLファイルに書き込むか
     * @param message        書き込みたいMessageのオブジェクト
     * @return HTMLの構文に沿った文字列
     */
    private static String messageChangeToHtml(HtmlListToEdit htmlListToEdit, Message message) {
        if (message.getText().contains("\r\n")) {
            message.setText(message.getText().replaceAll("\r\n", "<br>"));
        } else if (message.getText().contains("\n")) {
            message.setText(message.getText().replaceAll("\n", "<br>"));
        }
        String result = "            <tr id=\"No." + message.getMessageID() + "\">" + "\n" +
                "                <td>No." + message.getMessageID() + "</td>" + "\n" +
                "                <td>" + message.getTitle() + "</td>" + "\n" +
                "                <td>" + message.getText() + "</td>" + "\n" +
                "                <td>" + message.getName() + "</td>" + "\n" +
                "                <td>" + message.getDate() + "</td>" + "\n";
        switch (htmlListToEdit) {
            case INDEX_HTML:
                result = result +
                        "                <td>\n" +
                        "                    <form action=\"/program/board/\" method=\"post\">\n" +
                        "                        <input type=\"hidden\" name=\"param\" value=\"search\">\n" +
                        "                        <input type=\"hidden\" name=\"number\" value=\"" + message.getMessageID() + "\">\n" +
                        "                        <input type=\"submit\" value=\"このコメントを投稿した人の他のコメントを見てみる\">\n" +
                        "                    </form>\n" +
                        "                </td>\n";

            case SEARCH_HTML:
                result = result +
                        "                <td>" + "\n" +
                        "                    <form action=\"/program/board/\" method=\"post\">" + "\n" +
                        "                        <input type=\"hidden\" name=\"param\" value=\"delete1\">" + "\n" +
                        "                        <input type=\"hidden\" name=\"number\" value=\"" + message.getMessageID() + "\">" + "\n" +
                        "                        <input type=\"submit\" value=\"このコメントを削除する\">" + "\n" +
                        "                    </form>" + "\n" +
                        "                </td>" + "\n";
                break;

            case DELETE_HTML:
                break;

            default:
                return null;
        }
        return result;
    }

    /**
     * htmlファイルに書き込むメソッド
     *
     * @param filePath 書き込みたいファイルのパスを渡す
     * @param str      書き込みたい文字列を渡す
     * @throws IOException HTMLファイルに書き込み中にエラー発生
     */
    private static void writeHtml(String filePath, String str) throws IOException {
        try (OutputStream os = new FileOutputStream(new File(filePath))) {
            os.write(str.getBytes());
            os.flush();
        }
    }

    //テスト用
    public static void setFilePath(int index, String path) {
        filePath.put(index, path);
    }
}

/**
 * filePathとhtmlContentのインデックスを揃えるenum
 *
 * @author asada
 */
enum HtmlListToEdit {
    INDEX_HTML(0),
    SEARCH_HTML(1),
    DELETE_HTML(2);

    private final int id;

    HtmlListToEdit(final int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
