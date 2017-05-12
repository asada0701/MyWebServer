package jp.co.topgate.asada.web.app;

import jp.co.topgate.asada.web.model.Message;
import jp.co.topgate.asada.web.model.ModelController;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by yusuke-pc on 2017/05/09.
 */
public class HtmlEditorTest {
//    HtmlEditor sut;
//
//    @Before
//    public void setUp() throws Exception {
//        CsvWriter.setFilePath("./src/test/resources/data/message.csv");
//        ModelController.setMessageList(CsvWriter.readToMessage());
//        sut = new HtmlEditor();
//    }
//
//    @Test
//    public void コンストラクとallInitializationメソッドのテスト() throws Exception {
//        sut.allInitialization();
//        try (InputStream is = new FileInputStream(new File("./src/main/resources/2/index.html"));
//             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
//
//            assertThat(br.readLine(), is("<!DOCTYPE html>"));
//            assertThat(br.readLine(), is("<html>"));
//            assertThat(br.readLine(), is(""));
//            assertThat(br.readLine(), is("<head>"));
//            assertThat(br.readLine(), is("    <meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">"));
//            assertThat(br.readLine(), is("    <link rel=\"stylesheet\" type=\"text/css\" href=\"./css/style.css\">"));
//            assertThat(br.readLine(), is("</head>"));
//            assertThat(br.readLine(), is(""));
//            assertThat(br.readLine(), is("<body>"));
//            assertThat(br.readLine(), is("<center>"));
//            assertThat(br.readLine(), is("    <div id=\"header\">"));
//            assertThat(br.readLine(), is("        <h1>掲示板-LightBoard</h1>"));
//            assertThat(br.readLine(), is("        <a href=\"./caution.html\" target=\"_blank\">注意事項を読む</a>"));
//            assertThat(br.readLine(), is("    </div>"));
//            assertThat(br.readLine(), is("    <div id=\"form\">"));
//            assertThat(br.readLine(), is("        <form action=\"/program/board/\" method=\"post\">"));
//            assertThat(br.readLine(), is("            <p>"));
//            assertThat(br.readLine(), is("                名前<input type=\"text\" name=\"name\" size=\"40\" required>"));
//            assertThat(br.readLine(), is("            </p>"));
//            assertThat(br.readLine(), is("            <p>"));
//            assertThat(br.readLine(), is("                タイトル<input type=\"text\" name=\"title\" size=\"40\" required>"));
//            assertThat(br.readLine(), is("            </p>"));
//            assertThat(br.readLine(), is("            <p>"));
//            assertThat(br.readLine(), is("                メッセージ<br>"));
//            assertThat(br.readLine(), is("                <textarea name=\"text\" rows=\"4\" cols=\"40\" required></textarea>"));
//            assertThat(br.readLine(), is("            </p>"));
//            assertThat(br.readLine(), is("            <p>"));
//            assertThat(br.readLine(), is("                パスワード<input type=\"password\" name=\"password\" size=\"10\" required>(投稿した文を削除するときに使います。)"));
//            assertThat(br.readLine(), is("            </p>"));
//            assertThat(br.readLine(), is("            <input type=\"hidden\" name=\"param\" value=\"contribution\">"));
//            assertThat(br.readLine(), is("            <input type=\"submit\" value=\"投稿\">"));
//            assertThat(br.readLine(), is("        </form>"));
//            assertThat(br.readLine(), is("    </div>"));
//            assertThat(br.readLine(), is("    <div id=\"log\">"));
//            assertThat(br.readLine(), is("        <table border=\"1\">"));
//            assertThat(br.readLine(), is("            <tr>"));
//            assertThat(br.readLine(), is("                <th>ナンバー</th>"));
//            assertThat(br.readLine(), is("                <th>タイトル</th>"));
//            assertThat(br.readLine(), is("                <th>本文</th>"));
//            assertThat(br.readLine(), is("                <th>ユーザー名</th>"));
//            assertThat(br.readLine(), is("                <th>日付</th>"));
//            assertThat(br.readLine(), is("                <th></th>"));
//            assertThat(br.readLine(), is("                <th></th>"));
//            assertThat(br.readLine(), is("            </tr>"));
//            assertThat(br.readLine(), is("        </table>"));
//            assertThat(br.readLine(), is("    </div>"));
//            assertThat(br.readLine(), is("</center>"));
//            assertThat(br.readLine(), is("</body>"));
//            assertThat(br.readLine(), is(""));
//            assertThat(br.readLine(), is("</html>"));
//            assertThat(br.readLine(), is(nullValue()));
//        }
//    }
//
//    @Test
//    public void writeIndexHtmlメソッドのテスト() throws Exception {
//        HtmlEditor.writeIndexHtml();
//        try (InputStream is = new FileInputStream(new File("./src/main/resources/2/index.html"));
//             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
//
//            assertThat(br.readLine(), is("<!DOCTYPE html>"));
//            assertThat(br.readLine(), is("<html>"));
//            assertThat(br.readLine(), is(""));
//            assertThat(br.readLine(), is("<head>"));
//            assertThat(br.readLine(), is("    <meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">"));
//            assertThat(br.readLine(), is("    <link rel=\"stylesheet\" type=\"text/css\" href=\"./css/style.css\">"));
//            assertThat(br.readLine(), is("</head>"));
//            assertThat(br.readLine(), is(""));
//            assertThat(br.readLine(), is("<body>"));
//            assertThat(br.readLine(), is("<center>"));
//            assertThat(br.readLine(), is("    <div id=\"header\">"));
//            assertThat(br.readLine(), is("        <h1>掲示板-LightBoard</h1>"));
//            assertThat(br.readLine(), is("        <a href=\"./caution.html\" target=\"_blank\">注意事項を読む</a>"));
//            assertThat(br.readLine(), is("    </div>"));
//            assertThat(br.readLine(), is("    <div id=\"form\">"));
//            assertThat(br.readLine(), is("        <form action=\"/program/board/\" method=\"post\">"));
//            assertThat(br.readLine(), is("            <p>"));
//            assertThat(br.readLine(), is("                名前<input type=\"text\" name=\"name\" size=\"40\" required>"));
//            assertThat(br.readLine(), is("            </p>"));
//            assertThat(br.readLine(), is("            <p>"));
//            assertThat(br.readLine(), is("                タイトル<input type=\"text\" name=\"title\" size=\"40\" required>"));
//            assertThat(br.readLine(), is("            </p>"));
//            assertThat(br.readLine(), is("            <p>"));
//            assertThat(br.readLine(), is("                メッセージ<br>"));
//            assertThat(br.readLine(), is("                <textarea name=\"text\" rows=\"4\" cols=\"40\" required></textarea>"));
//            assertThat(br.readLine(), is("            </p>"));
//            assertThat(br.readLine(), is("            <p>"));
//            assertThat(br.readLine(), is("                パスワード<input type=\"password\" name=\"password\" size=\"10\" required>(投稿した文を削除するときに使います。)"));
//            assertThat(br.readLine(), is("            </p>"));
//            assertThat(br.readLine(), is("            <input type=\"hidden\" name=\"param\" value=\"contribution\">"));
//            assertThat(br.readLine(), is("            <input type=\"submit\" value=\"投稿\">"));
//            assertThat(br.readLine(), is("        </form>"));
//            assertThat(br.readLine(), is("    </div>"));
//            assertThat(br.readLine(), is("    <div id=\"log\">"));
//            assertThat(br.readLine(), is("        <table border=\"1\">"));
//            assertThat(br.readLine(), is("            <tr>"));
//            assertThat(br.readLine(), is("                <th>ナンバー</th>"));
//            assertThat(br.readLine(), is("                <th>タイトル</th>"));
//            assertThat(br.readLine(), is("                <th>本文</th>"));
//            assertThat(br.readLine(), is("                <th>ユーザー名</th>"));
//            assertThat(br.readLine(), is("                <th>日付</th>"));
//            assertThat(br.readLine(), is("                <th></th>"));
//            assertThat(br.readLine(), is("                <th></th>"));
//            assertThat(br.readLine(), is("            </tr>"));
//            assertThat(br.readLine(), is("            <tr id=\"No.4\">"));
//            assertThat(br.readLine(), is("                <td>No.4</td>"));
//            assertThat(br.readLine(), is("                <td>t</td>"));
//            assertThat(br.readLine(), is("                <td>そうですね</td>"));
//            assertThat(br.readLine(), is("                <td>管理者</td>"));
//            assertThat(br.readLine(), is("                <td>2017/5/11 11:57</td>"));
//            assertThat(br.readLine(), is("                <td>"));
//            assertThat(br.readLine(), is("                    <form action=\"/program/board/\" method=\"post\">"));
//            assertThat(br.readLine(), is("                        <input type=\"hidden\" name=\"param\" value=\"search\">"));
//            assertThat(br.readLine(), is("                        <input type=\"hidden\" name=\"number\" value=\"4\">"));
//            assertThat(br.readLine(), is("                        <input type=\"submit\" value=\"このコメントを投稿した人の他のコメントを見てみる\">"));
//            assertThat(br.readLine(), is("                    </form>"));
//            assertThat(br.readLine(), is("                </td>"));
//            assertThat(br.readLine(), is("                <td>"));
//            assertThat(br.readLine(), is("                    <form action=\"/program/board/\" method=\"post\">"));
//            assertThat(br.readLine(), is("                        <input type=\"hidden\" name=\"param\" value=\"delete1\">"));
//            assertThat(br.readLine(), is("                        <input type=\"hidden\" name=\"number\" value=\"4\">"));
//            assertThat(br.readLine(), is("                        <input type=\"submit\" value=\"このコメントを削除する\">"));
//            assertThat(br.readLine(), is("                    </form>"));
//            assertThat(br.readLine(), is("                </td>"));
//            assertThat(br.readLine(), is("            </tr>"));
//        }
//    }
//
//    @Test
//    public void writeSearchHtmlメソッドのテスト() throws Exception {
//        HtmlEditor.writeSearchHtml(4);
//        try (InputStream is = new FileInputStream(new File("./src/main/resources/2/search.html"));
//             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
//
//            assertThat(br.readLine(), is("<!DOCTYPE html>"));
//            assertThat(br.readLine(), is("<html>"));
//            assertThat(br.readLine(), is(""));
//            assertThat(br.readLine(), is("<head>"));
//            assertThat(br.readLine(), is("    <meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">"));
//            assertThat(br.readLine(), is("    <link rel=\"stylesheet\" type=\"text/css\" href=\"./css/style.css\">"));
//            assertThat(br.readLine(), is("</head>"));
//            assertThat(br.readLine(), is(""));
//            assertThat(br.readLine(), is("<body>"));
//            assertThat(br.readLine(), is("<center>"));
//            assertThat(br.readLine(), is("    <div id=\"header\">"));
//            assertThat(br.readLine(), is("        <h1>掲示板-LightBoard</h1>"));
//            assertThat(br.readLine(), is("        <a href=\"./caution.html\" target=\"_blank\">注意事項を読む</a>"));
//            assertThat(br.readLine(), is("    </div>"));
//            assertThat(br.readLine(), is("    <div id=\"form\">"));
//            assertThat(br.readLine(), is("        <form action=\"/program/board/\" method=\"post\">"));
//            assertThat(br.readLine(), is("            <p>"));
//            assertThat(br.readLine(), is("                名前<input type=\"text\" name=\"name\" size=\"40\" required>"));
//            assertThat(br.readLine(), is("            </p>"));
//            assertThat(br.readLine(), is("            <p>"));
//            assertThat(br.readLine(), is("                タイトル<input type=\"text\" name=\"title\" size=\"40\" required>"));
//            assertThat(br.readLine(), is("            </p>"));
//            assertThat(br.readLine(), is("            <p>"));
//            assertThat(br.readLine(), is("                メッセージ<br>"));
//            assertThat(br.readLine(), is("                <textarea name=\"text\" rows=\"4\" cols=\"40\" required></textarea>"));
//            assertThat(br.readLine(), is("            </p>"));
//            assertThat(br.readLine(), is("            <p>"));
//            assertThat(br.readLine(), is("                パスワード<input type=\"password\" name=\"password\" size=\"10\" required>(投稿した文を削除するときに使います。)"));
//            assertThat(br.readLine(), is("            </p>"));
//            assertThat(br.readLine(), is("            <input type=\"hidden\" name=\"param\" value=\"contribution\">"));
//            assertThat(br.readLine(), is("            <input type=\"submit\" value=\"投稿\">"));
//            assertThat(br.readLine(), is("        </form>"));
//            assertThat(br.readLine(), is("    </div>"));
//            assertThat(br.readLine(), is("    <div id=\"log\">"));
//            assertThat(br.readLine(), is("        <table border=\"1\">"));
//            assertThat(br.readLine(), is("            <tr>"));
//            assertThat(br.readLine(), is("                <th>ナンバー</th>"));
//            assertThat(br.readLine(), is("                <th>タイトル</th>"));
//            assertThat(br.readLine(), is("                <th>本文</th>"));
//            assertThat(br.readLine(), is("                <th>ユーザー名</th>"));
//            assertThat(br.readLine(), is("                <th>日付</th>"));
//            assertThat(br.readLine(), is("                <th></th>"));
//            assertThat(br.readLine(), is("            </tr>"));
//            assertThat(br.readLine(), is("            <tr id=\"No.4\">"));
//            assertThat(br.readLine(), is("                <td>No.4</td>"));
//            assertThat(br.readLine(), is("                <td>t</td>"));
//            assertThat(br.readLine(), is("                <td>そうですね</td>"));
//            assertThat(br.readLine(), is("                <td>管理者</td>"));
//            assertThat(br.readLine(), is("                <td>2017/5/11 11:57</td>"));
//            assertThat(br.readLine(), is("                <td>"));
//            assertThat(br.readLine(), is("                    <form action=\"/program/board/\" method=\"post\">"));
//            assertThat(br.readLine(), is("                        <input type=\"hidden\" name=\"param\" value=\"delete1\">"));
//            assertThat(br.readLine(), is("                        <input type=\"hidden\" name=\"number\" value=\"4\">"));
//            assertThat(br.readLine(), is("                        <input type=\"submit\" value=\"このコメントを削除する\">"));
//            assertThat(br.readLine(), is("                    </form>"));
//            assertThat(br.readLine(), is("                </td>"));
//            assertThat(br.readLine(), is("            </tr>"));
//        }
//    }
//
//    @Test
//    public void writeDeleteHtmlメソッドのテスト() throws Exception {
//        Message m = new Message();
//        m.setMessageID(2);
//        m.setPassword("t");
//        m.setName("asada");
//        m.setTitle("t");
//        m.setText("こんにちは");
//        m.setDate("2017/5/11 11:57");
//
//        HtmlEditor.writeDeleteHtml(m);
//
//        try (InputStream is = new FileInputStream(new File("./src/main/resources/2/delete.html"));
//             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
//
//            assertThat(br.readLine(), is("<!DOCTYPE html>"));
//            assertThat(br.readLine(), is("<html>"));
//            assertThat(br.readLine(), is(""));
//            assertThat(br.readLine(), is("<head>"));
//            assertThat(br.readLine(), is("    <meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">"));
//            assertThat(br.readLine(), is("    <link rel=\"stylesheet\" type=\"text/css\" href=\"./css/deleteStyle.css\">"));
//            assertThat(br.readLine(), is("</head>"));
//            assertThat(br.readLine(), is(""));
//            assertThat(br.readLine(), is("<body>"));
//            assertThat(br.readLine(), is("<center>"));
//            assertThat(br.readLine(), is("    <div id=\"header\">"));
//            assertThat(br.readLine(), is("        <h1>掲示板-LightBoard</h1>"));
//            assertThat(br.readLine(), is("        <a href=\"./caution.html\" target=\"_blank\">注意事項を読む</a>"));
//            assertThat(br.readLine(), is("    </div>"));
//            assertThat(br.readLine(), is("    <div id=\"log\">"));
//            assertThat(br.readLine(), is("        <h2>削除するメッセージ</h2>"));
//            assertThat(br.readLine(), is("        <table border=\"1\">"));
//            assertThat(br.readLine(), is("            <tr>"));
//            assertThat(br.readLine(), is("                <th>ナンバー</th>"));
//            assertThat(br.readLine(), is("                <th>タイトル</th>"));
//            assertThat(br.readLine(), is("                <th>本文</th>"));
//            assertThat(br.readLine(), is("                <th>ユーザー名</th>"));
//            assertThat(br.readLine(), is("                <th>日付</th>"));
//            assertThat(br.readLine(), is("            </tr>"));
//            assertThat(br.readLine(), is("            <tr id=\"No.2\">"));
//            assertThat(br.readLine(), is("                <td>No.2</td>"));
//            assertThat(br.readLine(), is("                <td>t</td>"));
//            assertThat(br.readLine(), is("                <td>こんにちは</td>"));
//            assertThat(br.readLine(), is("                <td>asada</td>"));
//            assertThat(br.readLine(), is("                <td>2017/5/11 11:57</td>"));
//            assertThat(br.readLine(), is("            </tr>"));
//        }
//    }
//
//    @After
//    public void tearDown() throws Exception {
//        sut.allInitialization();
//    }
}
