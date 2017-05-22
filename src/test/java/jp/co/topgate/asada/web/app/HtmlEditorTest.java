package jp.co.topgate.asada.web.app;

import jp.co.topgate.asada.web.model.Message;
import org.junit.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * HtmlEditorクラスのテスト
 *
 * @author asada
 */
public class HtmlEditorTest {
    private List<Message> messageList = new ArrayList<>();
    private HtmlEditor htmlEditor;

    @Before
    public void setUp() {
        htmlEditor = new HtmlEditor();

        Message m;
        m = new Message();
        m.setMessageID(1);
        m.setPassword("test");
        m.setName("管理者");
        m.setTitle("test");
        m.setText("こんにちは");
        m.setDate("2017/5/11 11:56");
        messageList.add(m);

        m = new Message();
        m.setMessageID(2);
        m.setPassword("t");
        m.setName("asada");
        m.setTitle("t");
        m.setText("こんにちは");
        m.setDate("2017/5/11 11:57");
        messageList.add(m);

        m = new Message();
        m.setMessageID(3);
        m.setPassword("t");
        m.setName("asada");
        m.setTitle("t");
        m.setText("今日は天気がいいですね");
        m.setDate("2017/5/11 11:57");
        messageList.add(m);

        m = new Message();
        m.setMessageID(4);
        m.setPassword("t");
        m.setName("管理者");
        m.setTitle("t");
        m.setText("そうですね");
        m.setDate("2017/5/11 11:57");
        messageList.add(m);
    }

    @Test
    public void editIndexOrSearchHtmlメソッドのテスト() {
        HtmlEditor sut = new HtmlEditor();
        EditHtmlList indexEnum = EditHtmlList.INDEX_HTML;
        EditHtmlList searchEnum = EditHtmlList.SEARCH_HTML;

        String resultIndex = sut.editIndexOrSearchHtml(indexEnum, messageList);
        String resultSearch = sut.editIndexOrSearchHtml(searchEnum, messageList);

        String[] index = resultIndex.split("\n");
        String[] search = resultSearch.split("\n");

        int i = 0;
        assertThat(index[i++], is("<!DOCTYPE html>"));
        assertThat(index[i++], is("<html>"));
        assertThat(index[i++], is(""));
        assertThat(index[i++], is("<head>"));
        assertThat(index[i++], is("    <meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">"));
        assertThat(index[i++], is("    <link rel=\"stylesheet\" type=\"text/css\" href=\"./css/style.css\">"));
        assertThat(index[i++], is("</head>"));
        assertThat(index[i++], is(""));
        assertThat(index[i++], is("<body>"));
        assertThat(index[i++], is("<center>"));
        assertThat(index[i++], is("    <div id=\"header\">"));
        assertThat(index[i++], is("        <h1>掲示板-LightBoard</h1>"));
        assertThat(index[i++], is("        <a href=\"./caution.html\" target=\"_blank\">注意事項を読む</a>"));
        assertThat(index[i++], is("    </div>"));
        assertThat(index[i++], is("    <div id=\"form\">"));
        assertThat(index[i++], is("        <form action=\"/program/board/\" method=\"post\">"));
        assertThat(index[i++], is("            <p>"));
        assertThat(index[i++], is("                名前<input type=\"text\" name=\"name\" size=\"40\" required>"));
        assertThat(index[i++], is("            </p>"));
        assertThat(index[i++], is("            <p>"));
        assertThat(index[i++], is("                タイトル<input type=\"text\" name=\"title\" size=\"40\" required>"));
        assertThat(index[i++], is("            </p>"));
        assertThat(index[i++], is("            <p>"));
        assertThat(index[i++], is("                メッセージ<br>"));
        assertThat(index[i++], is("                <textarea name=\"text\" rows=\"4\" cols=\"40\" required></textarea>"));
        assertThat(index[i++], is("            </p>"));
        assertThat(index[i++], is("            <p>"));
        assertThat(index[i++], is("                パスワード<input type=\"password\" name=\"password\" size=\"10\" required>(投稿した文を削除するときに使います。)"));
        assertThat(index[i++], is("            </p>"));
        assertThat(index[i++], is("            <input type=\"hidden\" name=\"param\" value=\"contribution\">"));
        assertThat(index[i++], is("            <input type=\"submit\" value=\"投稿\">"));
        assertThat(index[i++], is("        </form>"));
        assertThat(index[i++], is("    </div>"));
        assertThat(index[i++], is("    <div id=\"log\">"));
        assertThat(index[i++], is("        <table border=\"1\">"));
        assertThat(index[i++], is("            <tr>"));
        assertThat(index[i++], is("                <th>ナンバー</th>"));
        assertThat(index[i++], is("                <th>タイトル</th>"));
        assertThat(index[i++], is("                <th>本文</th>"));
        assertThat(index[i++], is("                <th>ユーザー名</th>"));
        assertThat(index[i++], is("                <th>日付</th>"));
        assertThat(index[i++], is("                <th></th>"));
        assertThat(index[i++], is("                <th></th>"));
        assertThat(index[i++], is("            </tr>"));
        assertThat(index[i++], is("            <tr id=\"No.4\">"));
        assertThat(index[i++], is("                <td>No.4</td>"));
        assertThat(index[i++], is("                <td>t</td>"));
        assertThat(index[i++], is("                <td>そうですね</td>"));
        assertThat(index[i++], is("                <td>管理者</td>"));
        assertThat(index[i++], is("                <td>2017/5/11 11:57</td>"));
        assertThat(index[i++], is("                <td>"));
        assertThat(index[i++], is("                    <form action=\"/program/board/\" method=\"post\">"));
        assertThat(index[i++], is("                        <input type=\"hidden\" name=\"param\" value=\"search\">"));
        assertThat(index[i++], is("                        <input type=\"hidden\" name=\"number\" value=\"4\">"));
        assertThat(index[i++], is("                        <input type=\"submit\" value=\"このコメントを投稿した人の他のコメントを見てみる\">"));
        assertThat(index[i++], is("                    </form>"));
        assertThat(index[i++], is("                </td>"));
        assertThat(index[i++], is("                <td>"));
        assertThat(index[i++], is("                    <form action=\"/program/board/\" method=\"post\">"));
        assertThat(index[i++], is("                        <input type=\"hidden\" name=\"param\" value=\"delete1\">"));
        assertThat(index[i++], is("                        <input type=\"hidden\" name=\"number\" value=\"4\">"));
        assertThat(index[i++], is("                        <input type=\"submit\" value=\"このコメントを削除する\">"));
        assertThat(index[i++], is("                    </form>"));
        assertThat(index[i++], is("                </td>"));
        assertThat(index[i++], is("            </tr>"));
        assertThat(index[i++], is("            <tr id=\"No.3\">"));
        assertThat(index[i++], is("                <td>No.3</td>"));
        assertThat(index[i++], is("                <td>t</td>"));
        assertThat(index[i++], is("                <td>今日は天気がいいですね</td>"));
        assertThat(index[i++], is("                <td>asada</td>"));
        assertThat(index[i++], is("                <td>2017/5/11 11:57</td>"));
        assertThat(index[i++], is("                <td>"));
        assertThat(index[i++], is("                    <form action=\"/program/board/\" method=\"post\">"));
        assertThat(index[i++], is("                        <input type=\"hidden\" name=\"param\" value=\"search\">"));
        assertThat(index[i++], is("                        <input type=\"hidden\" name=\"number\" value=\"3\">"));
        assertThat(index[i++], is("                        <input type=\"submit\" value=\"このコメントを投稿した人の他のコメントを見てみる\">"));
        assertThat(index[i++], is("                    </form>"));
        assertThat(index[i++], is("                </td>"));
        assertThat(index[i++], is("                <td>"));
        assertThat(index[i++], is("                    <form action=\"/program/board/\" method=\"post\">"));
        assertThat(index[i++], is("                        <input type=\"hidden\" name=\"param\" value=\"delete1\">"));
        assertThat(index[i++], is("                        <input type=\"hidden\" name=\"number\" value=\"3\">"));
        assertThat(index[i++], is("                        <input type=\"submit\" value=\"このコメントを削除する\">"));
        assertThat(index[i++], is("                    </form>"));
        assertThat(index[i++], is("                </td>"));
        assertThat(index[i++], is("            </tr>"));
        assertThat(index[i++], is("            <tr id=\"No.2\">"));
        assertThat(index[i++], is("                <td>No.2</td>"));
        assertThat(index[i++], is("                <td>t</td>"));
        assertThat(index[i++], is("                <td>こんにちは</td>"));
        assertThat(index[i++], is("                <td>asada</td>"));
        assertThat(index[i++], is("                <td>2017/5/11 11:57</td>"));
        assertThat(index[i++], is("                <td>"));
        assertThat(index[i++], is("                    <form action=\"/program/board/\" method=\"post\">"));
        assertThat(index[i++], is("                        <input type=\"hidden\" name=\"param\" value=\"search\">"));
        assertThat(index[i++], is("                        <input type=\"hidden\" name=\"number\" value=\"2\">"));
        assertThat(index[i++], is("                        <input type=\"submit\" value=\"このコメントを投稿した人の他のコメントを見てみる\">"));
        assertThat(index[i++], is("                    </form>"));
        assertThat(index[i++], is("                </td>"));
        assertThat(index[i++], is("                <td>"));
        assertThat(index[i++], is("                    <form action=\"/program/board/\" method=\"post\">"));
        assertThat(index[i++], is("                        <input type=\"hidden\" name=\"param\" value=\"delete1\">"));
        assertThat(index[i++], is("                        <input type=\"hidden\" name=\"number\" value=\"2\">"));
        assertThat(index[i++], is("                        <input type=\"submit\" value=\"このコメントを削除する\">"));
        assertThat(index[i++], is("                    </form>"));
        assertThat(index[i++], is("                </td>"));
        assertThat(index[i++], is("            </tr>"));
        assertThat(index[i++], is("            <tr id=\"No.1\">"));
        assertThat(index[i++], is("                <td>No.1</td>"));
        assertThat(index[i++], is("                <td>test</td>"));
        assertThat(index[i++], is("                <td>こんにちは</td>"));
        assertThat(index[i++], is("                <td>管理者</td>"));
        assertThat(index[i++], is("                <td>2017/5/11 11:56</td>"));
        assertThat(index[i++], is("                <td>"));
        assertThat(index[i++], is("                    <form action=\"/program/board/\" method=\"post\">"));
        assertThat(index[i++], is("                        <input type=\"hidden\" name=\"param\" value=\"search\">"));
        assertThat(index[i++], is("                        <input type=\"hidden\" name=\"number\" value=\"1\">"));
        assertThat(index[i++], is("                        <input type=\"submit\" value=\"このコメントを投稿した人の他のコメントを見てみる\">"));
        assertThat(index[i++], is("                    </form>"));
        assertThat(index[i++], is("                </td>"));
        assertThat(index[i++], is("                <td>"));
        assertThat(index[i++], is("                    <form action=\"/program/board/\" method=\"post\">"));
        assertThat(index[i++], is("                        <input type=\"hidden\" name=\"param\" value=\"delete1\">"));
        assertThat(index[i++], is("                        <input type=\"hidden\" name=\"number\" value=\"1\">"));
        assertThat(index[i++], is("                        <input type=\"submit\" value=\"このコメントを削除する\">"));
        assertThat(index[i++], is("                    </form>"));
        assertThat(index[i++], is("                </td>"));
        assertThat(index[i++], is("            </tr>"));
        assertThat(index[i++], is("        </table>"));
        assertThat(index[i++], is("    </div>"));
        assertThat(index[i++], is("</center>"));
        assertThat(index[i++], is("</body>"));
        assertThat(index[i++], is(""));
        assertThat(index[i], is("</html>"));

        i = 0;
        assertThat(search[i++], is("<!DOCTYPE html>"));
        assertThat(search[i++], is("<html>"));
        assertThat(search[i++], is(""));
        assertThat(search[i++], is("<head>"));
        assertThat(search[i++], is("    <meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">"));
        assertThat(search[i++], is("    <link rel=\"stylesheet\" type=\"text/css\" href=\"./css/style.css\">"));
        assertThat(search[i++], is("</head>"));
        assertThat(search[i++], is(""));
        assertThat(search[i++], is("<body>"));
        assertThat(search[i++], is("<center>"));
        assertThat(search[i++], is("    <div id=\"header\">"));
        assertThat(search[i++], is("        <h1>掲示板-LightBoard</h1>"));
        assertThat(search[i++], is("        <a href=\"./caution.html\" target=\"_blank\">注意事項を読む</a>"));
        assertThat(search[i++], is("    </div>"));
        assertThat(search[i++], is("    <div id=\"form\">"));
        assertThat(search[i++], is("        <form action=\"/program/board/\" method=\"post\">"));
        assertThat(search[i++], is("            <p>"));
        assertThat(search[i++], is("                名前<input type=\"text\" name=\"name\" size=\"40\" required>"));
        assertThat(search[i++], is("            </p>"));
        assertThat(search[i++], is("            <p>"));
        assertThat(search[i++], is("                タイトル<input type=\"text\" name=\"title\" size=\"40\" required>"));
        assertThat(search[i++], is("            </p>"));
        assertThat(search[i++], is("            <p>"));
        assertThat(search[i++], is("                メッセージ<br>"));
        assertThat(search[i++], is("                <textarea name=\"text\" rows=\"4\" cols=\"40\" required></textarea>"));
        assertThat(search[i++], is("            </p>"));
        assertThat(search[i++], is("            <p>"));
        assertThat(search[i++], is("                パスワード<input type=\"password\" name=\"password\" size=\"10\" required>(投稿した文を削除するときに使います。)"));
        assertThat(search[i++], is("            </p>"));
        assertThat(search[i++], is("            <input type=\"hidden\" name=\"param\" value=\"contribution\">"));
        assertThat(search[i++], is("            <input type=\"submit\" value=\"投稿\">"));
        assertThat(search[i++], is("        </form>"));
        assertThat(search[i++], is("    </div>"));
        assertThat(search[i++], is("    <div id=\"log\">"));
        assertThat(search[i++], is("        <table border=\"1\">"));
        assertThat(search[i++], is("            <tr>"));
        assertThat(search[i++], is("                <th>ナンバー</th>"));
        assertThat(search[i++], is("                <th>タイトル</th>"));
        assertThat(search[i++], is("                <th>本文</th>"));
        assertThat(search[i++], is("                <th>ユーザー名</th>"));
        assertThat(search[i++], is("                <th>日付</th>"));
        assertThat(search[i++], is("                <th></th>"));
        assertThat(search[i++], is("            </tr>"));
        assertThat(search[i++], is("            <tr id=\"No.4\">"));
        assertThat(search[i++], is("                <td>No.4</td>"));
        assertThat(search[i++], is("                <td>t</td>"));
        assertThat(search[i++], is("                <td>そうですね</td>"));
        assertThat(search[i++], is("                <td>管理者</td>"));
        assertThat(search[i++], is("                <td>2017/5/11 11:57</td>"));
        assertThat(search[i++], is("                <td>"));
        assertThat(search[i++], is("                    <form action=\"/program/board/\" method=\"post\">"));
        assertThat(search[i++], is("                        <input type=\"hidden\" name=\"param\" value=\"delete1\">"));
        assertThat(search[i++], is("                        <input type=\"hidden\" name=\"number\" value=\"4\">"));
        assertThat(search[i++], is("                        <input type=\"submit\" value=\"このコメントを削除する\">"));
        assertThat(search[i++], is("                    </form>"));
        assertThat(search[i++], is("                </td>"));
        assertThat(search[i++], is("            </tr>"));
        assertThat(search[i++], is("            <tr id=\"No.3\">"));
        assertThat(search[i++], is("                <td>No.3</td>"));
        assertThat(search[i++], is("                <td>t</td>"));
        assertThat(search[i++], is("                <td>今日は天気がいいですね</td>"));
        assertThat(search[i++], is("                <td>asada</td>"));
        assertThat(search[i++], is("                <td>2017/5/11 11:57</td>"));
        assertThat(search[i++], is("                <td>"));
        assertThat(search[i++], is("                    <form action=\"/program/board/\" method=\"post\">"));
        assertThat(search[i++], is("                        <input type=\"hidden\" name=\"param\" value=\"delete1\">"));
        assertThat(search[i++], is("                        <input type=\"hidden\" name=\"number\" value=\"3\">"));
        assertThat(search[i++], is("                        <input type=\"submit\" value=\"このコメントを削除する\">"));
        assertThat(search[i++], is("                    </form>"));
        assertThat(search[i++], is("                </td>"));
        assertThat(search[i++], is("            </tr>"));
        assertThat(search[i++], is("            <tr id=\"No.2\">"));
        assertThat(search[i++], is("                <td>No.2</td>"));
        assertThat(search[i++], is("                <td>t</td>"));
        assertThat(search[i++], is("                <td>こんにちは</td>"));
        assertThat(search[i++], is("                <td>asada</td>"));
        assertThat(search[i++], is("                <td>2017/5/11 11:57</td>"));
        assertThat(search[i++], is("                <td>"));
        assertThat(search[i++], is("                    <form action=\"/program/board/\" method=\"post\">"));
        assertThat(search[i++], is("                        <input type=\"hidden\" name=\"param\" value=\"delete1\">"));
        assertThat(search[i++], is("                        <input type=\"hidden\" name=\"number\" value=\"2\">"));
        assertThat(search[i++], is("                        <input type=\"submit\" value=\"このコメントを削除する\">"));
        assertThat(search[i++], is("                    </form>"));
        assertThat(search[i++], is("                </td>"));
        assertThat(search[i++], is("            </tr>"));
        assertThat(search[i++], is("            <tr id=\"No.1\">"));
        assertThat(search[i++], is("                <td>No.1</td>"));
        assertThat(search[i++], is("                <td>test</td>"));
        assertThat(search[i++], is("                <td>こんにちは</td>"));
        assertThat(search[i++], is("                <td>管理者</td>"));
        assertThat(search[i++], is("                <td>2017/5/11 11:56</td>"));
        assertThat(search[i++], is("                <td>"));
        assertThat(search[i++], is("                    <form action=\"/program/board/\" method=\"post\">"));
        assertThat(search[i++], is("                        <input type=\"hidden\" name=\"param\" value=\"delete1\">"));
        assertThat(search[i++], is("                        <input type=\"hidden\" name=\"number\" value=\"1\">"));
        assertThat(search[i++], is("                        <input type=\"submit\" value=\"このコメントを削除する\">"));
        assertThat(search[i++], is("                    </form>"));
        assertThat(search[i++], is("                </td>"));
        assertThat(search[i++], is("            </tr>"));
        assertThat(search[i++], is("        </table>"));
        assertThat(search[i++], is("    </div>"));
        assertThat(search[i++], is("    <div id=\"back\">"));
        assertThat(search[i++], is("        <form action=\"/program/board/\" method=\"post\">"));
        assertThat(search[i++], is("            <input type=\"hidden\" name=\"param\" value=\"back\">"));
        assertThat(search[i++], is("            <input type=\"submit\" value=\"topへ戻る\">"));
        assertThat(search[i++], is("        </form>"));
        assertThat(search[i++], is("    </div>"));
        assertThat(search[i++], is("</center>"));
        assertThat(search[i++], is("</body>"));
        assertThat(search[i++], is(""));
        assertThat(search[i], is("</html>"));

    }

    @Test
    public void editDeleteメソッドのテスト() {
        HtmlEditor sut = new HtmlEditor();
        Message m;
        m = new Message();
        m.setMessageID(1);
        m.setPassword("test");
        m.setName("管理者");
        m.setTitle("test");
        m.setText("こんにちは");
        m.setDate("2017/5/11 11:56");

        String resultHtml = sut.editDeleteHtml(m);

        String[] delete = resultHtml.split("\n");

        int i = 0;
        assertThat(delete[i++], is("<!DOCTYPE html>"));
        assertThat(delete[i++], is("<html>"));
        assertThat(delete[i++], is(""));
        assertThat(delete[i++], is("<head>"));
        assertThat(delete[i++], is("    <meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">"));
        assertThat(delete[i++], is("    <link rel=\"stylesheet\" type=\"text/css\" href=\"./css/deleteStyle.css\">"));
        assertThat(delete[i++], is("</head>"));
        assertThat(delete[i++], is(""));
        assertThat(delete[i++], is("<body>"));
        assertThat(delete[i++], is("<center>"));
        assertThat(delete[i++], is("    <div id=\"header\">"));
        assertThat(delete[i++], is("        <h1>掲示板-LightBoard</h1>"));
        assertThat(delete[i++], is("        <a href=\"./caution.html\" target=\"_blank\">注意事項を読む</a>"));
        assertThat(delete[i++], is("    </div>"));
        assertThat(delete[i++], is("    <div id=\"log\">"));
        assertThat(delete[i++], is("        <h2>削除するメッセージ</h2>"));
        assertThat(delete[i++], is("        <table border=\"1\">"));
        assertThat(delete[i++], is("            <tr>"));
        assertThat(delete[i++], is("                <th>ナンバー</th>"));
        assertThat(delete[i++], is("                <th>タイトル</th>"));
        assertThat(delete[i++], is("                <th>本文</th>"));
        assertThat(delete[i++], is("                <th>ユーザー名</th>"));
        assertThat(delete[i++], is("                <th>日付</th>"));
        assertThat(delete[i++], is("            </tr>"));
        assertThat(delete[i++], is("            <tr id=\"No.1\">"));
        assertThat(delete[i++], is("                <td>No.1</td>"));
        assertThat(delete[i++], is("                <td>test</td>"));
        assertThat(delete[i++], is("                <td>こんにちは</td>"));
        assertThat(delete[i++], is("                <td>管理者</td>"));
        assertThat(delete[i++], is("                <td>2017/5/11 11:56</td>"));
        assertThat(delete[i++], is("            </tr>"));
        assertThat(delete[i++], is("        </table>"));
        assertThat(delete[i++], is("    </div>"));
        assertThat(delete[i++], is("    <div id=\"form\">"));
        assertThat(delete[i++], is("        <p>投稿した時に入力したパスワードを入力してください。</p>"));
        assertThat(delete[i++], is("        <form action=\"/program/board/\" method=\"post\">"));
        assertThat(delete[i++], is("            <p>"));
        assertThat(delete[i++], is("                パスワード<input type=\"password\" name=\"password\" size=\"10\" required>"));
        assertThat(delete[i++], is("            </p>"));
        assertThat(delete[i++], is("            <input type=\"hidden\" name=\"number\" value=\"1\">"));
        assertThat(delete[i++], is("            <input type=\"hidden\" name=\"param\" value=\"delete2\">"));
        assertThat(delete[i++], is("            <input type=\"submit\" value=\"削除する\">"));
        assertThat(delete[i++], is("        </form>"));
        assertThat(delete[i++], is("    </div>"));
        assertThat(delete[i++], is("    <div id=\"back\">"));
        assertThat(delete[i++], is("        <form action=\"/program/board/\" method=\"post\">"));
        assertThat(delete[i++], is("            <input type=\"hidden\" name=\"param\" value=\"back\">"));
        assertThat(delete[i++], is("            <input type=\"submit\" value=\"戻る\">"));
        assertThat(delete[i++], is("        </form>"));
        assertThat(delete[i++], is("    </div>"));
        assertThat(delete[i++], is("</center>"));
        assertThat(delete[i++], is("</body>"));
        assertThat(delete[i++], is(""));
        assertThat(delete[i++], is("</html>"));
    }

    @Test
    public void messageChangeToHtmlメソッドのテスト() {
        Message m;
        m = new Message();
        m.setMessageID(1);
        m.setPassword("test");
        m.setName("管理者");
        m.setTitle("test");
        m.setText("こんにちは");
        m.setDate("2017/5/11 11:56");
        String index = HtmlEditor.messageChangeToHtml(EditHtmlList.INDEX_HTML, m);
        String search = HtmlEditor.messageChangeToHtml(EditHtmlList.SEARCH_HTML, m);
        String delete = HtmlEditor.messageChangeToHtml(EditHtmlList.DELETE_HTML, m);

        String[] result = index.split("\n");
        int i = 0;
        assertThat(result[i++], is("            <tr id=\"No.1\">"));
        assertThat(result[i++], is("                <td>No.1</td>"));
        assertThat(result[i++], is("                <td>test</td>"));
        assertThat(result[i++], is("                <td>こんにちは</td>"));
        assertThat(result[i++], is("                <td>管理者</td>"));
        assertThat(result[i++], is("                <td>2017/5/11 11:56</td>"));
        assertThat(result[i++], is("                <td>"));
        assertThat(result[i++], is("                    <form action=\"/program/board/\" method=\"post\">"));
        assertThat(result[i++], is("                        <input type=\"hidden\" name=\"param\" value=\"search\">"));
        assertThat(result[i++], is("                        <input type=\"hidden\" name=\"number\" value=\"1\">"));
        assertThat(result[i++], is("                        <input type=\"submit\" value=\"このコメントを投稿した人の他のコメントを見てみる\">"));
        assertThat(result[i++], is("                    </form>"));
        assertThat(result[i++], is("                </td>"));
        assertThat(result[i++], is("                <td>"));
        assertThat(result[i++], is("                    <form action=\"/program/board/\" method=\"post\">"));
        assertThat(result[i++], is("                        <input type=\"hidden\" name=\"param\" value=\"delete1\">"));
        assertThat(result[i++], is("                        <input type=\"hidden\" name=\"number\" value=\"1\">"));
        assertThat(result[i++], is("                        <input type=\"submit\" value=\"このコメントを削除する\">"));
        assertThat(result[i++], is("                    </form>"));
        assertThat(result[i], is("                </td>"));

        result = search.split("\n");
        i = 0;
        assertThat(result[i++], is("            <tr id=\"No.1\">"));
        assertThat(result[i++], is("                <td>No.1</td>"));
        assertThat(result[i++], is("                <td>test</td>"));
        assertThat(result[i++], is("                <td>こんにちは</td>"));
        assertThat(result[i++], is("                <td>管理者</td>"));
        assertThat(result[i++], is("                <td>2017/5/11 11:56</td>"));
        assertThat(result[i++], is("                <td>"));
        assertThat(result[i++], is("                    <form action=\"/program/board/\" method=\"post\">"));
        assertThat(result[i++], is("                        <input type=\"hidden\" name=\"param\" value=\"delete1\">"));
        assertThat(result[i++], is("                        <input type=\"hidden\" name=\"number\" value=\"1\">"));
        assertThat(result[i++], is("                        <input type=\"submit\" value=\"このコメントを削除する\">"));
        assertThat(result[i++], is("                    </form>"));
        assertThat(result[i], is("                </td>"));

        result = delete.split("\n");
        i = 0;
        assertThat(result[i++], is("            <tr id=\"No.1\">"));
        assertThat(result[i++], is("                <td>No.1</td>"));
        assertThat(result[i++], is("                <td>test</td>"));
        assertThat(result[i++], is("                <td>こんにちは</td>"));
        assertThat(result[i++], is("                <td>管理者</td>"));
        assertThat(result[i], is("                <td>2017/5/11 11:56</td>"));
    }

    @Test
    public void changeLineFeedToBrメソッドのテスト() throws Exception {
        String raw = "";
        String data = HtmlEditor.changeLineFeedToBr(raw);
        assertThat(data, is(""));

        raw = "改行\nテスト";
        data = HtmlEditor.changeLineFeedToBr(raw);
        assertThat(data, is("改行<br>テスト"));

        raw = "改行\r\nテスト";
        data = HtmlEditor.changeLineFeedToBr(raw);
        assertThat(data, is("改行<br>テスト"));

        raw = "複数\r\n改行\r\nテスト";
        data = HtmlEditor.changeLineFeedToBr(raw);
        assertThat(data, is("複数<br>改行<br>テスト"));
    }

    @Test
    public void writeHtmlメソッドのテスト() throws Exception {
        HtmlEditor he = new HtmlEditor();

        he.writeHtml(EditHtmlList.INDEX_HTML, "test");

        String path = "./src/main/resources/2/index.html";
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path))))) {
            assertThat(br.readLine(), is("test"));
        }

        he.allInitialization();
    }

    @After
    public void tearDown() {
        htmlEditor.allInitialization();
    }
}
