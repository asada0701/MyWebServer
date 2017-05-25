package jp.co.topgate.asada.web.program.board;

import jp.co.topgate.asada.web.exception.HtmlInitializeException;
import jp.co.topgate.asada.web.model.Message;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

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
    private static final String DIV_ID_LOG = "<div id=\"log\">";

    /**
     * div id = "log"の後の t の閉じタグの次からメッセージを追加する
     */
    private static final String INSERT_MESSAGE_FROM = "</tr>";

    /**
     * 編集するHTMLの初期状態を保存するマップ
     */
    private static Map<ProgramBoardHtmlList, String> rawHtml = new HashMap<>();

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

    /**
     * コンストラクタ
     * HTMLファイルの初期状態を保存する
     *
     * @throws HtmlInitializeException HTMLファイルの読み込み中にエラー発生
     */
    public HtmlEditor() throws HtmlInitializeException {
        for (ProgramBoardHtmlList programBoardHtmlList : ProgramBoardHtmlList.values()) {
            try (BufferedReader br = new BufferedReader(new FileReader(new File(programBoardHtmlList.getPath())))) {
                String line;
                StringBuilder builder = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    builder.append(line).append("\n");
                }
                rawHtml.put(programBoardHtmlList, builder.toString());

            } catch (IOException e) {
                throw new HtmlInitializeException(e.getMessage(), e.getCause());
            }
        }
    }

    /**
     * 登録されているHTMLファイルを初期化する
     *
     * @throws HtmlInitializeException HTMLファイルに書き込み中にエラー発生
     */
    public void resetAllFiles() throws HtmlInitializeException {
        for (ProgramBoardHtmlList programBoardHtmlList : ProgramBoardHtmlList.values()) {
            try (OutputStream outputStream = new FileOutputStream(new File(programBoardHtmlList.getPath()))) {
                outputStream.write(rawHtml.get(programBoardHtmlList).getBytes());
                outputStream.flush();
            } catch (IOException e) {
                throw new HtmlInitializeException(e.getMessage(), e.getCause());
            }
        }
    }

    /**
     * indexまたはsearchのHTML文章を編集する
     *
     * @param programBoardHtmlList 編集したいHTMLのEnum
     * @param messageList          HTML文章に書き込みたいMessageのリスト
     * @return 編集後のHTML文章
     */
    String editIndexOrSearchHtml(ProgramBoardHtmlList programBoardHtmlList, List<Message> messageList, String timeID) {
        String[] lineArray = rawHtml.get(programBoardHtmlList).split("\n");     //改行で分割
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
     * deleteのHTML文章を編集する
     *
     * @param message メッセージのオブジェクト
     * @return 編集後のHTML文章
     */
    String editDeleteHtml(Message message) {
        String[] lineArray = rawHtml.get(ProgramBoardHtmlList.DELETE_HTML).split("\n");     //改行で分割
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
     * messageをHTML文章にする
     *
     * @param programBoardHtmlList 編集したいHTMLのEnum
     * @param message              書き込みたいMessageのオブジェクト
     * @return HTML文章
     */
    static String changeMessageToHtml(ProgramBoardHtmlList programBoardHtmlList, Message message) {
        switch (programBoardHtmlList) {
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
    static String changeLineFeedToBrTag(String str) {
        for (String lineFeed : lineFeedPattern) {
            str = str.replaceAll(lineFeed, LINE_FEED_HTML);
        }
        return str;
    }

    /**
     * HTMLファイルに文章を書き込むメソッド
     *
     * @param programBoardHtmlList 書き込みたいHTMLのEnum
     * @param html                 書き込みたい文字列
     * @throws IOException 書き込み中の例外
     */
    void writeHtml(ProgramBoardHtmlList programBoardHtmlList, String html) throws IOException {
        try (OutputStream outputStream = new FileOutputStream(new File(programBoardHtmlList.getPath()))) {
            outputStream.write(html.getBytes());
            outputStream.flush();
        }
    }

    /**
     * 渡されたファイルパス先にあるファイルを読み込み、Stringで返す。画像ファイルなど読み込みたい場合には使用しないこと。
     *
     * @param path 読み込みたいファイルパス
     * @return ファイルの内容をStringで返す
     * @throws IOException ファイルが存在しないか、読み込めなかった。
     */
    String readHtml(String path) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(new File(path)))) {
            StringBuilder builder = new StringBuilder();
            String str;
            while ((str = br.readLine()) != null) {
                builder.append(str).append("\n");
            }

            return builder.toString();
        }
    }

    /**
     * HTMLページに書き込むID（二重リクエスト防ぐためのもの）を発行するメソッド
     *
     * @return エンコードされて発行する
     */
    String issueTimeIdInHtml() {
        LocalDateTime ldt = LocalDateTime.now();
        String timeID = "" + ldt.getYear() + ldt.getMonthValue() + ldt.getDayOfMonth() + ldt.getHour() +
                ldt.getMinute() + ldt.getSecond() + ldt.getNano();
        return Base64.getEncoder().encodeToString(timeID.getBytes());
    }
}

/**
 * 編集するHTMLのリスト
 *
 * @author asada
 */
enum ProgramBoardHtmlList {
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
    DELETE_HTML("/program/board/delete.html", "./src/main/resources/2/delete.html"),

    /**
     * result.html
     */
    RESULT_HTML("/program/board/result.html", "./src/main/resources/2/result.html");

    private final String uri;

    private final String path;

    ProgramBoardHtmlList(String uri, String path) {
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
