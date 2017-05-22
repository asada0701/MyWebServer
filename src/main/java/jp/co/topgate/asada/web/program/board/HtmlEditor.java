package jp.co.topgate.asada.web.program.board;

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
     * HTMLの編集する場所のスタート地点を
     * div id = "log" で探す
     */
    private static final String SEARCH_DIV_ID = "<div id=\"log\">";

    /**
     * div id = "log"の後の t の閉じタグの次から編集を開始する
     */
    private static final String EDIT_START_POINT = "</tr>";

    /**
     * 編集するHTMLの初期状態を保存するマップ
     */
    private static Map<EditHtmlList, String> htmlContent = new HashMap<>();

    /**
     * コンストラクタ
     * HTMLファイルの初期状態を保存する
     *
     * @throws HtmlInitializeException HTMLファイルの読み込み中にエラー発生
     */
    public HtmlEditor() throws HtmlInitializeException {
        for (EditHtmlList editHtmlList : EditHtmlList.values()) {
            try (BufferedReader br = new BufferedReader(new FileReader(new File(editHtmlList.getPath())))) {
                String line;
                StringBuilder builder = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    builder.append(line).append("\n");
                }
                htmlContent.put(editHtmlList, builder.toString());

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
    public void resetAllFiles() throws HtmlInitializeException {
        for (EditHtmlList editHtmlList : EditHtmlList.values()) {
            try (OutputStream outputStream = new FileOutputStream(new File(editHtmlList.getPath()))) {
                outputStream.write(htmlContent.get(editHtmlList).getBytes());
                outputStream.flush();
            } catch (IOException e) {
                throw new HtmlInitializeException(e.getMessage());
            }
        }
    }

    /**
     * indexまたはsearchのHTML文章を編集する
     *
     * @param editHtmlList 編集したいHTMLのEnum
     * @param messageList  HTML文章に書き込みたいMessageのリスト
     * @return 編集後のHTML文章
     */
    String editIndexOrSearchHtml(EditHtmlList editHtmlList, List<Message> messageList) {
        String[] lineArray = htmlContent.get(editHtmlList).split("\n");     //改行で分割
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < lineArray.length; i++) {
            String line = lineArray[i];
            if (line.endsWith(SEARCH_DIV_ID)) {         //<div id="log">の箇所を探す
                while (!line.endsWith(EDIT_START_POINT)) {            //</tr>の次の行から編集する
                    builder.append(line).append("\n");
                    line = lineArray[++i];
                }
                builder.append(lineArray[i]).append("\n");  //</tr>をappend

                for (int k = messageList.size() - 1; k > -1; k--) {                //ここからが掲示板のメッセージの部分
                    builder.append(messageChangeToHtml(editHtmlList, messageList.get(k)));
                    builder.append(line).append("\n");
                }
                line = lineArray[++i];
            }
            builder.append(line).append("\n");
        }
        return builder.toString();
    }

    /**
     * deleteのHTML文章を編集する
     *
     * @param message メッセージのオブジェクト
     * @return 編集後のHTML文章
     */
    String editDeleteHtml(Message message) {
        String[] lineArray = htmlContent.get(EditHtmlList.DELETE_HTML).split("\n");     //改行で分割
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < lineArray.length; i++) {
            String line = lineArray[i];
            if (line.endsWith(SEARCH_DIV_ID)) {         //<div id="log">の箇所を探す
                while (!line.endsWith(EDIT_START_POINT)) {            //</tr>の次の行から編集する
                    builder.append(line).append("\n");
                    line = lineArray[++i];
                }
                line = lineArray[i];
                builder.append(line).append("\n");

                builder.append(messageChangeToHtml(EditHtmlList.DELETE_HTML, message));
                line = lineArray[i];
            }
            if (line.endsWith("<input type=\"hidden\" name=\"number\" value=\"\">")) {
                builder.append("            <input type=\"hidden\" name=\"number\" value=\"");
                builder.append(message.getMessageID()).append("\">").append("\n");

                line = lineArray[++i];
            }

            builder.append(line).append("\n");
        }
        return builder.toString();
    }

    /**
     * messageをHTML文章にする
     *
     * @param editHtmlList 編集したいHTMLのEnum
     * @param message      書き込みたいMessageのオブジェクト
     * @return HTML文章
     */
    static String messageChangeToHtml(EditHtmlList editHtmlList, Message message) {
        switch (editHtmlList) {
            case INDEX_HTML:
                return "            <tr id=\"No." + message.getMessageID() + "\">" + "\n" +
                        "                <td>No." + message.getMessageID() + "</td>" + "\n" +
                        "                <td>" + message.getTitle() + "</td>" + "\n" +
                        "                <td>" + message.getText() + "</td>" + "\n" +
                        "                <td>" + message.getName() + "</td>" + "\n" +
                        "                <td>" + message.getDate() + "</td>" + "\n" +
                        "                <td>\n" +
                        "                    <form action=\"/program/board/\" method=\"post\">\n" +
                        "                        <input type=\"hidden\" name=\"param\" value=\"search\">\n" +
                        "                        <input type=\"hidden\" name=\"number\" value=\"" + message.getMessageID() + "\">\n" +
                        "                        <input type=\"submit\" value=\"このコメントを投稿した人の他のコメントを見てみる\">\n" +
                        "                    </form>\n" +
                        "                </td>\n" +

                        "                <td>" + "\n" +
                        "                    <form action=\"/program/board/\" method=\"post\">" + "\n" +
                        "                        <input type=\"hidden\" name=\"param\" value=\"delete_step_1\">" + "\n" +
                        "                        <input type=\"hidden\" name=\"number\" value=\"" + message.getMessageID() + "\">" + "\n" +
                        "                        <input type=\"submit\" value=\"このコメントを削除する\">" + "\n" +
                        "                    </form>" + "\n" +
                        "                </td>" + "\n";

            case SEARCH_HTML:
                return "            <tr id=\"No." + message.getMessageID() + "\">" + "\n" +
                        "                <td>No." + message.getMessageID() + "</td>" + "\n" +
                        "                <td>" + message.getTitle() + "</td>" + "\n" +
                        "                <td>" + message.getText() + "</td>" + "\n" +
                        "                <td>" + message.getName() + "</td>" + "\n" +
                        "                <td>" + message.getDate() + "</td>" + "\n" +
                        "                <td>" + "\n" +
                        "                    <form action=\"/program/board/\" method=\"post\">" + "\n" +
                        "                        <input type=\"hidden\" name=\"param\" value=\"delete_step_1\">" + "\n" +
                        "                        <input type=\"hidden\" name=\"number\" value=\"" + message.getMessageID() + "\">" + "\n" +
                        "                        <input type=\"submit\" value=\"このコメントを削除する\">" + "\n" +
                        "                    </form>" + "\n" +
                        "                </td>" + "\n";

            case DELETE_HTML:
                return "            <tr id=\"No." + message.getMessageID() + "\">" + "\n" +
                        "                <td>No." + message.getMessageID() + "</td>" + "\n" +
                        "                <td>" + message.getTitle() + "</td>" + "\n" +
                        "                <td>" + message.getText() + "</td>" + "\n" +
                        "                <td>" + message.getName() + "</td>" + "\n" +
                        "                <td>" + message.getDate() + "</td>" + "\n";

            default:
                return null;
        }
    }


    /**
     * 改行コードをHTMLのbrタグに変更するメソッド
     *
     * @param str HTMLに書き込みたい文章を渡す
     * @return 改行コードを全てbrタグに修正して返す
     */
    static String changeLineFeedToBr(String str) {
        if (str.contains("\r\n")) {
            return str.replaceAll("\r\n", "<br>");

        } else if (str.contains("\n")) {
            return str.replaceAll("\n", "<br>");
        }

        return str;
    }

    /**
     * HTMLファイルに文章を書き込むメソッド
     *
     * @param editHtmlList 書き込みたいHTMLのEnum
     * @param html         書き込みたい文字列
     * @throws IOException 書き込み中の例外
     */
    void writeHtml(EditHtmlList editHtmlList, String html) throws IOException {
        try (OutputStream outputStream = new FileOutputStream(new File(editHtmlList.getPath()))) {
            outputStream.write(html.getBytes());
            outputStream.flush();
        }
    }
}

/**
 * URIとファイルのパスのEnum
 *
 * @author asada
 */
enum EditHtmlList {
    /**
     * index.html
     * URI、ファイルのパス
     */
    INDEX_HTML("/program/board/index.html", "./src/main/resources/2/index.html"),

    /**
     * search.html
     */
    SEARCH_HTML("/program/board/search.html", "./src/main/resources/2/search.html"),

    /**
     * delete.html
     */
    DELETE_HTML("/program/board/delete.html", "./src/main/resources/2/delete.html");

    private final String uri;

    private final String path;

    EditHtmlList(String uri, String path) {
        this.uri = uri;
        this.path = path;
    }

    /**
     * URIを取得するメソッド
     *
     * @return URIを返す
     */
    public String getUri() {
        return uri;
    }

    /**
     * ファイルのパスを取得するメソッド
     *
     * @return ファイルのパスを返す
     */
    public String getPath() {
        return path;
    }
}
