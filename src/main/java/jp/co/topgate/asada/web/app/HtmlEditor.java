package jp.co.topgate.asada.web.app;

import jp.co.topgate.asada.web.exception.HtmlInitializeException;
import jp.co.topgate.asada.web.model.Message;
import jp.co.topgate.asada.web.model.ModelController;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * HTMLを編集するクラス
 *
 * @author asada
 */
public class HtmlEditor {
    /**
     * 編集するhtmlの初期状態を保存するリスト
     */
    private static Map<Integer, String> htmlContent = new HashMap<>();

    /**
     * コンストラクタ
     * HTMLファイルの初期状態を保存する
     *
     * @throws HtmlInitializeException HTMLファイルの読み込み中にエラー発生
     */
    public HtmlEditor() throws HtmlInitializeException {
        for (EditHtmlList hlte : EditHtmlList.values()) {
            try (BufferedReader br = new BufferedReader(new FileReader(new File(hlte.getPath())))) {
                String str;
                StringBuilder builder = new StringBuilder();
                while ((str = br.readLine()) != null) {
                    builder.append(str).append("\n");
                }
                htmlContent.put(hlte.getId(), builder.toString());

            } catch (IOException e) {
                throw new HtmlInitializeException(e.getMessage());
            }
        }
    }

    /**
     * 登録されているHTMLファイルを初期化する
     *
     * @throws HtmlInitializeException HTMLファイルに書き込み中にエラー発生
     */
    public void allInitialization() throws HtmlInitializeException {
        for (EditHtmlList hlte : EditHtmlList.values()) {
            try (OutputStream os = new FileOutputStream(new File(hlte.getPath()))) {
                os.write(htmlContent.get(hlte.getId()).getBytes());
                os.flush();
            } catch (IOException e) {
                throw new HtmlInitializeException(e.getMessage());
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
        String path = EditHtmlList.INDEX_HTML.getPath();

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
                        builder.append(messageChangeToHtml(EditHtmlList.INDEX_HTML, list.get(i)));
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
     * @param name 探したい投稿者の名前を渡す
     * @throws IOException          HTMLファイルに書き込み中にエラー発生
     * @throws NullPointerException 引数がnull
     */
    static void writeSearchHtml(String name) throws IOException, NullPointerException {
        Objects.requireNonNull(name);

        List<Message> list = ModelController.findSameNameMessage(name);

        String path = EditHtmlList.SEARCH_HTML.getPath();

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
                        builder.append(messageChangeToHtml(EditHtmlList.SEARCH_HTML, list.get(i)));
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
     * @throws IOException          HTMLファイルに書き込み中にエラー発生
     * @throws NullPointerException 引数がnull
     */
    static void writeDeleteHtml(Message message) throws IOException, NullPointerException {
        Objects.requireNonNull(message);

        String path = EditHtmlList.DELETE_HTML.getPath();

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

                    builder.append(messageChangeToHtml(EditHtmlList.DELETE_HTML, message));
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
     * @throws NullPointerException 引数がnull
     */
    private static String messageChangeToHtml(EditHtmlList htmlListToEdit, Message message) throws NullPointerException {
        Objects.requireNonNull(htmlListToEdit);
        Objects.requireNonNull(message);

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
}

/**
 * filePathとhtmlContentのインデックスを揃えるenum
 *
 * @author asada
 */
enum EditHtmlList {
    /**
     * index.html
     * 添え字、ファイルのパス
     */
    INDEX_HTML(0, "./src/main/resources/2/index.html"),

    /**
     * search.html
     * 添え字、ファイルのパス
     */
    SEARCH_HTML(1, "./src/main/resources/2/search.html"),

    /**
     * delete.html
     * 添え字、ファイルのパス
     */
    DELETE_HTML(2, "./src/main/resources/2/delete.html");

    private final int id;
    private final String path;

    EditHtmlList(final int id, final String path) {
        this.id = id;
        this.path = path;
    }

    public int getId() {
        return id;
    }

    public String getPath() {
        return path;
    }
}
