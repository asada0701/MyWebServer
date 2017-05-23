package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.app.Handler;
import jp.co.topgate.asada.web.app.UrlPattern;
import jp.co.topgate.asada.web.program.board.ProgramBoardHandler;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

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
            try (InputStream is = new FileInputStream(new File("./src/test/resources/GetRequestMessage.txt"))) {

                RequestMessage requestMessage = RequestMessageParser.parseRequestMessage(is);

                Handler sut = Handler.getHandler(requestMessage);

                assertThat(sut, is(instanceOf(StaticHandler.class)));
            }
        }

        @Test
        public void urlPattern以外のPOSTのテスト() throws Exception {
            try (InputStream is = new FileInputStream(new File("./src/test/resources/NotContainsUrlPatternTest.txt"))) {

                RequestMessage requestMessage = RequestMessageParser.parseRequestMessage(is);

                Handler sut = Handler.getHandler(requestMessage);

                assertThat(sut, is(instanceOf(StaticHandler.class)));
            }
        }

        @Test
        public void WebAppHandlerが返されるテスト() throws Exception {
            try (InputStream is = new FileInputStream(new File("./src/test/resources/PostRequestMessage.txt"))) {

                RequestMessage requestMessage = RequestMessageParser.parseRequestMessage(is);

                Handler sut = Handler.getHandler(requestMessage);

                assertThat(sut, is(instanceOf(ProgramBoardHandler.class)));
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
