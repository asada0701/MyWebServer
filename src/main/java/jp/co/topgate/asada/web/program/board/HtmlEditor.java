package jp.co.topgate.asada.web.program.board;

import jp.co.topgate.asada.web.program.board.model.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * HTMLを編集するクラス
 *
 * @author asada
 */
class HtmlEditor {
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
     * index.htmlのページを編集するメソッド
     *
     * @param messageList 投稿されたメッセージのリストを渡す
     * @param timeID      Messageがもつユニークな値。二重リクエストを防ぐためのもの
     * @return 編集後のHTML文章を返す
     */
    static String editIndexHtml(List<Message> messageList, String timeID) {
        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE html>\n");
        builder.append("<html>\n");
        builder.append("\n");
        builder.append("<head>\n");
        builder.append("    <meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">\n");
        builder.append("    <link rel=\"stylesheet\" type=\"text/css\" href=\"./css/style.css\">\n");
        builder.append("</head>\n");
        builder.append("\n");
        builder.append("<body>\n");
        builder.append("<center>\n");
        builder.append("    <div id=\"header\">\n");
        builder.append("        <h1>掲示板-LightBoard</h1>\n");
        builder.append("        <a href=\"./caution.html\" target=\"_blank\">注意事項を読む</a>\n");
        builder.append("    </div>\n");
        builder.append("    <div id=\"form\">\n");
        builder.append("        <form action=\"/program/board/\" method=\"post\">\n");
        builder.append("            <p>\n");
        builder.append("                名前<input type=\"text\" name=\"name\" size=\"21\" maxlength=\"20\" required>(20文字まで)\n");
        builder.append("            </p>\n");
        builder.append("            <p>\n");
        builder.append("                タイトル<input type=\"text\" name=\"title\" size=\"21\" maxlength=\"20\" required>(20文字まで)\n");
        builder.append("            </p>\n");
        builder.append("            <p>\n");
        builder.append("                メッセージ<br>\n");
        builder.append("                <textarea name=\"text\" rows=\"5\" cols=\"42\" maxlength=\"200\" required></textarea>\n");
        builder.append("                <br>(200文字まで)\n");
        builder.append("            </p>\n");
        builder.append("            <p>\n");
        builder.append("                パスワード<input type=\"password\" name=\"password\" size=\"21\" maxlength=\"20\" required>(20文字まで)\n");
        builder.append("                <br>(投稿した文を削除するときに使います。)\n");
        builder.append("            </p>\n");
        builder.append("            <input type=\"hidden\" name=\"param\" value=\"write\">\n");
        builder.append("            <input type=\"hidden\" name=\"timeID\" value=\"").append(timeID).append("\">\n");
        builder.append("            <input type=\"submit\" value=\"投稿\">\n");
        builder.append("        </form>\n");
        builder.append("    </div>\n");
        builder.append("    <div id=\"log\">\n");
        builder.append("        <table border=\"1\" style=\"table-layout:fixed;width:100%;\">\n");
        builder.append("            <colgroup>\n");
        builder.append("                <col style=\"width:5%;\">\n");
        builder.append("                <col style=\"width:10%;\">\n");
        builder.append("                <col style=\"width:30%;\">\n");
        builder.append("                <col style=\"width:10%;\">\n");
        builder.append("                <col style=\"width:15%;\">\n");
        builder.append("                <col style=\"width:20%;\">\n");
        builder.append("                <col style=\"width:10%;\">\n");
        builder.append("            </colgroup>\n");
        builder.append("            <tbody>\n");
        builder.append("            <tr>\n");
        builder.append("                <th>No</th>\n");
        builder.append("                <th>タイトル</th>\n");
        builder.append("                <th>本文</th>\n");
        builder.append("                <th>ユーザー名</th>\n");
        builder.append("                <th>日付</th>\n");
        builder.append("                <th></th>\n");
        builder.append("                <th></th>\n");
        builder.append("            </tr>\n");

        for (int i = messageList.size() - 1; i >= 0; i--) {
            builder.append("            <tr id=\"No.").append(messageList.get(i).getMessageID()).append("\">\n");
            builder.append("                <td align=\"center\" style=\"word-wrap:break-word;\">No.").append(messageList.get(i).getMessageID()).append("</td>\n");
            builder.append("                <td align=\"center\" style=\"word-wrap:break-word;\">").append(messageList.get(i).getTitle()).append("</td>\n");
            builder.append("                <td align=\"center\" style=\"word-wrap:break-word;\">").append(messageList.get(i).getText()).append("</td>\n");
            builder.append("                <td align=\"center\" style=\"word-wrap:break-word;\">").append(messageList.get(i).getName()).append("</td>\n");
            builder.append("                <td align=\"center\" style=\"word-wrap:break-word;\">").append(messageList.get(i).getDate()).append("</td>\n");
            builder.append("                <td align=\"center\">\n");
            builder.append("                    <form action=\"/program/board/search.html\" method=\"get\">\n");
            builder.append("                        <input type=\"hidden\" name=\"param\" value=\"search\">\n");
            builder.append("                        <input type=\"hidden\" name=\"name\" value=\"").append(messageList.get(i).getName()).append("\">\n");
            builder.append("                        <input type=\"submit\" value=\"この人の他のコメントも見てみる\">\n");
            builder.append("                    </form>\n");
            builder.append("                </td>\n");
            builder.append("                <td align=\"center\">\n");
            builder.append("                    <form action=\"/program/board/\" method=\"post\">\n");
            builder.append("                        <input type=\"hidden\" name=\"param\" value=\"delete_step_1\">\n");
            builder.append("                        <input type=\"hidden\" name=\"number\" value=\"").append(messageList.get(i).getMessageID()).append("\">\n");
            builder.append("                        <input type=\"submit\" value=\"削除する\">\n");
            builder.append("                    </form>\n");
            builder.append("                </td>\n");
            builder.append("            </tr>\n");
        }

        builder.append("            </tbody>\n");
        builder.append("        </table>\n");
        builder.append("    </div>\n");
        builder.append("</center>\n");
        builder.append("</body>\n");
        builder.append("\n");
        builder.append("</html>\n");

        return builder.toString();
    }

    /**
     * search.htmlのページを編集するメソッド
     *
     * @param messageList 投稿されたメッセージのリストを渡す
     * @param timeID      Messageがもつユニークな値。二重リクエストを防ぐためのもの
     * @return 編集後のHTML文章を返す
     */
    static String editSearchHtml(List<Message> messageList, String timeID) {
        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE html>\n");
        builder.append("<html>\n");
        builder.append("\n");
        builder.append("<head>\n");
        builder.append("    <meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">\n");
        builder.append("    <link rel=\"stylesheet\" type=\"text/css\" href=\"./css/style.css\">\n");
        builder.append("</head>\n");
        builder.append("\n");
        builder.append("<body>\n");
        builder.append("<center>\n");
        builder.append("    <div id=\"header\">\n");
        builder.append("        <h1>掲示板-LightBoard</h1>\n");
        builder.append("        <a href=\"./caution.html\" target=\"_blank\">注意事項を読む</a>\n");
        builder.append("    </div>\n");
        builder.append("    <div id=\"form\">\n");
        builder.append("        <form action=\"/program/board/\" method=\"post\">\n");
        builder.append("            <p>\n");
        builder.append("                名前<input type=\"text\" name=\"name\" size=\"21\" maxlength=\"20\" required>(20文字まで)\n");
        builder.append("            </p>\n");
        builder.append("            <p>\n");
        builder.append("                タイトル<input type=\"text\" name=\"title\" size=\"21\" maxlength=\"20\" required>(20文字まで)\n");
        builder.append("            </p>\n");
        builder.append("            <p>\n");
        builder.append("                メッセージ<br>\n");
        builder.append("                <textarea name=\"text\" rows=\"5\" cols=\"42\" maxlength=\"200\" required></textarea>\n");
        builder.append("                <br>(200文字まで)\n");
        builder.append("            </p>\n");
        builder.append("            <p>\n");
        builder.append("                パスワード<input type=\"password\" name=\"password\" size=\"21\" maxlength=\"20\" required>(20文字まで)\n");
        builder.append("                <br>(投稿した文を削除するときに使います。)\n");
        builder.append("            </p>\n");
        builder.append("            <input type=\"hidden\" name=\"param\" value=\"write\">\n");
        builder.append("            <input type=\"hidden\" name=\"timeID\" value=\"").append(timeID).append("\">\n");
        builder.append("            <input type=\"submit\" value=\"投稿\">\n");
        builder.append("        </form>\n");
        builder.append("    </div>\n");
        builder.append("    <div id=\"log\">\n");
        builder.append("        <table border=\"1\" style=\"table-layout:fixed;width:90%;\">\n");
        builder.append("            <colgroup>\n");
        builder.append("                <col style=\"width:5%;\">\n");
        builder.append("                <col style=\"width:10%;\">\n");
        builder.append("                <col style=\"width:30%;\">\n");
        builder.append("                <col style=\"width:10%;\">\n");
        builder.append("                <col style=\"width:15%;\">\n");
        builder.append("                <col style=\"width:20%;\">\n");
        builder.append("            </colgroup>\n");
        builder.append("            <tbody>\n");
        builder.append("            <tr>\n");
        builder.append("                <th>No</th>\n");
        builder.append("                <th>タイトル</th>\n");
        builder.append("                <th>本文</th>\n");
        builder.append("                <th>ユーザー名</th>\n");
        builder.append("                <th>日付</th>\n");
        builder.append("                <th></th>\n");
        builder.append("            </tr>\n");

        for (int i = messageList.size() - 1; i >= 0; i--) {
            builder.append("            <tr id=\"No.").append(messageList.get(i).getMessageID()).append("\">\n");
            builder.append("                <td align=\"center\" style=\"word-wrap:break-word;\">No.").append(messageList.get(i).getMessageID()).append("</td>\n");
            builder.append("                <td align=\"center\" style=\"word-wrap:break-word;\">").append(messageList.get(i).getTitle()).append("</td>\n");
            builder.append("                <td align=\"center\" style=\"word-wrap:break-word;\">").append(messageList.get(i).getText()).append("</td>\n");
            builder.append("                <td align=\"center\" style=\"word-wrap:break-word;\">").append(messageList.get(i).getName()).append("</td>\n");
            builder.append("                <td align=\"center\" style=\"word-wrap:break-word;\">").append(messageList.get(i).getDate()).append("</td>\n");
            builder.append("                <td align=\"center\">\n");
            builder.append("                    <form action=\"/program/board/\" method=\"post\">\n");
            builder.append("                        <input type=\"hidden\" name=\"param\" value=\"delete_step_1\">\n");
            builder.append("                        <input type=\"hidden\" name=\"number\" value=\"").append(messageList.get(i).getMessageID()).append("\">\n");
            builder.append("                        <input type=\"submit\" value=\"削除する\">\n");
            builder.append("                    </form>\n");
            builder.append("                </td>\n");
            builder.append("            </tr>\n");
        }

        builder.append("            </tbody>\n");
        builder.append("        </table>\n");
        builder.append("    </div>\n");
        builder.append("    <div id=\"back\">\n");
        builder.append("        <form action=\"/program/board/\" method=\"post\">\n");
        builder.append("            <input type=\"hidden\" name=\"param\" value=\"back\">\n");
        builder.append("            <input type=\"submit\" value=\"topへ戻る\">\n");
        builder.append("        </form>\n");
        builder.append("    </div>\n");
        builder.append("</center>\n");
        builder.append("</body>\n");
        builder.append("\n");
        builder.append("</html>\n");

        return builder.toString();
    }

    /**
     * delete.htmlのページを編集するメソッド
     *
     * @param message メッセージのオブジェクトを渡す
     * @return 編集後のHTML文章を返す
     */
    static String editDeleteHtml(Message message) {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "\n" +
                "<head>\n" +
                "    <meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">\n" +
                "    <link rel=\"stylesheet\" type=\"text/css\" href=\"./css/deleteStyle.css\">\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                "<center>\n" +
                "    <div id=\"header\">\n" +
                "        <h1>掲示板-LightBoard</h1>\n" +
                "        <a href=\"./caution.html\" target=\"_blank\">注意事項を読む</a>\n" +
                "    </div>\n" +
                "    <div id=\"log\">\n" +
                "        <h2>削除するメッセージ</h2>\n" +
                "        <table border=\"1\" style=\"table-layout:fixed;width:80%;\">\n" +
                "            <colgroup>\n" +
                "                <col style=\"width:5%;\">\n" +
                "                <col style=\"width:10%;\">\n" +
                "                <col style=\"width:30%;\">\n" +
                "                <col style=\"width:10%;\">\n" +
                "                <col style=\"width:15%;\">\n" +
                "            </colgroup>\n" +
                "            <tbody>\n" +
                "            <tr>\n" +
                "                <th>No</th>\n" +
                "                <th>タイトル</th>\n" +
                "                <th>本文</th>\n" +
                "                <th>ユーザー名</th>\n" +
                "                <th>日付</th>\n" +
                "            </tr>\n" +
                "            <tr id=\"No." + message.getMessageID() + "\">\n" +
                "                <td align=\"center\" style=\"word-wrap:break-word;\">No." + message.getMessageID() + "</td>\n" +
                "                <td align=\"center\" style=\"word-wrap:break-word;\">" + message.getTitle() + "</td>\n" +
                "                <td align=\"center\" style=\"word-wrap:break-word;\">" + message.getText() + "</td>\n" +
                "                <td align=\"center\" style=\"word-wrap:break-word;\">" + message.getName() + "</td>\n" +
                "                <td align=\"center\" style=\"word-wrap:break-word;\">" + message.getDate() + "</td>\n" +
                "            </tr>\n" +
                "            </tbody>\n" +
                "        </table>\n" +
                "    </div>\n" +
                "    <div id=\"form\">\n" +
                "        <p>投稿した時に入力したパスワードを入力してください。</p>\n" +
                "        <form action=\"/program/board/\" method=\"post\">\n" +
                "            <p>\n" +
                "                パスワード<input type=\"password\" name=\"password\" size=\"10\" required>\n" +
                "            </p>\n" +
                "            <input type=\"hidden\" name=\"number\" value=\"" + message.getMessageID() + "\">\n" +
                "            <input type=\"hidden\" name=\"param\" value=\"delete_step_2\">\n" +
                "            <input type=\"submit\" value=\"削除する\">\n" +
                "        </form>\n" +
                "    </div>\n" +
                "    <div id=\"back\">\n" +
                "        <form action=\"/program/board/\" method=\"post\">\n" +
                "            <input type=\"hidden\" name=\"param\" value=\"back\">\n" +
                "            <input type=\"submit\" value=\"topへ戻る\">\n" +
                "        </form>\n" +
                "    </div>\n" +
                "</center>\n" +
                "</body>\n" +
                "\n" +
                "</html>\n";
    }

    /**
     * 投稿したメッセージの削除に成功した場合。結果をユーザーに知らせる結果画面を取得するメソッド
     *
     * @return HTML文章を返す
     */
    static String getResultHtml() {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "\n" +
                "<head>\n" +
                "    <meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">\n" +
                "    <meta http-equiv=\"refresh\" content=\"5;URL=index.html\">\n" +
                "    <link rel=\"stylesheet\" type=\"text/css\" href=\"./css/deleteStyle.css\">\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                "<center>\n" +
                "    <div id=\"header\">\n" +
                "        <h1>掲示板-LightBoard</h1>\n" +
                "    </div>\n" +
                "    <div id=\"result\">\n" +
                "        <h2>5秒後に自動的に掲示板へ戻ります。</h2>\n" +
                "        <p>削除しました。</p>\n" +
                "    </div>\n" +
                "    <div id=\"back\">\n" +
                "        <form action=\"/program/board/\" method=\"post\">\n" +
                "            <input type=\"hidden\" name=\"param\" value=\"back\">\n" +
                "            <input type=\"submit\" value=\"topへ戻る\">\n" +
                "        </form>\n" +
                "    </div>\n" +
                "</center>\n" +
                "</body>\n" +
                "\n" +
                "</html>";
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
