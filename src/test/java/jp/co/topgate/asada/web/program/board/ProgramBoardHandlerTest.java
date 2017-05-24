package jp.co.topgate.asada.web.program.board;

import jp.co.topgate.asada.web.RequestMessage;
import jp.co.topgate.asada.web.RequestMessageParser;
import jp.co.topgate.asada.web.ResponseMessage;
import jp.co.topgate.asada.web.StatusLine;
import jp.co.topgate.asada.web.exception.IllegalRequestException;
import jp.co.topgate.asada.web.model.Message;
import jp.co.topgate.asada.web.model.ModelController;
import org.hamcrest.Matchers;
import org.junit.*;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
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
    public static class matchMethodのテスト {
        @Test
        public void trueになるかテスト() {
            assertThat(ProgramBoardHandler.matchMethod("GET"), is(true));
            assertThat(ProgramBoardHandler.matchMethod("POST"), is(true));
        }

        @Test
        public void falseになるかテスト() {
            assertThat(ProgramBoardHandler.matchMethod("PUT"), is(false));
            assertThat(ProgramBoardHandler.matchMethod("DELETE"), is(false));
            assertThat(ProgramBoardHandler.matchMethod(null), is(false));
            assertThat(ProgramBoardHandler.matchMethod(""), is(false));
        }
    }

    public static class コンストラクタのテスト {
        @Test
        public void nullチェック() throws Exception {
            ProgramBoardHandler sut = new ProgramBoardHandler(null);
            assertThat(sut.getRequestMessage(), is(nullValue()));
            assertThat(sut.getHtmlEditor(), is(instanceOf(HtmlEditor.class)));
        }

        @Test
        public void コンストラクタ() throws Exception {
            RequestMessage requestMessage;
            String path = "./src/test/resources/GetRequestMessage.txt";
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(path)))) {
                requestMessage = RequestMessageParser.parse(bis);
            }
            ProgramBoardHandler sut = new ProgramBoardHandler(requestMessage);

            assertThat(sut.getRequestMessage(), is(requestMessage));
            assertThat(sut.getHtmlEditor(), is(instanceOf(HtmlEditor.class)));
        }
    }

    public static class handleRequestメソッドのテスト {
        static HtmlEditor htmlEditor = new HtmlEditor();
        static List<Message> testList = new ArrayList<>();

        static {
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
        }

        @Before
        public void setUp() throws Exception {
            ModelController.setMessageList(testList);
        }

        @Test
        public void ステータスコード200のテスト() throws Exception {
            try (FileInputStream is = new FileInputStream(new File("./src/test/resources/GetRequestMessage.txt"))) {

                RequestMessage rm = RequestMessageParser.parse(is);

                ProgramBoardHandler sut = new ProgramBoardHandler(rm);

                ResponseMessage responseMessage = sut.handleRequest();

                responseMessage.addHeaderWithContentType("Test");

                assertThat(responseMessage.getProtocolVersion(), is("HTTP/1.1"));
                assertThat(responseMessage.getStatusLine(), is(StatusLine.OK));
                List<String> headerField = responseMessage.getHeaderField();
                assertThat(headerField.size(), is(3));
                assertThat(headerField.get(0), is("Content-Type: text/html; charset=UTF-8"));
                assertThat(headerField.get(1), is("Content-Length: 714"));
                assertThat(headerField.get(2), is("Content-Type: Test"));
                assertThat(responseMessage.getFilePath(), is(nullValue()));

                StringBuilder builder = new StringBuilder();
                try (BufferedReader br = new BufferedReader(new FileReader(new File("./src/test/resources/html/index.html")))) {
                    String str;
                    while ((str = br.readLine()) != null) {
                        builder.append(str).append("\n");
                    }
                }
                assertThat(responseMessage.getTarget(), is(builder.toString().getBytes()));
            }
        }

        @After
        public void tearDown() throws Exception {
            htmlEditor.resetAllFiles();
        }
    }

    public static class decideStatusLineメソッドのテスト {
        @Test
        public void nullチェック() throws Exception {
            StatusLine sut = ProgramBoardHandler.decideStatusLine(null, null, null);
            assertThat(sut.getStatusCode(), is(505));
        }

        @Test
        public void GETリクエスト200() throws Exception {
            StatusLine sut = ProgramBoardHandler.decideStatusLine("GET", "/index.html", "HTTP/1.1");
            assertThat(sut.getStatusCode(), is(200));
        }

        @Test
        public void POSTリクエスト200() throws Exception {
            StatusLine sut = ProgramBoardHandler.decideStatusLine("POST", "/program/board/index.html", "HTTP/1.1");
            assertThat(sut.getStatusCode(), is(200));
        }

        @Test
        public void 存在しないファイルを指定すると404() throws Exception {
            StatusLine sut = ProgramBoardHandler.decideStatusLine("GET", "/hogehoge", "HTTP/1.1");
            assertThat(sut.getStatusCode(), is(404));
        }

        @Test
        public void ディレクトリを指定すると404() throws Exception {
            StatusLine sut = ProgramBoardHandler.decideStatusLine("GET", "/", "HTTP/1.1");
            assertThat(sut.getStatusCode(), is(404));
        }

        @Test
        public void POSTの時にURIが想定外のものだと400() throws Exception {
            StatusLine sut = ProgramBoardHandler.decideStatusLine("POST", "/", "HTTP/1.1");
            assertThat(sut.getStatusCode(), is(400));
        }

        @Test
        public void GETとPOST以外は501() throws Exception {
            StatusLine sut;
            sut = ProgramBoardHandler.decideStatusLine("PUT", "/", "HTTP/1.1");
            assertThat(sut.getStatusCode(), is(501));

            sut = ProgramBoardHandler.decideStatusLine("DELETE", "/", "HTTP/1.1");
            assertThat(sut.getStatusCode(), is(501));
        }

        @Test
        public void HTTPのバージョンが指定と異なる505() throws Exception {
            StatusLine sut = ProgramBoardHandler.decideStatusLine("GET", "/", "HTTP/2.0");
            assertThat(sut.getStatusCode(), is(505));
        }
    }

    public static class doGetメソッドのテスト {
        @Before
        public void setUp() {
            List<Message> messageList = new ArrayList<>();
            Message m;
            m = new Message();
            m.setMessageID(1);
            m.setPassword(new BCryptPasswordEncoder().encode("test"));
            m.setName("管理者");
            m.setTitle("test");
            m.setText("こんにちは");
            m.setDate("2017/5/11 11:56");
            messageList.add(m);

            m = new Message();
            m.setMessageID(2);
            m.setPassword(new BCryptPasswordEncoder().encode("t"));
            m.setName("asada");
            m.setTitle("t");
            m.setText("こんにちは");
            m.setDate("2017/5/11 11:57");
            messageList.add(m);

            ModelController.setMessageList(messageList);
        }

        @Test
        public void 正しく動作するか() throws Exception {
            HtmlEditor htmlEditor = new HtmlEditor();
            ProgramBoardHandler.doGet(htmlEditor);

            //Verify
            String path = "./src/main/resources/2/index.html";
            try (BufferedReader br1 = new BufferedReader(new FileReader(new File(path)));
                 BufferedReader br2 = new BufferedReader(new FileReader(new File("./src/test/resources/html/index2message.html")))) {

                String str;
                while ((str = br1.readLine()) != null) {
                    assertThat(str, is(br2.readLine()));
                }
            }

            //TearDown
            htmlEditor.resetAllFiles();
        }
    }

    public static class doPostメソッドのテスト {
        static HtmlEditor htmlEditor;

        @Before
        public void setUp() {
            htmlEditor = new HtmlEditor();
            List<Message> messageList = new ArrayList<>();
            Message m;
            m = new Message();
            m.setMessageID(1);
            m.setPassword(new BCryptPasswordEncoder().encode("test"));
            m.setName("管理者");
            m.setTitle("test");
            m.setText("こんにちは");
            m.setDate("2017/5/11 11:56");
            messageList.add(m);

            m = new Message();
            m.setMessageID(2);
            m.setPassword(new BCryptPasswordEncoder().encode("t"));
            m.setName("asada");
            m.setTitle("t");
            m.setText("こんにちは");
            m.setDate("2017/5/11 11:57");
            messageList.add(m);

            m = new Message();
            m.setMessageID(3);
            m.setPassword(new BCryptPasswordEncoder().encode("t"));
            m.setName("asada");
            m.setTitle("t");
            m.setText("今日は天気がいいですね");
            m.setDate("2017/5/11 11:57");
            messageList.add(m);

            m = new Message();
            m.setMessageID(4);
            m.setPassword(new BCryptPasswordEncoder().encode("t"));
            m.setName("管理者");
            m.setTitle("t");
            m.setText("そうですね");
            m.setDate("2017/5/11 11:57");
            messageList.add(m);

            ModelController.setMessageList(messageList);
        }

        @Test(expected = NullPointerException.class)
        public void messageBodyがnullの場合() throws Exception {
            HtmlEditor htmlEditor = new HtmlEditor();

            ProgramBoardHandler.doPost(htmlEditor, Param.WRITE, null);

            htmlEditor.resetAllFiles();
        }

        @Test(expected = IllegalRequestException.class)
        public void paramがnullの場合() throws Exception {
            HtmlEditor htmlEditor = new HtmlEditor();
            Map<String, String> messageBody = new HashMap<>();

            ProgramBoardHandler.doPost(htmlEditor, null, messageBody);

            htmlEditor.resetAllFiles();
        }

        @Test(expected = IllegalRequestException.class)
        public void htmlEditorがnullの場合() throws Exception {
            HtmlEditor htmlEditor = new HtmlEditor();
            Map<String, String> messageBody = new HashMap<>();

            ProgramBoardHandler.doPost(null, Param.WRITE, messageBody);

            htmlEditor.resetAllFiles();
        }

        @Test
        public void switchのwriteをテスト() throws Exception {
            Map<String, String> map = new HashMap<>();
            map.put("name", "asada");
            map.put("title", "test");
            map.put("text", "メッセージ");
            map.put("password", "test");

            String sut = ProgramBoardHandler.doPost(htmlEditor, Param.WRITE, map).getUri();
            assertThat(sut, is(ProgramBoardHtmlList.INDEX_HTML.getUri()));

            htmlEditor.resetAllFiles();
        }

        @Test
        public void switchのsearchをテスト() throws Exception {
            Map<String, String> map = new HashMap<>();
            map.put("number", "2");

            String sut = ProgramBoardHandler.doPost(htmlEditor, Param.SEARCH, map).getUri();
            assertThat(sut, Matchers.is(ProgramBoardHtmlList.SEARCH_HTML.getUri()));

            htmlEditor.resetAllFiles();
        }

        @Test
        public void switchのdelete_step_1をテスト() throws Exception {
            Map<String, String> map = new HashMap<>();
            map.put("number", "2");

            String sut = ProgramBoardHandler.doPost(htmlEditor, Param.DELETE_STEP_1, map).getUri();
            assertThat(sut, is(ProgramBoardHtmlList.DELETE_HTML.getUri()));

            htmlEditor.resetAllFiles();
        }

        @Test
        public void switchのdelete_step_2をテスト() throws Exception {
            Map<String, String> map = new HashMap<>();
            map.put("number", "2");
            map.put("password", "t");

            String sut = ProgramBoardHandler.doPost(htmlEditor, Param.DELETE_STEP_2, map).getUri();
            assertThat(sut, is(ProgramBoardHtmlList.RESULT_HTML.getUri()));

            htmlEditor.resetAllFiles();
        }

        @Test
        public void switchのbackをテスト() throws Exception {
            String sut = ProgramBoardHandler.doPost(htmlEditor, Param.BACK, null).getUri();
            assertThat(sut, is(ProgramBoardHtmlList.INDEX_HTML.getUri()));

            htmlEditor.resetAllFiles();
        }

        @After
        public void tearDown() {
            htmlEditor.resetAllFiles();
        }
    }

    public static class writeIndexメソッドのテスト {
        @Test
        public void 正しく動作するか() throws Exception {
            HtmlEditor htmlEditor = new HtmlEditor();

            List<Message> testList = new ArrayList<>();
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

            String s = ProgramBoardHandler.writeIndex(htmlEditor).getUri();

            //Verify
            assertThat(s, is(ProgramBoardHtmlList.INDEX_HTML.getUri()));

            String path = "./src/main/resources/2/index.html";
            try (BufferedReader br1 = new BufferedReader(new FileReader(new File(path)));
                 BufferedReader br2 = new BufferedReader(new FileReader(new File("./src/test/resources/html/index2message.html")))) {

                String str;
                while ((str = br1.readLine()) != null) {
                    assertThat(str, is(br2.readLine()));
                }
            }

            //TearDown
            htmlEditor.resetAllFiles();
        }
    }
}
