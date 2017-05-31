package jp.co.topgate.asada.web.program.board;

import jp.co.topgate.asada.web.*;
import jp.co.topgate.asada.web.program.board.model.Message;
import jp.co.topgate.asada.web.program.board.model.ModelController;
import org.junit.*;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        public void nullチェック() throws Exception {
            ProgramBoardHandler sut = new ProgramBoardHandler(null, null);
            assertThat(sut.getRequestMessage(), is(nullValue()));
            assertThat(sut.getResponseMessage(), is(nullValue()));
        }

        @Test
        public void コンストラクタ() throws Exception {
            RequestMessage requestMessage = new RequestMessage(null, null, null, null, null);
            ResponseMessage responseMessage = new ResponseMessage(null);
            ProgramBoardHandler sut = new ProgramBoardHandler(requestMessage, responseMessage);

            assertThat(sut.getRequestMessage(), is(requestMessage));
            assertThat(sut.getResponseMessage(), is(responseMessage));
        }
    }

//    public static class handleRequestメソッドのテスト {
//        static List<Message> testList = new ArrayList<>();
//
//        static {
//            Message m = new Message();
//            m.setMessageID(1);
//            m.setPassword("test");
//            m.setName("管理者");
//            m.setTitle("test");
//            m.setText("こんにちは");
//            m.setDate("2017/5/11 11:56");
//            testList.add(m);
//
//            m = new Message();
//            m.setMessageID(2);
//            m.setPassword("t");
//            m.setName("asada");
//            m.setTitle("t");
//            m.setText("こんにちは");
//            m.setDate("2017/5/11 11:57");
//            testList.add(m);
//        }
//
//        @Before
//        public void setUp() throws Exception {
//            ModelController.setMessageList(testList);
//        }
//
//        @Test
//        public void ステータスコード200のテスト() throws Exception {
//            try (FileInputStream is = new FileInputStream(new File("./src/test/resources/GetRequestMessage.txt"))) {
//
//                RequestMessage requestMessage = RequestMessageParser.parse(is);
//
//                ProgramBoardHandler sut = new ProgramBoardHandler(requestMessage);
//
//                ResponseMessage responseMessage = sut.handleRequest();
//
//                responseMessage.addHeaderWithContentType("Test");
//
//                assertThat(responseMessage.getProtocolVersion(), is("HTTP/1.1"));
//                assertThat(responseMessage.getStatusLine(), is(StatusLine.OK));
//                List<String> headerField = responseMessage.getHeaderField();
//                assertThat(headerField.size(), is(3));
//                assertThat(headerField.get(0), is("Content-Type: text/html; charset=UTF-8"));
//                assertThat(headerField.get(1), is("Content-Length: 714"));
//                assertThat(headerField.get(2), is("Content-Type: Test"));
//                assertThat(responseMessage.getFilePath(), is(nullValue()));
//
//                StringBuilder builder = new StringBuilder();
//                try (BufferedReader br = new BufferedReader(new FileReader(new File("./src/test/resources/html/index.html")))) {
//                    String str;
//                    while ((str = br.readLine()) != null) {
//                        builder.append(str).append("\n");
//                    }
//                }
//                assertThat(responseMessage.getTarget(), is(builder.toString().getBytes()));
//            }
//        }
//
//        @After
//        public void tearDown() throws Exception {
//            htmlEditor.resetAllFiles();
//        }
//    }

//    public static class createErrorResponseMessageメソッドのテスト {
//
//        @Test
//        public void nullチェック() throws Exception {
//            ResponseMessage sut = ProgramBoardHandler.createErrorResponseMessage(null);
//            assertThat(sut.getStatusLine(), is(nullValue()));
//        }
//
//        @Test
//        public void 正しく動作するか() throws Exception {
//            ResponseMessage sut = ProgramBoardHandler.createErrorResponseMessage(StatusLine.OK);
//            assertThat(sut.getStatusLine(), is(StatusLine.OK));
//            assertThat(sut.getHeaderField().size(), is(1));
//            assertThat(sut.getHeaderField().get(0), is("Content-Type: text/html; charset=UTF-8"));
//
//            sut = ProgramBoardHandler.createErrorResponseMessage(StatusLine.BAD_REQUEST);
//            sut.addHeader("hoge", "hogehoge");
//            assertThat(sut.getStatusLine(), is(StatusLine.BAD_REQUEST));
//            assertThat(sut.getHeaderField().size(), is(2));
//            assertThat(sut.getHeaderField().get(0), is("Content-Type: text/html; charset=UTF-8"));
//            assertThat(sut.getHeaderField().get(1), is("hoge: hogehoge"));
//        }
//    }

    public static class doGetメソッドのテスト {
        private RequestMessage requestMessage;
        private FileOutputStream outputStream = null;
        private ResponseMessage responseMessage;

        @Before
        public void setUp() throws Exception {
            outputStream = new FileOutputStream(new File("./src/test/resources/responseMessage.txt"));
            responseMessage = new ResponseMessage(outputStream);

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
        public void indexをGETしてみる() throws Exception {
            String method = "GET";
            String uri = "/program/board/index.html";
            Map<String, String> headerField = new HashMap<>();
            headerField.put("hoge", "hogehoge");
            requestMessage = new RequestMessage(method, uri, null, headerField, null);

            ProgramBoardHandler.doGet(requestMessage, responseMessage, "timeIdOfValue");

            outputStream.close();

            //Verify
            try (BufferedReader br1 = new BufferedReader(new FileReader(new File("./src/test/resources/responseMessage.txt")));
                 BufferedReader br2 = new BufferedReader(new FileReader(new File("./src/test/resources/Response/GetProgramBoard.txt")))) {

                String str;
                while ((str = br1.readLine()) != null) {
                    assertThat(str, is(br2.readLine()));
                }
            }
        }

        @Test
        public void searchをGETしてみる() throws Exception {
            String method = "GET";
            String uri = "/program/board/search.html";
            Map<String, String> uriQuery = new HashMap<>();
            uriQuery.put("param", "search");
            uriQuery.put("name", "管理者");
            requestMessage = new RequestMessage(method, uri, uriQuery, null, null);

            ProgramBoardHandler.doGet(requestMessage, responseMessage, "timeIdOfValue");

            outputStream.close();

            //Verify
            try (BufferedReader br1 = new BufferedReader(new FileReader(new File("./src/test/resources/responseMessage.txt")));
                 BufferedReader br2 = new BufferedReader(new FileReader(new File("./src/test/resources/Response/GetSearchProgramBoard.txt")))) {

                String str;
                while ((str = br1.readLine()) != null) {
                    assertThat(str, is(br2.readLine()));
                }
            }
        }

        @After
        public void tearDown() throws Exception {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

    public static class doPostメソッドのテスト {
        private RequestMessage requestMessage;
        private FileOutputStream outputStream = null;
        private ResponseMessage responseMessage;

        @Before
        public void setUp() throws Exception {
            outputStream = new FileOutputStream(new File("./src/test/resources/responseMessage.txt"));
            responseMessage = new ResponseMessage(outputStream);

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
        public void paramがwriteの場合() throws Exception {

        }
    }

    public static class sendResponseメソッドのテスト {
        private ByteArrayOutputStream outputStream;
        private ResponseMessage responseMessage;

        @Before
        public void setUp() {
            outputStream = new ByteArrayOutputStream();
            responseMessage = new ResponseMessage(outputStream);
        }

        @Test
        public void 引数に文字列を渡す() {
            ProgramBoardHandler.sendResponse(responseMessage, "hoge");
            String[] response = outputStream.toString().split("\n");

            assertThat(response.length, is(5));
            assertThat(response[0], is("HTTP/1.1 200 OK"));
            assertThat(response[1], is("Content-Type: text/html; charset=UTF-8"));
            assertThat(response[2], is("Content-Length: 4"));
            assertThat(response[3], is(""));
            assertThat(response[4], is("hoge"));
        }

        @Test
        public void 引数にファイルを渡す() {
            ProgramBoardHandler.sendResponse(responseMessage, new File("./src/test/resources/漢字テスト/寿司.txt"));
            String[] response = outputStream.toString().split("\n");

            assertThat(response.length, is(7));
            assertThat(response[0], is("HTTP/1.1 200 OK"));
            assertThat(response[1], is("Content-Type: text/plain"));
            assertThat(response[2], is("Content-Length: 24"));
            assertThat(response[3], is(""));
            assertThat(response[4], is("寿司"));
            assertThat(response[5], is("マグロ"));
            assertThat(response[6], is("イカ"));
        }
    }

    public static class sendErrorResponseメソッドのテスト {
        private ByteArrayOutputStream outputStream;
        private ResponseMessage responseMessage;

        @Before
        public void setUp() {
            outputStream = new ByteArrayOutputStream();
            responseMessage = new ResponseMessage(outputStream);
        }

        @Test
        public void BadRequestテスト() {
            ProgramBoardHandler.sendErrorResponse(responseMessage, StatusLine.BAD_REQUEST);
            String[] response = outputStream.toString().split("\n");

            assertThat(response.length, is(4));
            assertThat(response[0], is("HTTP/1.1 400 Bad Request"));
            assertThat(response[1], is("Content-Type: text/html; charset=UTF-8"));
            assertThat(response[2], is(""));
            assertThat(response[3], is("<html><head><title>400 Bad Request</title></head><body><h1>Bad Request</h1>" +
                    "<p>Your browser sent a request that this server could not understand.<br /></p></body></html>"));
        }

        @Test
        public void NotFoundテスト() {
            ProgramBoardHandler.sendErrorResponse(responseMessage, StatusLine.NOT_FOUND);
            String[] response = outputStream.toString().split("\n");

            assertThat(response.length, is(4));
            assertThat(response[0], is("HTTP/1.1 404 Not Found"));
            assertThat(response[1], is("Content-Type: text/html; charset=UTF-8"));
            assertThat(response[2], is(""));
            assertThat(response[3], is("<html><head><title>404 Not Found</title></head><body><h1>Not Found</h1>" +
                    "<p>お探しのページは見つかりませんでした。</p></body></html>"));
        }
    }
}
