package jp.co.topgate.asada.web.app;

import jp.co.topgate.asada.web.exception.HtmlInitializeException;
import jp.co.topgate.asada.web.model.Message;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HTMLを編集するクラス
 *
 * @author asada
 */
public class HtmlEditor {
    /**
     * 編集するHTMLの初期状態を保存するリスト
     */
    private static Map<EditHtmlList, String> htmlContent = new HashMap<>();

    /**
     * コンストラクタ
     * HTMLファイルの初期状態を保存する
     *
     * @throws HtmlInitializeException HTMLファイルの読み込み中にエラー発生
     */
    public HtmlEditor() throws HtmlInitializeException {
        for (EditHtmlList eh : EditHtmlList.values()) {
            try (BufferedReader br = new BufferedReader(new FileReader(new File(eh.getPath())))) {
                String str;
                StringBuilder builder = new StringBuilder();
                while ((str = br.readLine()) != null) {
                    builder.append(str).append("\n");
                }
                htmlContent.put(eh, builder.toString());

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
        for (EditHtmlList eh : EditHtmlList.values()) {
            try (OutputStream os = new FileOutputStream(new File(eh.getPath()))) {
                os.write(htmlContent.get(eh).getBytes());
                os.flush();
            } catch (IOException e) {
                throw new HtmlInitializeException(e.getMessage());
            }
        }
    }

    /**
     * htmlContentに保存してあるhtmlを返す
     *
     * @param ehl 取得したいhtmlのEnum
     * @return HTML文章
     */
    String getHtml(EditHtmlList ehl) {
        return htmlContent.get(ehl);
    }

    /**
     * indexまたはsearchのHTML文章を編集する
     *
     * @param ehl     編集したいHTMLのEnum
     * @param rawHtml 編集前のHTML文章
     * @param list    HTML文章に書き込みたいMessageのリスト
     * @return 編集後のHTML文章
     */
    String editIndexOrSearchHtml(EditHtmlList ehl, String rawHtml, List<Message> list) {
        String[] line = rawHtml.split("\n");
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < line.length; i++) {
            String str = line[i];
            if (str.endsWith("<div id=\"log\">")) {
                while (!str.endsWith("</tr>")) {
                    builder.append(str).append("\n");
                    str = line[++i];
                }
                str = line[i];
                builder.append(str).append("\n");

                for (int k = list.size() - 1; k > -1; k--) {
                    builder.append(messageChangeToHtml(ehl, list.get(k)));
                    builder.append(str).append("\n");
                }
                str = line[++i];
            }
            builder.append(str).append("\n");
        }
        return builder.toString();
    }

    /**
     * deleteのHTML文章を編集する
     *
     * @param rawHtml 編集前のHTML文章
     * @param message メッセージのオブジェクト
     * @return 編集後のHTML文章
     */
    String editDeleteHtml(String rawHtml, Message message) {
        String[] line = rawHtml.split("\n");
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < line.length; i++) {
            String str = line[i];
            if (str.endsWith("<div id=\"log\">")) {
                while (!str.endsWith("</tr>")) {
                    builder.append(str).append("\n");
                    str = line[++i];
                }
                str = line[i];
                builder.append(str).append("\n");

                builder.append(messageChangeToHtml(EditHtmlList.DELETE_HTML, message));
                str = line[i];
            }
            if (str.endsWith("<input type=\"hidden\" name=\"number\" value=\"\">")) {
                builder.append("            <input type=\"hidden\" name=\"number\" value=\"");
                builder.append(message.getMessageID()).append("\">").append("\n");

                str = line[++i];
            }

            builder.append(str).append("\n");
        }
        return builder.toString();
    }

    /**
     * messageをHTML文章にする
     *
     * @param ehl     編集したHTMLのEnum
     * @param message 書き込みたいMessageのオブジェクト
     * @return HTML文章
     */
    static String messageChangeToHtml(EditHtmlList ehl, Message message) {
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
        switch (ehl) {
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
     * HTMLファイルに文章を書き込むメソッド
     *
     * @param ehl  書き込みたいHTMLのEnum
     * @param html 書き込みたい文字列
     * @throws IOException 書き込み中の例外
     */
    void writeHtml(EditHtmlList ehl, String html) throws IOException {
        try (OutputStream os = new FileOutputStream(new File(ehl.getPath()))) {
            os.write(html.getBytes());
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
    INDEX_HTML("./src/main/resources/2/index.html"),

    /**
     * search.html
     */
    SEARCH_HTML("./src/main/resources/2/search.html"),

    /**
     * delete.html
     */
    DELETE_HTML("./src/main/resources/2/delete.html");

    private final String path;

    EditHtmlList(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
