package jp.co.topgate.asada.web.app;

import jp.co.topgate.asada.web.StaticHandler;
import jp.co.topgate.asada.web.exception.RequestParseException;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

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
        @Test
        public void 正しいリクエストメッセージを送る() throws Exception {
            try (InputStream is = new FileInputStream(new File("./src/test/resources/GetRequestMessage.txt"));
                 BufferedInputStream bis = new BufferedInputStream(is)) {

                Handler sut = Handler.getHandler(bis);

                assertThat(sut, is(instanceOf(StaticHandler.class)));
            }
        }

        @Test
        public void urlPattern以外のPOSTのテスト() throws Exception {
            try (InputStream is = new FileInputStream(new File("./src/test/resources/NotContainsUrlPatternTest.txt"));
                 BufferedInputStream bis = new BufferedInputStream(is)) {

                Handler sut = Handler.getHandler(bis);

                assertThat(sut, is(instanceOf(StaticHandler.class)));
            }
        }

        @Test
        public void WebAppHandlerが返されるテスト() throws Exception {
            try (InputStream is = new FileInputStream(new File("./src/test/resources/PostRequestMessage.txt"));
                 BufferedInputStream bis = new BufferedInputStream(is)) {

                Handler sut = Handler.getHandler(bis);

                assertThat(sut, is(instanceOf(ProgramBoardHandler.class)));
            }
        }

        @Test(expected = RequestParseException.class)
        public void 誤ったリクエストメッセージを送ると例外が発生する() throws Exception {
            try (InputStream is = new FileInputStream(new File("./src/test/resources/emptyRequestMessage.txt"));
                 BufferedInputStream bis = new BufferedInputStream(is)) {

                Handler.getHandler(bis);
            }
        }
    }

    public static class getFilePathメソッドのテスト {
        @Test
        public void 引数に空が渡された場合() {
            String s = Handler.getFilePath(UrlPattern.PROGRAM_BOARD, "");
            assertThat(s, is("./src/main/resources"));
        }

        @Test(expected = NullPointerException.class)
        public void 引数にnullが渡された場合() {
            Handler.getFilePath(UrlPattern.PROGRAM_BOARD, null);
        }

        @Test
        public void 登録されていないURIが渡された場合() {
            String s = Handler.getFilePath(UrlPattern.PROGRAM_BOARD, "/index.html");
            assertThat(s, is("./src/main/resources/index.html"));
        }

        @Test
        public void 登録されているUIRが渡された場合() {
            String s = Handler.getFilePath(UrlPattern.PROGRAM_BOARD, "/program/board/index.html");
            assertThat(s, is("./src/main/resources/2/index.html"));
        }
    }

    public static class getStatusLineのテスト {
        @Test
        public void GETリクエスト200() throws Exception {
            StatusLine sut = StaticHandler.getStatusLine("GET", "/index.html", "HTTP/1.1");
            assertThat(sut.getStatusCode(), is(200));
        }

        @Test
        public void POSTリクエスト200() throws Exception {
            StatusLine sut = StaticHandler.getStatusLine("POST", "/index.html", "HTTP/1.1");
            assertThat(sut.getStatusCode(), is(200));
        }

        @Test
        public void 存在しないファイルを指定すると404() throws Exception {
            StatusLine sut = StaticHandler.getStatusLine("GET", "/hogehoge", "HTTP/1.1");
            assertThat(sut.getStatusCode(), is(404));
        }

        @Test
        public void ディレクトリを指定すると404() throws Exception {
            StatusLine sut = StaticHandler.getStatusLine("GET", "/", "HTTP/1.1");
            assertThat(sut.getStatusCode(), is(404));
        }

        @Test
        public void GETとPOST以外は501() throws Exception {
            StatusLine sut;
            sut = StaticHandler.getStatusLine("PUT", "/", "HTTP/1.1");
            assertThat(sut.getStatusCode(), is(501));

            sut = StaticHandler.getStatusLine("DELETE", "/", "HTTP/1.1");
            assertThat(sut.getStatusCode(), is(501));
        }

        @Test
        public void HTTPのバージョンが指定と異なる505() throws Exception {
            StatusLine sut = StaticHandler.getStatusLine("GET", "/", "HTTP/2.0");
            assertThat(sut.getStatusCode(), is(505));
        }
    }
}
