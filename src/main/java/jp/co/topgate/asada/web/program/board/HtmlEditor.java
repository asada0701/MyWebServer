package jp.co.topgate.asada.web.program.board;

import jp.co.topgate.asada.web.program.board.model.Message;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

/**
 * HTMLを編集するクラス
 *
 * @author asada
 */
class HtmlEditor {

    /**
     * HTMLの編集する場所のスタート地点を
     * div id = "log" で探す
     */
    private static final String DIV_ID_LOG = "<div id=\"log\">";

    /**
     * div id = "log"の後の t の閉じタグの次からメッセージを追加する
     */
    private static final String INSERT_MESSAGE_FROM = "</tr>";

    /**
     * ブラウザから送られてきた文字列に含まれている改行コードを
     * HTMLのbrタグに置換する際に使用するリスト
     */
    private static List<String> lineFeedPattern = new ArrayList<>();

    static {
        lineFeedPattern.add("\r\n");
        lineFeedPattern.add("\n");
    }

    /**
     * HTML文章で改行を表すbrタグ
     */
    private static final String LINE_FEED_HTML = "<br>";


    private HtmlEditor() {

    }

    /**
     * indexまたはsearchのHTML文章を編集し、編集したHTML文章を文字列で返す
     *
     * @param programBoardHtmlList 編集したいHTMLのEnum
     * @param messageList          HTML文章に書き込みたいMessageのリスト
     * @return 編集後のHTML文章
     * @throws IOException {@link HtmlEditor#readHtml(Path)}を参照
     */
    static String editIndexOrSearchHtml(ProgramBoardHtmlList programBoardHtmlList, List<Message> messageList, String timeID) throws IOException {
        Path filePath = programBoardHtmlList.getPath();
        String[] lineArray = readHtml(filePath).split("\n");
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < lineArray.length; i++) {
            String line = lineArray[i];

            if (line.startsWith("            <input type=\"hidden\" name=\"timeID\" value=\"")) {
                builder.append("            <input type=\"hidden\" name=\"timeID\" value=\"");
                builder.append(timeID).append("\">").append("\n");
                continue;
            }

            if (line.endsWith(DIV_ID_LOG)) {         //<div id="log">の箇所を探す
                while (!line.endsWith(INSERT_MESSAGE_FROM)) {            //</tr>の次の行から編集する
                    builder.append(line).append("\n");
                    line = lineArray[++i];
                }
                builder.append(lineArray[i]).append("\n");  //</tr>をappend

                for (int k = messageList.size() - 1; k > -1; k--) {                //ここからが掲示板のメッセージの部分
                    builder.append(changeMessageToHtml(programBoardHtmlList, messageList.get(k)));
                    builder.append(line).append("\n");
                }
                line = lineArray[++i];
            }
            builder.append(line).append("\n");
        }
        return builder.toString();
    }

    /**
     * deleteのHTML文章を編集し、編集したHTML文章を文字列で返す
     *
     * @param message メッセージのオブジェクト
     * @return 編集後のHTML文章
     * @throws IOException {@link HtmlEditor#readHtml(Path)}を参照
     */
    static String editDeleteHtml(Message message) throws IOException {
        Path filePath = ProgramBoardHtmlList.DELETE_HTML.getPath();
        String[] lineArray = readHtml(filePath).split("\n");
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < lineArray.length; i++) {
            String line = lineArray[i];
            if (line.endsWith(DIV_ID_LOG)) {         //<div id="log">の箇所を探す
                while (!line.endsWith(INSERT_MESSAGE_FROM)) {            //</tr>の次の行から編集する
                    builder.append(line).append("\n");
                    line = lineArray[++i];
                }
                line = lineArray[i];
                builder.append(line).append("\n");

                builder.append(changeMessageToHtml(ProgramBoardHtmlList.DELETE_HTML, message));
                line = lineArray[i];
            }
            if (line.startsWith("            <input type=\"hidden\" name=\"number\" value=\"")) {
                builder.append("            <input type=\"hidden\" name=\"number\" value=\"");
                builder.append(message.getMessageID()).append("\">").append("\n");
                continue;
            }

            builder.append(line).append("\n");
        }
        return builder.toString();
    }

    /**
     * HTMLファイルを読み込み、ファイルの内容を返す
     *
     * @param filePath 読み込みたいファイルのパスを渡す
     * @return 読み込んだファイルを返す
     * @throws IOException HTMLファイルの読み込み中にエラー発生
     */
    static String readHtml(Path filePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath.toFile()))) {
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                builder.append(line).append("\n");
            }
            return builder.toString();
        }
    }

    /**
     * Messageのオブジェクトを受け取り、各HTMLに沿ったHTML文章に整形する
     *
     * @param programBoardHtmlList 編集したいHTMLのEnum
     * @param message              書き込みたいMessageのオブジェクト
     * @return HTML文章
     */
    static String changeMessageToHtml(ProgramBoardHtmlList programBoardHtmlList, Message message) {
        switch (programBoardHtmlList) {
            case INDEX_HTML:
                return "            <tr id=\"No." + message.getMessageID() + "\">" + "\n" +
                        "                <td align=\"center\" style=\"word-wrap:break-word;\">No." + message.getMessageID() + "</td>" + "\n" +
                        "                <td align=\"center\" style=\"word-wrap:break-word;\">" + message.getTitle() + "</td>" + "\n" +
                        "                <td align=\"center\" style=\"word-wrap:break-word;\">" + message.getText() + "</td>" + "\n" +
                        "                <td align=\"center\" style=\"word-wrap:break-word;\">" + message.getName() + "</td>" + "\n" +
                        "                <td align=\"center\" style=\"word-wrap:break-word;\">" + message.getDate() + "</td>" + "\n" +
                        "                <td align=\"center\">\n" +
                        "                    <form action=\"/program/board/search.html\" method=\"get\">\n" +
                        "                        <input type=\"hidden\" name=\"param\" value=\"search\">\n" +
                        "                        <input type=\"hidden\" name=\"name\" value=\"" + message.getName() + "\">\n" +
                        "                        <input type=\"submit\" value=\"この人の他のコメントも見てみる\">\n" +
                        "                    </form>\n" +
                        "                </td>\n" +
                        "                <td align=\"center\">" + "\n" +
                        "                    <form action=\"/program/board/\" method=\"post\">" + "\n" +
                        "                        <input type=\"hidden\" name=\"param\" value=\"delete_step_1\">" + "\n" +
                        "                        <input type=\"hidden\" name=\"number\" value=\"" + message.getMessageID() + "\">" + "\n" +
                        "                        <input type=\"submit\" value=\"削除する\">" + "\n" +
                        "                    </form>" + "\n" +
                        "                </td>" + "\n";

            case SEARCH_HTML:
                return "            <tr id=\"No." + message.getMessageID() + "\">" + "\n" +
                        "                <td align=\"center\" style=\"word-wrap:break-word;\">No." + message.getMessageID() + "</td>" + "\n" +
                        "                <td align=\"center\" style=\"word-wrap:break-word;\">" + message.getTitle() + "</td>" + "\n" +
                        "                <td align=\"center\" style=\"word-wrap:break-word;\">" + message.getText() + "</td>" + "\n" +
                        "                <td align=\"center\" style=\"word-wrap:break-word;\">" + message.getName() + "</td>" + "\n" +
                        "                <td align=\"center\" style=\"word-wrap:break-word;\">" + message.getDate() + "</td>" + "\n" +
                        "                <td align=\"center\">" + "\n" +
                        "                    <form action=\"/program/board/\" method=\"post\">" + "\n" +
                        "                        <input type=\"hidden\" name=\"param\" value=\"delete_step_1\">" + "\n" +
                        "                        <input type=\"hidden\" name=\"number\" value=\"" + message.getMessageID() + "\">" + "\n" +
                        "                        <input type=\"submit\" value=\"削除する\">" + "\n" +
                        "                    </form>" + "\n" +
                        "                </td>" + "\n";

            case DELETE_HTML:
                return "            <tr id=\"No." + message.getMessageID() + "\">" + "\n" +
                        "                <td align=\"center\" style=\"word-wrap:break-word;\">No." + message.getMessageID() + "</td>" + "\n" +
                        "                <td align=\"center\" style=\"word-wrap:break-word;\">" + message.getTitle() + "</td>" + "\n" +
                        "                <td align=\"center\" style=\"word-wrap:break-word;\">" + message.getText() + "</td>" + "\n" +
                        "                <td align=\"center\" style=\"word-wrap:break-word;\">" + message.getName() + "</td>" + "\n" +
                        "                <td align=\"center\" style=\"word-wrap:break-word;\">" + message.getDate() + "</td>" + "\n";

            default:
                return null;
        }
    }

    /**
     * 改行コードをHTMLのbrタグに変更するメソッド
     * 改行文字を<br>タグに変更しないと、CSVファイル内で改行が発生してしまう
     *
     * @param str HTMLに書き込みたい文章を渡す
     * @return 改行コードを全てbrタグに修正して返す
     */
    static String changeLineFeedToBrTag(String str) {
        for (String lineFeed : lineFeedPattern) {
            str = str.replaceAll(lineFeed, LINE_FEED_HTML);
        }
        return str;
    }
}
