package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.program.board.ProgramBoardHandler;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;

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
            try (InputStream is = new FileInputStream(new File("./src/test/resources/Request/GetRequestMessage.txt"))) {

                RequestMessage requestMessage = RequestMessageParser.parse(is);

                Handler sut = Handler.getHandler(requestMessage, null);

                assertThat(sut, is(instanceOf(StaticHandler.class)));
            }
        }

        @Test
        public void urlPattern以外のPOSTのテスト() throws Exception {
            try (InputStream is = new FileInputStream(new File("./src/test/resources/Request/NotContainsUrlPatternTest.txt"))) {

                RequestMessage requestMessage = RequestMessageParser.parse(is);

                Handler sut = Handler.getHandler(requestMessage, null);

                assertThat(sut, is(instanceOf(StaticHandler.class)));
            }
        }

        @Test
        public void ProgramBoardHandlerが返されるテスト() throws Exception {
            try (InputStream is = new FileInputStream(new File("./src/test/resources/Request/PostRequestMessage.txt"))) {

                RequestMessage requestMessage = RequestMessageParser.parse(is);

                Handler sut = Handler.getHandler(requestMessage, null);

                assertThat(sut, is(instanceOf(ProgramBoardHandler.class)));
            }
        }
    }

    public static class getFilePathメソッドのテスト {
        private Path path;

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

            path = Handler.getFilePath(null);
            assertThat(path.toString(), is("./src/main/resources/null"));

            path = Handler.getFilePath("//////////////hoge////////////");
            assertThat(path.toString(), is("./src/main/resources/hoge"));

            path = Handler.getFilePath("./hoge/");
            assertThat(path.toString(), is("./src/main/resources/./hoge"));

            path = Handler.getFilePath(".../hoge/./");
            assertThat(path.toString(), is("./src/main/resources/.../hoge/."));
        }
    }
}
