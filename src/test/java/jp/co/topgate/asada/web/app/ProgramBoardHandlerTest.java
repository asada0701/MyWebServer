package jp.co.topgate.asada.web.app;

import jp.co.topgate.asada.web.RequestMessage;
import jp.co.topgate.asada.web.ResponseMessage;
import jp.co.topgate.asada.web.StaticHandler;
import jp.co.topgate.asada.web.exception.RequestParseException;
import jp.co.topgate.asada.web.model.Message;
import jp.co.topgate.asada.web.model.ModelController;
import org.hamcrest.core.IsNull;
import org.junit.*;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.springframework.security.access.method.P;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * ProgramBoardHandlerクラスのテスト
 *
 * @author asada
 */
@RunWith(Enclosed.class)
public class ProgramBoardHandlerTest {
    public static class コンストラクタのテスト {
        @Test
        public void コンストラクタ() throws Exception {
            RequestMessage rm;

            String path = "./src/test/resources/GetRequestMessage.txt";
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(path)))) {
                rm = new RequestMessage(bis);
            }
            ProgramBoardHandler sut = new ProgramBoardHandler(rm);

            assertThat(sut.getRequestMessage(), is(rm));
            assertThat(sut.getHtmlEditor(), is(instanceOf(HtmlEditor.class)));
        }
    }

    public static class doRequestProcessメソッドのテスト {
        static HtmlEditor he;
        static List<Message> testList = new ArrayList<>();

        @Before
        public void setUp() throws Exception {
            he = new HtmlEditor();

            Message m = new Message();
            m.setMessageID(1);
            m.setPassword("test");
            m.setName("管理者");
            m.setTitle("test");
            m.setText("こんにちは");
            m.setDate("2017/5/11 11:56");
            testList.add(m);

            m = new Message();
            m.setMessageID(2);
            m.setPassword("t");
            m.setName("asada");
            m.setTitle("t");
            m.setText("こんにちは");
            m.setDate("2017/5/11 11:57");
            testList.add(m);

            ModelController.setMessageList(testList);
        }

        @Test
        public void GETリクエストURIはindexで200() throws Exception {
            String path = "./src/test/resources/getProgramBoard.txt";
            ProgramBoardHandler handler;
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(path)))) {
                handler = new ProgramBoardHandler(new RequestMessage(bis));
            }
            StatusLine sut = handler.doRequestProcess();

            assertThat(sut.getStatusCode(), is(200));

            path = "./src/main/resources/2/index.html";
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path))))) {
                assertThat(br.readLine(), is("<!DOCTYPE html>"));
                assertThat(br.readLine(), is("<html>"));
                assertThat(br.readLine(), is(""));
                assertThat(br.readLine(), is("<head>"));
                assertThat(br.readLine(), is("    <meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">"));
                assertThat(br.readLine(), is("    <link rel=\"stylesheet\" type=\"text/css\" href=\"./css/style.css\">"));
                assertThat(br.readLine(), is("</head>"));
                assertThat(br.readLine(), is(""));
                assertThat(br.readLine(), is("<body>"));
                assertThat(br.readLine(), is("<center>"));
                assertThat(br.readLine(), is("    <div id=\"header\">"));
                assertThat(br.readLine(), is("        <h1>掲示板-LightBoard</h1>"));
                assertThat(br.readLine(), is("        <a href=\"./caution.html\" target=\"_blank\">注意事項を読む</a>"));
                assertThat(br.readLine(), is("    </div>"));
                assertThat(br.readLine(), is("    <div id=\"form\">"));
                assertThat(br.readLine(), is("        <form action=\"/program/board/\" method=\"post\">"));
                assertThat(br.readLine(), is("            <p>"));
                assertThat(br.readLine(), is("                名前<input type=\"text\" name=\"name\" size=\"40\" required>"));
                assertThat(br.readLine(), is("            </p>"));
                assertThat(br.readLine(), is("            <p>"));
                assertThat(br.readLine(), is("                タイトル<input type=\"text\" name=\"title\" size=\"40\" required>"));
                assertThat(br.readLine(), is("            </p>"));
                assertThat(br.readLine(), is("            <p>"));
                assertThat(br.readLine(), is("                メッセージ<br>"));
                assertThat(br.readLine(), is("                <textarea name=\"text\" rows=\"4\" cols=\"40\" required></textarea>"));
                assertThat(br.readLine(), is("            </p>"));
                assertThat(br.readLine(), is("            <p>"));
                assertThat(br.readLine(), is("                パスワード<input type=\"password\" name=\"password\" size=\"10\" required>(投稿した文を削除するときに使います。)"));
                assertThat(br.readLine(), is("            </p>"));
                assertThat(br.readLine(), is("            <input type=\"hidden\" name=\"param\" value=\"contribution\">"));
                assertThat(br.readLine(), is("            <input type=\"submit\" value=\"投稿\">"));
                assertThat(br.readLine(), is("        </form>"));
                assertThat(br.readLine(), is("    </div>"));
                assertThat(br.readLine(), is("    <div id=\"log\">"));
                assertThat(br.readLine(), is("        <table border=\"1\">"));
                assertThat(br.readLine(), is("            <tr>"));
                assertThat(br.readLine(), is("                <th>ナンバー</th>"));
                assertThat(br.readLine(), is("                <th>タイトル</th>"));
                assertThat(br.readLine(), is("                <th>本文</th>"));
                assertThat(br.readLine(), is("                <th>ユーザー名</th>"));
                assertThat(br.readLine(), is("                <th>日付</th>"));
                assertThat(br.readLine(), is("                <th></th>"));
                assertThat(br.readLine(), is("                <th></th>"));
                assertThat(br.readLine(), is("            </tr>"));
                assertThat(br.readLine(), is("            <tr id=\"No.2\">"));
                assertThat(br.readLine(), is("                <td>No.2</td>"));
                assertThat(br.readLine(), is("                <td>t</td>"));
                assertThat(br.readLine(), is("                <td>こんにちは</td>"));
                assertThat(br.readLine(), is("                <td>asada</td>"));
                assertThat(br.readLine(), is("                <td>2017/5/11 11:57</td>"));
                assertThat(br.readLine(), is("                <td>"));
                assertThat(br.readLine(), is("                    <form action=\"/program/board/\" method=\"post\">"));
                assertThat(br.readLine(), is("                        <input type=\"hidden\" name=\"param\" value=\"search\">"));
                assertThat(br.readLine(), is("                        <input type=\"hidden\" name=\"number\" value=\"2\">"));
                assertThat(br.readLine(), is("                        <input type=\"submit\" value=\"このコメントを投稿した人の他のコメントを見てみる\">"));
                assertThat(br.readLine(), is("                    </form>"));
                assertThat(br.readLine(), is("                </td>"));
                assertThat(br.readLine(), is("                <td>"));
                assertThat(br.readLine(), is("                    <form action=\"/program/board/\" method=\"post\">"));
                assertThat(br.readLine(), is("                        <input type=\"hidden\" name=\"param\" value=\"delete1\">"));
                assertThat(br.readLine(), is("                        <input type=\"hidden\" name=\"number\" value=\"2\">"));
                assertThat(br.readLine(), is("                        <input type=\"submit\" value=\"このコメントを削除する\">"));
                assertThat(br.readLine(), is("                    </form>"));
                assertThat(br.readLine(), is("                </td>"));
                assertThat(br.readLine(), is("            </tr>"));
                assertThat(br.readLine(), is("            <tr id=\"No.1\">"));
                assertThat(br.readLine(), is("                <td>No.1</td>"));
                assertThat(br.readLine(), is("                <td>test</td>"));
                assertThat(br.readLine(), is("                <td>こんにちは</td>"));
                assertThat(br.readLine(), is("                <td>管理者</td>"));
                assertThat(br.readLine(), is("                <td>2017/5/11 11:56</td>"));
                assertThat(br.readLine(), is("                <td>"));
                assertThat(br.readLine(), is("                    <form action=\"/program/board/\" method=\"post\">"));
                assertThat(br.readLine(), is("                        <input type=\"hidden\" name=\"param\" value=\"search\">"));
                assertThat(br.readLine(), is("                        <input type=\"hidden\" name=\"number\" value=\"1\">"));
                assertThat(br.readLine(), is("                        <input type=\"submit\" value=\"このコメントを投稿した人の他のコメントを見てみる\">"));
                assertThat(br.readLine(), is("                    </form>"));
                assertThat(br.readLine(), is("                </td>"));
                assertThat(br.readLine(), is("                <td>"));
                assertThat(br.readLine(), is("                    <form action=\"/program/board/\" method=\"post\">"));
                assertThat(br.readLine(), is("                        <input type=\"hidden\" name=\"param\" value=\"delete1\">"));
                assertThat(br.readLine(), is("                        <input type=\"hidden\" name=\"number\" value=\"1\">"));
                assertThat(br.readLine(), is("                        <input type=\"submit\" value=\"このコメントを削除する\">"));
                assertThat(br.readLine(), is("                    </form>"));
                assertThat(br.readLine(), is("                </td>"));
                assertThat(br.readLine(), is("            </tr>"));
                assertThat(br.readLine(), is("        </table>"));
                assertThat(br.readLine(), is("    </div>"));
                assertThat(br.readLine(), is("</center>"));
                assertThat(br.readLine(), is("</body>"));
                assertThat(br.readLine(), is(""));
                assertThat(br.readLine(), is("</html>"));
            }

            he.allInitialization();
        }

        @Test
        public void GETリクエストURIはindex以外で200() throws Exception {
            String path = "./src/test/resources/GetRequestMessage.txt";
            ProgramBoardHandler handler;
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(path)))) {
                handler = new ProgramBoardHandler(new RequestMessage(bis));
            }
            StatusLine sut = handler.doRequestProcess();

            assertThat(sut.getStatusCode(), is(200));

            he.allInitialization();
        }

        @Test
        public void POSTリクエストでURIを間違えてみる() throws Exception {
            String path = "./src/test/resources/NotContainsUrlPatternTest.txt";
            ProgramBoardHandler handler;
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(path)))) {
                handler = new ProgramBoardHandler(new RequestMessage(bis));
            }
            StatusLine sut = handler.doRequestProcess();

            assertThat(sut.getStatusCode(), is(400));

            he.allInitialization();
        }

//        @Test
//        public void POSTでsearchを編集してみる() throws Exception {
//
//        }

        @After
        public void tearDown() throws Exception {
            he.allInitialization();
        }
    }

//    public static class doResponseProcessメソッドのテスト {
//
//    }

    public static class doPostメソッドのテスト {
        static HtmlEditor he;

        @Before
        public void setUp() {
            he = new HtmlEditor();
            List<Message> messageList = new ArrayList<>();
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

            ModelController.setMessageList(messageList);
        }

        @Test
        public void switchのsearchをテスト() throws Exception {
            String path = "./src/test/resources/PostSearchTest.txt";
            ProgramBoardHandler handler;
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(path)))) {
                handler = new ProgramBoardHandler(new RequestMessage(bis));
            }
            StatusLine sut = handler.doRequestProcess();
            assertThat(sut.getStatusCode(), is(200));

            path = "./src/main/resources/2/search.html";
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path))))) {
                assertThat(br.readLine(), is("<!DOCTYPE html>"));
                assertThat(br.readLine(), is("<html>"));
                assertThat(br.readLine(), is(""));
                assertThat(br.readLine(), is("<head>"));
                assertThat(br.readLine(), is("    <meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">"));
                assertThat(br.readLine(), is("    <link rel=\"stylesheet\" type=\"text/css\" href=\"./css/style.css\">"));
                assertThat(br.readLine(), is("</head>"));
                assertThat(br.readLine(), is(""));
                assertThat(br.readLine(), is("<body>"));
                assertThat(br.readLine(), is("<center>"));
                assertThat(br.readLine(), is("    <div id=\"header\">"));
                assertThat(br.readLine(), is("        <h1>掲示板-LightBoard</h1>"));
                assertThat(br.readLine(), is("        <a href=\"./caution.html\" target=\"_blank\">注意事項を読む</a>"));
                assertThat(br.readLine(), is("    </div>"));
                assertThat(br.readLine(), is("    <div id=\"form\">"));
                assertThat(br.readLine(), is("        <form action=\"/program/board/\" method=\"post\">"));
                assertThat(br.readLine(), is("            <p>"));
                assertThat(br.readLine(), is("                名前<input type=\"text\" name=\"name\" size=\"40\" required>"));
                assertThat(br.readLine(), is("            </p>"));
                assertThat(br.readLine(), is("            <p>"));
                assertThat(br.readLine(), is("                タイトル<input type=\"text\" name=\"title\" size=\"40\" required>"));
                assertThat(br.readLine(), is("            </p>"));
                assertThat(br.readLine(), is("            <p>"));
                assertThat(br.readLine(), is("                メッセージ<br>"));
                assertThat(br.readLine(), is("                <textarea name=\"text\" rows=\"4\" cols=\"40\" required></textarea>"));
                assertThat(br.readLine(), is("            </p>"));
                assertThat(br.readLine(), is("            <p>"));
                assertThat(br.readLine(), is("                パスワード<input type=\"password\" name=\"password\" size=\"10\" required>(投稿した文を削除するときに使います。)"));
                assertThat(br.readLine(), is("            </p>"));
                assertThat(br.readLine(), is("            <input type=\"hidden\" name=\"param\" value=\"contribution\">"));
                assertThat(br.readLine(), is("            <input type=\"submit\" value=\"投稿\">"));
                assertThat(br.readLine(), is("        </form>"));
                assertThat(br.readLine(), is("    </div>"));
                assertThat(br.readLine(), is("    <div id=\"log\">"));
                assertThat(br.readLine(), is("        <table border=\"1\">"));
                assertThat(br.readLine(), is("            <tr>"));
                assertThat(br.readLine(), is("                <th>ナンバー</th>"));
                assertThat(br.readLine(), is("                <th>タイトル</th>"));
                assertThat(br.readLine(), is("                <th>本文</th>"));
                assertThat(br.readLine(), is("                <th>ユーザー名</th>"));
                assertThat(br.readLine(), is("                <th>日付</th>"));
                assertThat(br.readLine(), is("                <th></th>"));
                assertThat(br.readLine(), is("            </tr>"));
                assertThat(br.readLine(), is("            <tr id=\"No.3\">"));
                assertThat(br.readLine(), is("                <td>No.3</td>"));
                assertThat(br.readLine(), is("                <td>t</td>"));
                assertThat(br.readLine(), is("                <td>今日は天気がいいですね</td>"));
                assertThat(br.readLine(), is("                <td>asada</td>"));
                assertThat(br.readLine(), is("                <td>2017/5/11 11:57</td>"));
                assertThat(br.readLine(), is("                <td>"));
                assertThat(br.readLine(), is("                    <form action=\"/program/board/\" method=\"post\">"));
                assertThat(br.readLine(), is("                        <input type=\"hidden\" name=\"param\" value=\"delete1\">"));
                assertThat(br.readLine(), is("                        <input type=\"hidden\" name=\"number\" value=\"3\">"));
                assertThat(br.readLine(), is("                        <input type=\"submit\" value=\"このコメントを削除する\">"));
                assertThat(br.readLine(), is("                    </form>"));
                assertThat(br.readLine(), is("                </td>"));
                assertThat(br.readLine(), is("            </tr>"));
                assertThat(br.readLine(), is("            <tr id=\"No.2\">"));
                assertThat(br.readLine(), is("                <td>No.2</td>"));
                assertThat(br.readLine(), is("                <td>t</td>"));
                assertThat(br.readLine(), is("                <td>こんにちは</td>"));
                assertThat(br.readLine(), is("                <td>asada</td>"));
                assertThat(br.readLine(), is("                <td>2017/5/11 11:57</td>"));
                assertThat(br.readLine(), is("                <td>"));
                assertThat(br.readLine(), is("                    <form action=\"/program/board/\" method=\"post\">"));
                assertThat(br.readLine(), is("                        <input type=\"hidden\" name=\"param\" value=\"delete1\">"));
                assertThat(br.readLine(), is("                        <input type=\"hidden\" name=\"number\" value=\"2\">"));
                assertThat(br.readLine(), is("                        <input type=\"submit\" value=\"このコメントを削除する\">"));
                assertThat(br.readLine(), is("                    </form>"));
                assertThat(br.readLine(), is("                </td>"));
                assertThat(br.readLine(), is("            </tr>"));
                assertThat(br.readLine(), is("        </table>"));
                assertThat(br.readLine(), is("    </div>"));
                assertThat(br.readLine(), is("    <div id=\"back\">"));
                assertThat(br.readLine(), is("        <form action=\"/program/board/\" method=\"post\">"));
                assertThat(br.readLine(), is("            <input type=\"hidden\" name=\"param\" value=\"back\">"));
                assertThat(br.readLine(), is("            <input type=\"submit\" value=\"topへ戻る\">"));
                assertThat(br.readLine(), is("        </form>"));
                assertThat(br.readLine(), is("    </div>"));
                assertThat(br.readLine(), is("</center>"));
                assertThat(br.readLine(), is("</body>"));
                assertThat(br.readLine(), is(""));
                assertThat(br.readLine(), is("</html>"));
            }

            he.allInitialization();
        }

        @Test
        public void switchのdelete1をテスト() throws Exception {
            String path = "./src/test/resources/PostDelete1Test.txt";
            ProgramBoardHandler handler;
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(path)))) {
                handler = new ProgramBoardHandler(new RequestMessage(bis));
            }
            StatusLine sut = handler.doRequestProcess();
            assertThat(sut.getStatusCode(), is(200));

            path = "./src/main/resources/2/delete.html";
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path))))) {
                assertThat(br.readLine(), is("<!DOCTYPE html>"));
                assertThat(br.readLine(), is("<html>"));
                assertThat(br.readLine(), is(""));
                assertThat(br.readLine(), is("<head>"));
                assertThat(br.readLine(), is("    <meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">"));
                assertThat(br.readLine(), is("    <link rel=\"stylesheet\" type=\"text/css\" href=\"./css/deleteStyle.css\">"));
                assertThat(br.readLine(), is("</head>"));
                assertThat(br.readLine(), is(""));
                assertThat(br.readLine(), is("<body>"));
                assertThat(br.readLine(), is("<center>"));
                assertThat(br.readLine(), is("    <div id=\"header\">"));
                assertThat(br.readLine(), is("        <h1>掲示板-LightBoard</h1>"));
                assertThat(br.readLine(), is("        <a href=\"./caution.html\" target=\"_blank\">注意事項を読む</a>"));
                assertThat(br.readLine(), is("    </div>"));
                assertThat(br.readLine(), is("    <div id=\"log\">"));
                assertThat(br.readLine(), is("        <h2>削除するメッセージ</h2>"));
                assertThat(br.readLine(), is("        <table border=\"1\">"));
                assertThat(br.readLine(), is("            <tr>"));
                assertThat(br.readLine(), is("                <th>ナンバー</th>"));
                assertThat(br.readLine(), is("                <th>タイトル</th>"));
                assertThat(br.readLine(), is("                <th>本文</th>"));
                assertThat(br.readLine(), is("                <th>ユーザー名</th>"));
                assertThat(br.readLine(), is("                <th>日付</th>"));
                assertThat(br.readLine(), is("            </tr>"));
                assertThat(br.readLine(), is("            <tr id=\"No.2\">"));
                assertThat(br.readLine(), is("                <td>No.2</td>"));
                assertThat(br.readLine(), is("                <td>t</td>"));
                assertThat(br.readLine(), is("                <td>こんにちは</td>"));
                assertThat(br.readLine(), is("                <td>asada</td>"));
                assertThat(br.readLine(), is("                <td>2017/5/11 11:57</td>"));
                assertThat(br.readLine(), is("            </tr>"));
                assertThat(br.readLine(), is("        </table>"));
                assertThat(br.readLine(), is("    </div>"));
                assertThat(br.readLine(), is("    <div id=\"form\">"));
                assertThat(br.readLine(), is("        <p>投稿した時に入力したパスワードを入力してください。</p>"));
                assertThat(br.readLine(), is("        <form action=\"/program/board/\" method=\"post\">"));
                assertThat(br.readLine(), is("            <p>"));
                assertThat(br.readLine(), is("                パスワード<input type=\"password\" name=\"password\" size=\"10\" required>"));
                assertThat(br.readLine(), is("            </p>"));
                assertThat(br.readLine(), is("            <input type=\"hidden\" name=\"number\" value=\"2\">"));
                assertThat(br.readLine(), is("            <input type=\"hidden\" name=\"param\" value=\"delete2\">"));
                assertThat(br.readLine(), is("            <input type=\"submit\" value=\"削除する\">"));
                assertThat(br.readLine(), is("        </form>"));
                assertThat(br.readLine(), is("    </div>"));
                assertThat(br.readLine(), is("    <div id=\"back\">"));
                assertThat(br.readLine(), is("        <form action=\"/program/board/\" method=\"post\">"));
                assertThat(br.readLine(), is("            <input type=\"hidden\" name=\"param\" value=\"back\">"));
                assertThat(br.readLine(), is("            <input type=\"submit\" value=\"戻る\">"));
                assertThat(br.readLine(), is("        </form>"));
                assertThat(br.readLine(), is("    </div>"));
                assertThat(br.readLine(), is("</center>"));
                assertThat(br.readLine(), is("</body>"));
                assertThat(br.readLine(), is(""));
                assertThat(br.readLine(), is("</html>"));
            }

            he.allInitialization();
        }

        @Test
        public void switchのdelete2をテスト() throws Exception {
            String path = "./src/test/resources/PostDelete2Test.txt";
            ProgramBoardHandler handler;
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(path)))) {
                handler = new ProgramBoardHandler(new RequestMessage(bis));
            }
            StatusLine sut = handler.doRequestProcess();
            assertThat(sut.getStatusCode(), is(200));

            path = "./src/main/resources/2/delete.html";
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path))))) {
                assertThat(br.readLine(), is("<!DOCTYPE html>"));
                assertThat(br.readLine(), is("<html>"));
                assertThat(br.readLine(), is(""));
                assertThat(br.readLine(), is("<head>"));
                assertThat(br.readLine(), is("    <meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">"));
                assertThat(br.readLine(), is("    <link rel=\"stylesheet\" type=\"text/css\" href=\"./css/deleteStyle.css\">"));
                assertThat(br.readLine(), is("</head>"));
                assertThat(br.readLine(), is(""));
                assertThat(br.readLine(), is("<body>"));
                assertThat(br.readLine(), is("<center>"));
                assertThat(br.readLine(), is("    <div id=\"header\">"));
                assertThat(br.readLine(), is("        <h1>掲示板-LightBoard</h1>"));
                assertThat(br.readLine(), is("        <a href=\"./caution.html\" target=\"_blank\">注意事項を読む</a>"));
                assertThat(br.readLine(), is("    </div>"));
                assertThat(br.readLine(), is("    <div id=\"log\">"));
                assertThat(br.readLine(), is("        <h2>削除するメッセージ</h2>"));
                assertThat(br.readLine(), is("        <table border=\"1\">"));
                assertThat(br.readLine(), is("            <tr>"));
                assertThat(br.readLine(), is("                <th>ナンバー</th>"));
                assertThat(br.readLine(), is("                <th>タイトル</th>"));
                assertThat(br.readLine(), is("                <th>本文</th>"));
                assertThat(br.readLine(), is("                <th>ユーザー名</th>"));
                assertThat(br.readLine(), is("                <th>日付</th>"));
                assertThat(br.readLine(), is("            </tr>"));
                assertThat(br.readLine(), is("            <tr id=\"No.2\">"));
                assertThat(br.readLine(), is("                <td>No.2</td>"));
                assertThat(br.readLine(), is("                <td>t</td>"));
                assertThat(br.readLine(), is("                <td>こんにちは</td>"));
                assertThat(br.readLine(), is("                <td>asada</td>"));
                assertThat(br.readLine(), is("                <td>2017/5/11 11:57</td>"));
                assertThat(br.readLine(), is("            </tr>"));
                assertThat(br.readLine(), is("        </table>"));
                assertThat(br.readLine(), is("    </div>"));
                assertThat(br.readLine(), is("    <div id=\"form\">"));
                assertThat(br.readLine(), is("        <p>投稿した時に入力したパスワードを入力してください。</p>"));
                assertThat(br.readLine(), is("        <form action=\"/program/board/\" method=\"post\">"));
                assertThat(br.readLine(), is("            <p>"));
                assertThat(br.readLine(), is("                パスワード<input type=\"password\" name=\"password\" size=\"10\" required>"));
                assertThat(br.readLine(), is("            </p>"));
                assertThat(br.readLine(), is("            <input type=\"hidden\" name=\"number\" value=\"2\">"));
                assertThat(br.readLine(), is("            <input type=\"hidden\" name=\"param\" value=\"delete2\">"));
                assertThat(br.readLine(), is("            <input type=\"submit\" value=\"削除する\">"));
                assertThat(br.readLine(), is("        </form>"));
                assertThat(br.readLine(), is("    </div>"));
                assertThat(br.readLine(), is("    <div id=\"back\">"));
                assertThat(br.readLine(), is("        <form action=\"/program/board/\" method=\"post\">"));
                assertThat(br.readLine(), is("            <input type=\"hidden\" name=\"param\" value=\"back\">"));
                assertThat(br.readLine(), is("            <input type=\"submit\" value=\"戻る\">"));
                assertThat(br.readLine(), is("        </form>"));
                assertThat(br.readLine(), is("    </div>"));
                assertThat(br.readLine(), is("</center>"));
                assertThat(br.readLine(), is("</body>"));
                assertThat(br.readLine(), is(""));
                assertThat(br.readLine(), is("</html>"));
            }

            he.allInitialization();
        }

        @Test
        public void switchのcontributionをテスト() throws Exception {
            String path = "./src/test/resources/PostRequestMessage.txt";
            ProgramBoardHandler handler;
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(path)))) {
                handler = new ProgramBoardHandler(new RequestMessage(bis));
            }
            StatusLine sut = handler.doRequestProcess();
            assertThat(sut.getStatusCode(), is(200));

            path = "./src/main/resources/2/index.html";
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path))))) {
                assertThat(br.readLine(), is("<!DOCTYPE html>"));
                assertThat(br.readLine(), is("<html>"));
                assertThat(br.readLine(), is(""));
                assertThat(br.readLine(), is("<head>"));
                assertThat(br.readLine(), is("    <meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">"));
                assertThat(br.readLine(), is("    <link rel=\"stylesheet\" type=\"text/css\" href=\"./css/style.css\">"));
                assertThat(br.readLine(), is("</head>"));
                assertThat(br.readLine(), is(""));
                assertThat(br.readLine(), is("<body>"));
                assertThat(br.readLine(), is("<center>"));
                assertThat(br.readLine(), is("    <div id=\"header\">"));
                assertThat(br.readLine(), is("        <h1>掲示板-LightBoard</h1>"));
                assertThat(br.readLine(), is("        <a href=\"./caution.html\" target=\"_blank\">注意事項を読む</a>"));
                assertThat(br.readLine(), is("    </div>"));
                assertThat(br.readLine(), is("    <div id=\"form\">"));
                assertThat(br.readLine(), is("        <form action=\"/program/board/\" method=\"post\">"));
                assertThat(br.readLine(), is("            <p>"));
                assertThat(br.readLine(), is("                名前<input type=\"text\" name=\"name\" size=\"40\" required>"));
                assertThat(br.readLine(), is("            </p>"));
                assertThat(br.readLine(), is("            <p>"));
                assertThat(br.readLine(), is("                タイトル<input type=\"text\" name=\"title\" size=\"40\" required>"));
                assertThat(br.readLine(), is("            </p>"));
                assertThat(br.readLine(), is("            <p>"));
                assertThat(br.readLine(), is("                メッセージ<br>"));
                assertThat(br.readLine(), is("                <textarea name=\"text\" rows=\"4\" cols=\"40\" required></textarea>"));
                assertThat(br.readLine(), is("            </p>"));
                assertThat(br.readLine(), is("            <p>"));
                assertThat(br.readLine(), is("                パスワード<input type=\"password\" name=\"password\" size=\"10\" required>(投稿した文を削除するときに使います。)"));
                assertThat(br.readLine(), is("            </p>"));
                assertThat(br.readLine(), is("            <input type=\"hidden\" name=\"param\" value=\"contribution\">"));
                assertThat(br.readLine(), is("            <input type=\"submit\" value=\"投稿\">"));
                assertThat(br.readLine(), is("        </form>"));
                assertThat(br.readLine(), is("    </div>"));
                assertThat(br.readLine(), is("    <div id=\"log\">"));
                assertThat(br.readLine(), is("        <table border=\"1\">"));
                assertThat(br.readLine(), is("            <tr>"));
                assertThat(br.readLine(), is("                <th>ナンバー</th>"));
                assertThat(br.readLine(), is("                <th>タイトル</th>"));
                assertThat(br.readLine(), is("                <th>本文</th>"));
                assertThat(br.readLine(), is("                <th>ユーザー名</th>"));
                assertThat(br.readLine(), is("                <th>日付</th>"));
                assertThat(br.readLine(), is("                <th></th>"));
                assertThat(br.readLine(), is("                <th></th>"));
                assertThat(br.readLine(), is("            </tr>"));
                assertThat(br.readLine(), is("            <tr id=\"No.5\">"));
                assertThat(br.readLine(), is("                <td>No.5</td>"));
                assertThat(br.readLine(), is("                <td>test</td>"));
                assertThat(br.readLine(), is("                <td>こんにちは</td>"));
                assertThat(br.readLine(), is("                <td>asada</td>"));
                assertThat(br.readLine(), is("                <td>" + ModelController.getNowDate() + "</td>"));
                assertThat(br.readLine(), is("                <td>"));
                assertThat(br.readLine(), is("                    <form action=\"/program/board/\" method=\"post\">"));
                assertThat(br.readLine(), is("                        <input type=\"hidden\" name=\"param\" value=\"search\">"));
                assertThat(br.readLine(), is("                        <input type=\"hidden\" name=\"number\" value=\"5\">"));
                assertThat(br.readLine(), is("                        <input type=\"submit\" value=\"このコメントを投稿した人の他のコメントを見てみる\">"));
                assertThat(br.readLine(), is("                    </form>"));
                assertThat(br.readLine(), is("                </td>"));
                assertThat(br.readLine(), is("                <td>"));
                assertThat(br.readLine(), is("                    <form action=\"/program/board/\" method=\"post\">"));
                assertThat(br.readLine(), is("                        <input type=\"hidden\" name=\"param\" value=\"delete1\">"));
                assertThat(br.readLine(), is("                        <input type=\"hidden\" name=\"number\" value=\"5\">"));
                assertThat(br.readLine(), is("                        <input type=\"submit\" value=\"このコメントを削除する\">"));
                assertThat(br.readLine(), is("                    </form>"));
                assertThat(br.readLine(), is("                </td>"));
                assertThat(br.readLine(), is("            </tr>"));
                assertThat(br.readLine(), is("            <tr id=\"No.4\">"));
                assertThat(br.readLine(), is("                <td>No.4</td>"));
                assertThat(br.readLine(), is("                <td>t</td>"));
                assertThat(br.readLine(), is("                <td>そうですね</td>"));
                assertThat(br.readLine(), is("                <td>管理者</td>"));
                assertThat(br.readLine(), is("                <td>2017/5/11 11:57</td>"));
                assertThat(br.readLine(), is("                <td>"));
                assertThat(br.readLine(), is("                    <form action=\"/program/board/\" method=\"post\">"));
                assertThat(br.readLine(), is("                        <input type=\"hidden\" name=\"param\" value=\"search\">"));
                assertThat(br.readLine(), is("                        <input type=\"hidden\" name=\"number\" value=\"4\">"));
                assertThat(br.readLine(), is("                        <input type=\"submit\" value=\"このコメントを投稿した人の他のコメントを見てみる\">"));
                assertThat(br.readLine(), is("                    </form>"));
                assertThat(br.readLine(), is("                </td>"));
                assertThat(br.readLine(), is("                <td>"));
                assertThat(br.readLine(), is("                    <form action=\"/program/board/\" method=\"post\">"));
                assertThat(br.readLine(), is("                        <input type=\"hidden\" name=\"param\" value=\"delete1\">"));
                assertThat(br.readLine(), is("                        <input type=\"hidden\" name=\"number\" value=\"4\">"));
                assertThat(br.readLine(), is("                        <input type=\"submit\" value=\"このコメントを削除する\">"));
                assertThat(br.readLine(), is("                    </form>"));
                assertThat(br.readLine(), is("                </td>"));
                assertThat(br.readLine(), is("            </tr>"));
                assertThat(br.readLine(), is("            <tr id=\"No.3\">"));
                assertThat(br.readLine(), is("                <td>No.3</td>"));
                assertThat(br.readLine(), is("                <td>t</td>"));
                assertThat(br.readLine(), is("                <td>今日は天気がいいですね</td>"));
                assertThat(br.readLine(), is("                <td>asada</td>"));
                assertThat(br.readLine(), is("                <td>2017/5/11 11:57</td>"));
                assertThat(br.readLine(), is("                <td>"));
                assertThat(br.readLine(), is("                    <form action=\"/program/board/\" method=\"post\">"));
                assertThat(br.readLine(), is("                        <input type=\"hidden\" name=\"param\" value=\"search\">"));
                assertThat(br.readLine(), is("                        <input type=\"hidden\" name=\"number\" value=\"3\">"));
                assertThat(br.readLine(), is("                        <input type=\"submit\" value=\"このコメントを投稿した人の他のコメントを見てみる\">"));
                assertThat(br.readLine(), is("                    </form>"));
                assertThat(br.readLine(), is("                </td>"));
                assertThat(br.readLine(), is("                <td>"));
                assertThat(br.readLine(), is("                    <form action=\"/program/board/\" method=\"post\">"));
                assertThat(br.readLine(), is("                        <input type=\"hidden\" name=\"param\" value=\"delete1\">"));
                assertThat(br.readLine(), is("                        <input type=\"hidden\" name=\"number\" value=\"3\">"));
                assertThat(br.readLine(), is("                        <input type=\"submit\" value=\"このコメントを削除する\">"));
                assertThat(br.readLine(), is("                    </form>"));
                assertThat(br.readLine(), is("                </td>"));
                assertThat(br.readLine(), is("            </tr>"));
                assertThat(br.readLine(), is("            <tr id=\"No.2\">"));
                assertThat(br.readLine(), is("                <td>No.2</td>"));
                assertThat(br.readLine(), is("                <td>t</td>"));
                assertThat(br.readLine(), is("                <td>こんにちは</td>"));
                assertThat(br.readLine(), is("                <td>asada</td>"));
                assertThat(br.readLine(), is("                <td>2017/5/11 11:57</td>"));
                assertThat(br.readLine(), is("                <td>"));
                assertThat(br.readLine(), is("                    <form action=\"/program/board/\" method=\"post\">"));
                assertThat(br.readLine(), is("                        <input type=\"hidden\" name=\"param\" value=\"search\">"));
                assertThat(br.readLine(), is("                        <input type=\"hidden\" name=\"number\" value=\"2\">"));
                assertThat(br.readLine(), is("                        <input type=\"submit\" value=\"このコメントを投稿した人の他のコメントを見てみる\">"));
                assertThat(br.readLine(), is("                    </form>"));
                assertThat(br.readLine(), is("                </td>"));
                assertThat(br.readLine(), is("                <td>"));
                assertThat(br.readLine(), is("                    <form action=\"/program/board/\" method=\"post\">"));
                assertThat(br.readLine(), is("                        <input type=\"hidden\" name=\"param\" value=\"delete1\">"));
                assertThat(br.readLine(), is("                        <input type=\"hidden\" name=\"number\" value=\"2\">"));
                assertThat(br.readLine(), is("                        <input type=\"submit\" value=\"このコメントを削除する\">"));
                assertThat(br.readLine(), is("                    </form>"));
                assertThat(br.readLine(), is("                </td>"));
                assertThat(br.readLine(), is("            </tr>"));
                assertThat(br.readLine(), is("            <tr id=\"No.1\">"));
                assertThat(br.readLine(), is("                <td>No.1</td>"));
                assertThat(br.readLine(), is("                <td>test</td>"));
                assertThat(br.readLine(), is("                <td>こんにちは</td>"));
                assertThat(br.readLine(), is("                <td>管理者</td>"));
                assertThat(br.readLine(), is("                <td>2017/5/11 11:56</td>"));
                assertThat(br.readLine(), is("                <td>"));
                assertThat(br.readLine(), is("                    <form action=\"/program/board/\" method=\"post\">"));
                assertThat(br.readLine(), is("                        <input type=\"hidden\" name=\"param\" value=\"search\">"));
                assertThat(br.readLine(), is("                        <input type=\"hidden\" name=\"number\" value=\"1\">"));
                assertThat(br.readLine(), is("                        <input type=\"submit\" value=\"このコメントを投稿した人の他のコメントを見てみる\">"));
                assertThat(br.readLine(), is("                    </form>"));
                assertThat(br.readLine(), is("                </td>"));
                assertThat(br.readLine(), is("                <td>"));
                assertThat(br.readLine(), is("                    <form action=\"/program/board/\" method=\"post\">"));
                assertThat(br.readLine(), is("                        <input type=\"hidden\" name=\"param\" value=\"delete1\">"));
                assertThat(br.readLine(), is("                        <input type=\"hidden\" name=\"number\" value=\"1\">"));
                assertThat(br.readLine(), is("                        <input type=\"submit\" value=\"このコメントを削除する\">"));
                assertThat(br.readLine(), is("                    </form>"));
                assertThat(br.readLine(), is("                </td>"));
                assertThat(br.readLine(), is("            </tr>"));
                assertThat(br.readLine(), is("        </table>"));
                assertThat(br.readLine(), is("    </div>"));
                assertThat(br.readLine(), is("</center>"));
                assertThat(br.readLine(), is("</body>"));
                assertThat(br.readLine(), is(""));
                assertThat(br.readLine(), is("</html>"));

                he.allInitialization();
            }
        }

        @Test
        public void switchのbackをテスト() throws Exception {
            String path = "./src/test/resources/PostBackTest.txt";
            ProgramBoardHandler handler;
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(path)))) {
                handler = new ProgramBoardHandler(new RequestMessage(bis));
            }
            StatusLine sut = handler.doRequestProcess();
            assertThat(sut.getStatusCode(), is(200));

            path = "./src/main/resources/2/index.html";
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path))))) {
                assertThat(br.readLine(), is("<!DOCTYPE html>"));
                assertThat(br.readLine(), is("<html>"));
                assertThat(br.readLine(), is(""));
                assertThat(br.readLine(), is("<head>"));
                assertThat(br.readLine(), is("    <meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">"));
                assertThat(br.readLine(), is("    <link rel=\"stylesheet\" type=\"text/css\" href=\"./css/style.css\">"));
                assertThat(br.readLine(), is("</head>"));
                assertThat(br.readLine(), is(""));
                assertThat(br.readLine(), is("<body>"));
                assertThat(br.readLine(), is("<center>"));
                assertThat(br.readLine(), is("    <div id=\"header\">"));
                assertThat(br.readLine(), is("        <h1>掲示板-LightBoard</h1>"));
                assertThat(br.readLine(), is("        <a href=\"./caution.html\" target=\"_blank\">注意事項を読む</a>"));
                assertThat(br.readLine(), is("    </div>"));
                assertThat(br.readLine(), is("    <div id=\"form\">"));
                assertThat(br.readLine(), is("        <form action=\"/program/board/\" method=\"post\">"));
                assertThat(br.readLine(), is("            <p>"));
                assertThat(br.readLine(), is("                名前<input type=\"text\" name=\"name\" size=\"40\" required>"));
                assertThat(br.readLine(), is("            </p>"));
                assertThat(br.readLine(), is("            <p>"));
                assertThat(br.readLine(), is("                タイトル<input type=\"text\" name=\"title\" size=\"40\" required>"));
                assertThat(br.readLine(), is("            </p>"));
                assertThat(br.readLine(), is("            <p>"));
                assertThat(br.readLine(), is("                メッセージ<br>"));
                assertThat(br.readLine(), is("                <textarea name=\"text\" rows=\"4\" cols=\"40\" required></textarea>"));
                assertThat(br.readLine(), is("            </p>"));
                assertThat(br.readLine(), is("            <p>"));
                assertThat(br.readLine(), is("                パスワード<input type=\"password\" name=\"password\" size=\"10\" required>(投稿した文を削除するときに使います。)"));
                assertThat(br.readLine(), is("            </p>"));
                assertThat(br.readLine(), is("            <input type=\"hidden\" name=\"param\" value=\"contribution\">"));
                assertThat(br.readLine(), is("            <input type=\"submit\" value=\"投稿\">"));
                assertThat(br.readLine(), is("        </form>"));
                assertThat(br.readLine(), is("    </div>"));
                assertThat(br.readLine(), is("    <div id=\"log\">"));
                assertThat(br.readLine(), is("        <table border=\"1\">"));
                assertThat(br.readLine(), is("            <tr>"));
                assertThat(br.readLine(), is("                <th>ナンバー</th>"));
                assertThat(br.readLine(), is("                <th>タイトル</th>"));
                assertThat(br.readLine(), is("                <th>本文</th>"));
                assertThat(br.readLine(), is("                <th>ユーザー名</th>"));
                assertThat(br.readLine(), is("                <th>日付</th>"));
                assertThat(br.readLine(), is("                <th></th>"));
                assertThat(br.readLine(), is("                <th></th>"));
                assertThat(br.readLine(), is("            </tr>"));
                assertThat(br.readLine(), is("            <tr id=\"No.4\">"));
                assertThat(br.readLine(), is("                <td>No.4</td>"));
                assertThat(br.readLine(), is("                <td>t</td>"));
                assertThat(br.readLine(), is("                <td>そうですね</td>"));
                assertThat(br.readLine(), is("                <td>管理者</td>"));
                assertThat(br.readLine(), is("                <td>2017/5/11 11:57</td>"));
                assertThat(br.readLine(), is("                <td>"));
                assertThat(br.readLine(), is("                    <form action=\"/program/board/\" method=\"post\">"));
                assertThat(br.readLine(), is("                        <input type=\"hidden\" name=\"param\" value=\"search\">"));
                assertThat(br.readLine(), is("                        <input type=\"hidden\" name=\"number\" value=\"4\">"));
                assertThat(br.readLine(), is("                        <input type=\"submit\" value=\"このコメントを投稿した人の他のコメントを見てみる\">"));
                assertThat(br.readLine(), is("                    </form>"));
                assertThat(br.readLine(), is("                </td>"));
                assertThat(br.readLine(), is("                <td>"));
                assertThat(br.readLine(), is("                    <form action=\"/program/board/\" method=\"post\">"));
                assertThat(br.readLine(), is("                        <input type=\"hidden\" name=\"param\" value=\"delete1\">"));
                assertThat(br.readLine(), is("                        <input type=\"hidden\" name=\"number\" value=\"4\">"));
                assertThat(br.readLine(), is("                        <input type=\"submit\" value=\"このコメントを削除する\">"));
                assertThat(br.readLine(), is("                    </form>"));
                assertThat(br.readLine(), is("                </td>"));
                assertThat(br.readLine(), is("            </tr>"));
                assertThat(br.readLine(), is("            <tr id=\"No.3\">"));
                assertThat(br.readLine(), is("                <td>No.3</td>"));
                assertThat(br.readLine(), is("                <td>t</td>"));
                assertThat(br.readLine(), is("                <td>今日は天気がいいですね</td>"));
                assertThat(br.readLine(), is("                <td>asada</td>"));
                assertThat(br.readLine(), is("                <td>2017/5/11 11:57</td>"));
                assertThat(br.readLine(), is("                <td>"));
                assertThat(br.readLine(), is("                    <form action=\"/program/board/\" method=\"post\">"));
                assertThat(br.readLine(), is("                        <input type=\"hidden\" name=\"param\" value=\"search\">"));
                assertThat(br.readLine(), is("                        <input type=\"hidden\" name=\"number\" value=\"3\">"));
                assertThat(br.readLine(), is("                        <input type=\"submit\" value=\"このコメントを投稿した人の他のコメントを見てみる\">"));
                assertThat(br.readLine(), is("                    </form>"));
                assertThat(br.readLine(), is("                </td>"));
                assertThat(br.readLine(), is("                <td>"));
                assertThat(br.readLine(), is("                    <form action=\"/program/board/\" method=\"post\">"));
                assertThat(br.readLine(), is("                        <input type=\"hidden\" name=\"param\" value=\"delete1\">"));
                assertThat(br.readLine(), is("                        <input type=\"hidden\" name=\"number\" value=\"3\">"));
                assertThat(br.readLine(), is("                        <input type=\"submit\" value=\"このコメントを削除する\">"));
                assertThat(br.readLine(), is("                    </form>"));
                assertThat(br.readLine(), is("                </td>"));
                assertThat(br.readLine(), is("            </tr>"));
                assertThat(br.readLine(), is("            <tr id=\"No.2\">"));
                assertThat(br.readLine(), is("                <td>No.2</td>"));
                assertThat(br.readLine(), is("                <td>t</td>"));
                assertThat(br.readLine(), is("                <td>こんにちは</td>"));
                assertThat(br.readLine(), is("                <td>asada</td>"));
                assertThat(br.readLine(), is("                <td>2017/5/11 11:57</td>"));
                assertThat(br.readLine(), is("                <td>"));
                assertThat(br.readLine(), is("                    <form action=\"/program/board/\" method=\"post\">"));
                assertThat(br.readLine(), is("                        <input type=\"hidden\" name=\"param\" value=\"search\">"));
                assertThat(br.readLine(), is("                        <input type=\"hidden\" name=\"number\" value=\"2\">"));
                assertThat(br.readLine(), is("                        <input type=\"submit\" value=\"このコメントを投稿した人の他のコメントを見てみる\">"));
                assertThat(br.readLine(), is("                    </form>"));
                assertThat(br.readLine(), is("                </td>"));
                assertThat(br.readLine(), is("                <td>"));
                assertThat(br.readLine(), is("                    <form action=\"/program/board/\" method=\"post\">"));
                assertThat(br.readLine(), is("                        <input type=\"hidden\" name=\"param\" value=\"delete1\">"));
                assertThat(br.readLine(), is("                        <input type=\"hidden\" name=\"number\" value=\"2\">"));
                assertThat(br.readLine(), is("                        <input type=\"submit\" value=\"このコメントを削除する\">"));
                assertThat(br.readLine(), is("                    </form>"));
                assertThat(br.readLine(), is("                </td>"));
                assertThat(br.readLine(), is("            </tr>"));
                assertThat(br.readLine(), is("            <tr id=\"No.1\">"));
                assertThat(br.readLine(), is("                <td>No.1</td>"));
                assertThat(br.readLine(), is("                <td>test</td>"));
                assertThat(br.readLine(), is("                <td>こんにちは</td>"));
                assertThat(br.readLine(), is("                <td>管理者</td>"));
                assertThat(br.readLine(), is("                <td>2017/5/11 11:56</td>"));
                assertThat(br.readLine(), is("                <td>"));
                assertThat(br.readLine(), is("                    <form action=\"/program/board/\" method=\"post\">"));
                assertThat(br.readLine(), is("                        <input type=\"hidden\" name=\"param\" value=\"search\">"));
                assertThat(br.readLine(), is("                        <input type=\"hidden\" name=\"number\" value=\"1\">"));
                assertThat(br.readLine(), is("                        <input type=\"submit\" value=\"このコメントを投稿した人の他のコメントを見てみる\">"));
                assertThat(br.readLine(), is("                    </form>"));
                assertThat(br.readLine(), is("                </td>"));
                assertThat(br.readLine(), is("                <td>"));
                assertThat(br.readLine(), is("                    <form action=\"/program/board/\" method=\"post\">"));
                assertThat(br.readLine(), is("                        <input type=\"hidden\" name=\"param\" value=\"delete1\">"));
                assertThat(br.readLine(), is("                        <input type=\"hidden\" name=\"number\" value=\"1\">"));
                assertThat(br.readLine(), is("                        <input type=\"submit\" value=\"このコメントを削除する\">"));
                assertThat(br.readLine(), is("                    </form>"));
                assertThat(br.readLine(), is("                </td>"));
                assertThat(br.readLine(), is("            </tr>"));
                assertThat(br.readLine(), is("        </table>"));
                assertThat(br.readLine(), is("    </div>"));
                assertThat(br.readLine(), is("</center>"));
                assertThat(br.readLine(), is("</body>"));
                assertThat(br.readLine(), is(""));
                assertThat(br.readLine(), is("</html>"));

                he.allInitialization();
            }
        }

        @After
        public void tearDown() {
            he.allInitialization();
        }
    }

    public static class getStatusLineメソッドのテスト {
        @Test
        public void GETリクエスト200() throws Exception {
            StatusLine sut = ProgramBoardHandler.getStatusLine("GET", "/index.html", "HTTP/1.1");
            assertThat(sut.getStatusCode(), is(200));
        }

        @Test
        public void POSTリクエスト200() throws Exception {
            StatusLine sut = ProgramBoardHandler.getStatusLine("POST", "/program/board/index.html", "HTTP/1.1");
            assertThat(sut.getStatusCode(), is(200));
        }

        @Test
        public void 存在しないファイルを指定すると404() throws Exception {
            StatusLine sut = ProgramBoardHandler.getStatusLine("GET", "/hogehoge", "HTTP/1.1");
            assertThat(sut.getStatusCode(), is(404));
        }

        @Test
        public void ディレクトリを指定すると404() throws Exception {
            StatusLine sut = ProgramBoardHandler.getStatusLine("GET", "/", "HTTP/1.1");
            assertThat(sut.getStatusCode(), is(404));
        }

        @Test
        public void POSTの時にURIが想定外のものだと400() throws Exception {
            StatusLine sut = ProgramBoardHandler.getStatusLine("POST", "/", "HTTP/1.1");
            assertThat(sut.getStatusCode(), is(400));
        }

        @Test
        public void GETとPOST以外は501() throws Exception {
            StatusLine sut;
            sut = ProgramBoardHandler.getStatusLine("PUT", "/", "HTTP/1.1");
            assertThat(sut.getStatusCode(), is(501));

            sut = ProgramBoardHandler.getStatusLine("DELETE", "/", "HTTP/1.1");
            assertThat(sut.getStatusCode(), is(501));
        }

        @Test
        public void HTTPのバージョンが指定と異なる505() throws Exception {
            StatusLine sut = ProgramBoardHandler.getStatusLine("GET", "/", "HTTP/2.0");
            assertThat(sut.getStatusCode(), is(505));
        }
    }
}
