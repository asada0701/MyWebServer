package jp.co.topgate.asada.web;

import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * ResponseMessageクラスのテスト
 *
 * @author asada
 */
@RunWith(Enclosed.class)
public class ResponseMessageTest {
    public static class コンストラクタのテスト {
        @Test(expected = NullPointerException.class)
        public void nullチェック() {
            ResponseMessage responseMessage = new ResponseMessage(null);
            responseMessage.writeResponseLineAndHeader(StatusLine.OK);
        }

        @Test
        public void 正しく動作するか() throws Exception {
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                ResponseMessage responseMessage = new ResponseMessage(outputStream);
                assertThat(responseMessage.getOutputStream(), is(outputStream));
            }
        }
    }

    public static class writeResponseLineAndHeaderメソッドのテスト {
        @Test
        public void 正しく動作するか() throws Exception {
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                ResponseMessage responseMessage = new ResponseMessage(outputStream);

                responseMessage.writeResponseLineAndHeader(StatusLine.OK);

                assertThat(outputStream.toByteArray(), is("HTTP/1.1 200 OK\n\n".getBytes()));
            }
        }

        @Test
        public void 複数回呼び出してみる() throws Exception {
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                ResponseMessage responseMessage = new ResponseMessage(outputStream);

                //メソッドが実行される
                responseMessage.writeResponseLineAndHeader(StatusLine.OK);

                //実行されない
                responseMessage.writeResponseLineAndHeader(StatusLine.BAD_REQUEST);
                responseMessage.writeResponseLineAndHeader(StatusLine.NOT_FOUND);

                assertThat(outputStream.toByteArray(), is("HTTP/1.1 200 OK\n\n".getBytes()));
            }
        }
    }

    public static class createResponseLineメソッドのテスト {

        @NotNull
        private String createResponseLineHelper(StatusLine statusLine) {
            return ResponseMessage.createResponseLine(statusLine.getStatusCode(), statusLine.getReasonPhrase());
        }

        @Test
        public void ステータスコード200のテスト() {
            String responseLine = createResponseLineHelper(StatusLine.OK);
            assertThat(responseLine, is("HTTP/1.1 200 OK\n"));
        }

        @Test
        public void ステータスコード400のテスト() {
            String responseLine = createResponseLineHelper(StatusLine.BAD_REQUEST);
            assertThat(responseLine, is("HTTP/1.1 400 Bad request\n"));
        }

        @Test
        public void ステータスコード404のテスト() {
            String responseLine = createResponseLineHelper(StatusLine.NOT_FOUND);
            assertThat(responseLine, is("HTTP/1.1 404 Not Found\n"));
        }

        @Test
        public void ステータスコード500のテスト() {
            String responseLine = createResponseLineHelper(StatusLine.INTERNAL_SERVER_ERROR);
            assertThat(responseLine, is("HTTP/1.1 500 Internal Server Error\n"));
        }

        @Test
        public void ステータスコード501のテスト() {
            String responseLine = createResponseLineHelper(StatusLine.NOT_IMPLEMENTED);
            assertThat(responseLine, is("HTTP/1.1 501 Not Implemented\n"));
        }

        @Test
        public void ステータスコード505のテスト() {
            String responseLine = createResponseLineHelper(StatusLine.HTTP_VERSION_NOT_SUPPORTED);
            assertThat(responseLine, is("HTTP/1.1 505 HTTP Version Not Supported\n"));
        }
    }

    public static class createHeaderメソッドのテスト {
        @Test(expected = NullPointerException.class)
        public void nullチェック() {
            ResponseMessage.createHeader(null);
        }

        @Test
        public void 空チェック() {
            List<String> list = new ArrayList<>();
            String sut = ResponseMessage.createHeader(list);
            assertThat(sut, is("\n"));
        }

        @Test
        public void 正しく動作するか() {
            List<String> list = new ArrayList<>();
            list.add("Connection: Keep-Alive");
            list.add("Content-Type: text/html; charset=UTF-8");

            String sut = ResponseMessage.createHeader(list);
            assertThat(sut, is("Connection: Keep-Alive\nContent-Type: text/html; charset=UTF-8\n\n"));
        }
    }

    public static class getErrorMessageBodyメソッドのテスト {
        @Test
        public void nullチェック() {
            String str = ResponseMessage.getErrorMessageBody(null);
            assertThat(str, is("<html><head><title>500 Internal Server Error</title></head>" +
                    "<body><h1>Internal Server Error</h1>" +
                    "<p>サーバー内部のエラーにより表示できません。ごめんなさい。</p></body></html>"));
        }

        @Test
        public void BadRequest() {
            String str = ResponseMessage.getErrorMessageBody(StatusLine.BAD_REQUEST);
            assertThat(str, is("<html><head><title>400 Bad request</title></head>" +
                    "<body><h1>Bad request</h1>" +
                    "<p>Your browser sent a request that this server could not understand.<br /></p></body></html>"));
        }

        @Test
        public void NotFound() {
            String str = ResponseMessage.getErrorMessageBody(StatusLine.NOT_FOUND);
            assertThat(str, is("<html><head><title>404 Not Found</title></head>" +
                    "<body><h1>Not Found</h1>" +
                    "<p>お探しのページは見つかりませんでした。</p></body></html>"));
        }

        @Test
        public void InternalServerError() {
            String str = ResponseMessage.getErrorMessageBody(StatusLine.INTERNAL_SERVER_ERROR);
            assertThat(str, is("<html><head><title>500 Internal Server Error</title></head>" +
                    "<body><h1>Internal Server Error</h1>" +
                    "<p>サーバー内部のエラーにより表示できません。ごめんなさい。</p></body></html>"));
        }

        @Test
        public void NotImplemented() {
            String str = ResponseMessage.getErrorMessageBody(StatusLine.NOT_IMPLEMENTED);
            assertThat(str, is("<html><head><title>501 Not Implemented</title></head>" +
                    "<body><h1>Not Implemented</h1>" +
                    "<p>Webサーバーでメソッドが実装されていません。</p></body></html>"));
        }

        @Test
        public void HTTPVersionNotSupported() {
            String str = ResponseMessage.getErrorMessageBody(StatusLine.HTTP_VERSION_NOT_SUPPORTED);
            assertThat(str, is("<html><head><title>505 HTTP Version Not Supported</title></head>" +
                    "<body><h1>HTTP Version Not Supported</h1></body></html>"));
        }
    }

    public static class addHeaderメソッドのテスト {
        private ByteArrayOutputStream outputStream = null;
        private ResponseMessage sut;

        @Before
        public void setUp() {
            outputStream = new ByteArrayOutputStream();
            sut = new ResponseMessage(outputStream);
        }

        @Test
        public void addHeaderのテスト() throws Exception {
            sut = new ResponseMessage(outputStream);
            sut.addHeader("name", "value");
            sut.addHeader("name2", "value2");
            sut.writeResponseLineAndHeader(StatusLine.OK);

            assertThat(outputStream.toString(), is("HTTP/1.1 200 OK\nname: value\nname2: value2\n\n"));
        }

        @Test
        public void addHeaderWithContentTypeのテスト() throws Exception {
            sut = new ResponseMessage(outputStream);
            sut.addHeaderWithContentType("value");
            sut.writeResponseLineAndHeader(StatusLine.OK);

            assertThat(outputStream.toString(), is("HTTP/1.1 200 OK\nContent-Type: value\n\n"));
        }

        @Test
        public void addHeaderWithContentLengthのテスト() throws Exception {
            sut = new ResponseMessage(outputStream);
            sut.addHeaderWithContentLength("value");
            sut.writeResponseLineAndHeader(StatusLine.OK);

            assertThat(outputStream.toString(), is("HTTP/1.1 200 OK\nContent-Length: value\n\n"));
        }

        @After
        public void tearDown() throws IOException {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }
}
