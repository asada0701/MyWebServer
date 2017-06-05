package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.program.board.ProgramBoardHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Handlerクラスをテストする
 *
 * @author asada
 */
@RunWith(Enclosed.class)
public class HandlerTest {
    public static class getHandlerメソッドのテスト {
        private Handler setUpHandler(Path filePath) throws Exception {
            try (InputStream is = new FileInputStream(filePath.toFile())) {

                RequestMessage requestMessage = RequestMessageParser.parse(is);

                return Handler.getHandler(requestMessage, null);
            }
        }

        @Test
        public void 正しいリクエストメッセージを送る() throws Exception {
            Handler sut = setUpHandler(Paths.get("./src/test/resources/request/GetRequestMessage.txt"));
            assertThat(sut, is(instanceOf(StaticHandler.class)));
        }

        @Test
        public void urlPattern以外のPOSTのテスト() throws Exception {
            Handler sut = setUpHandler(Paths.get("./src/test/resources/request/NotContainsUrlPatternTest.txt"));
            assertThat(sut, is(instanceOf(StaticHandler.class)));
        }

        @Test
        public void ProgramBoardHandlerが返されるテスト() throws Exception {
            Handler sut = setUpHandler(Paths.get("./src/test/resources/request/PostRequestMessage.txt"));
            assertThat(sut, is(instanceOf(ProgramBoardHandler.class)));
        }
    }

    public static class getFilePathメソッドのテスト {
        private Path path;

        @Test(expected = NullPointerException.class)
        public void nullチェック() {
            path = Handler.getFilePath(null);
        }

        @Test
        public void 想定している引数() {
            path = Handler.getFilePath("index.html");
            assertThat(path.toString(), is("./src/main/resources/index.html"));

            path = Handler.getFilePath("/program/board/index.html");
            assertThat(path.toString(), is("./src/main/resources/program/board/index.html"));
        }

        @Test
        public void 想定していない引数() {
            path = Handler.getFilePath("");
            assertThat(path.toString(), is("./src/main/resources"));

            path = Handler.getFilePath("//////////////hoge////////////");
            assertThat(path.toString(), is("./src/main/resources/hoge"));

            path = Handler.getFilePath("./hoge/");
            assertThat(path.toString(), is("./src/main/resources/./hoge"));

            path = Handler.getFilePath(".../hoge/./");
            assertThat(path.toString(), is("./src/main/resources/.../hoge/."));
        }
    }

    public static class changeUriToWelcomePageのテスト {
        @Test(expected = NullPointerException.class)
        public void nullチェック() throws Exception {
            Handler.changeUriToWelcomePage(null);
        }

        @Test
        public void 正しく動作するか() throws Exception {
            assertThat(Handler.changeUriToWelcomePage("/"), is("/index.html"));

            assertThat(Handler.changeUriToWelcomePage("///////"), is("///////index.html"));

            assertThat(Handler.changeUriToWelcomePage("/hoge//"), is("/hoge//index.html"));
        }

        @Test
        public void 引数にスラッシュが含まれない場合() throws Exception {
            assertThat(Handler.changeUriToWelcomePage(""), is(""));

            assertThat(Handler.changeUriToWelcomePage("hoge"), is("hoge"));
        }
    }

    public static class sendResponseメソッドのテスト {
        private ByteArrayOutputStream outputStream;
        private ResponseMessage responseMessage;
        private Handler handler;

        @Before
        public void setUp() {
            outputStream = new ByteArrayOutputStream();
            responseMessage = new ResponseMessage(outputStream);
            handler = new Handler() {
                @Override
                public void handleRequest() {

                }
            };
        }

        @Test(expected = NullPointerException.class)
        public void nullチェック() {
            handler.sendResponse(null, "hoge");
        }

        @Test
        public void 引数に文字列を渡す() {
            handler.sendResponse(responseMessage, "hoge");
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
            handler.sendResponse(responseMessage, Paths.get("./src/test/resources/漢字テスト/寿司.txt"));
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
        private Handler handler;

        public String[] setUp(StatusLine statusLine) {
            outputStream = new ByteArrayOutputStream();
            responseMessage = new ResponseMessage(outputStream);
            handler = new Handler() {
                @Override
                public void handleRequest() {

                }
            };
            handler.sendErrorResponse(responseMessage, statusLine);
            return outputStream.toString().split("\n");
        }

        @Test
        public void BadRequestテスト() {
            String[] response = setUp(StatusLine.BAD_REQUEST);

            assertThat(response.length, is(4));
            assertThat(response[0], is("HTTP/1.1 400 Bad request"));
            assertThat(response[1], is("Content-Type: text/html; charset=UTF-8"));
            assertThat(response[2], is(""));
            assertThat(response[3], is("<html><head><title>400 Bad request</title></head><body><h1>Bad request</h1>" +
                    "<p>Your browser sent a request that this server could not understand.<br /></p></body></html>"));
        }

        @Test
        public void NotFoundテスト() {
            String[] response = setUp(StatusLine.NOT_FOUND);

            assertThat(response.length, is(4));
            assertThat(response[0], is("HTTP/1.1 404 Not Found"));
            assertThat(response[1], is("Content-Type: text/html; charset=UTF-8"));
            assertThat(response[2], is(""));
            assertThat(response[3], is("<html><head><title>404 Not Found</title></head><body><h1>Not Found</h1>" +
                    "<p>お探しのページは見つかりませんでした。</p></body></html>"));
        }
    }
}
