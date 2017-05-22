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
}
