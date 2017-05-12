package jp.co.topgate.asada.web.app;

import jp.co.topgate.asada.web.StaticHandler;
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
 * Created by yusuke-pc on 2017/05/09.
 */
@RunWith(Enclosed.class)
public class HandlerTest {
    public static class getHandlerメソッドのテスト {
        @Test
        public void 正しいリクエストメッセージを送る() throws Exception {
            try (InputStream is = new FileInputStream(new File("./src/test/resources/requestMessage.txt"));
                 BufferedInputStream bis = new BufferedInputStream(is)) {

                Handler sut = Handler.getHandler(bis);

                assertThat(sut, is(instanceOf(StaticHandler.class)));
            }
        }

        @Test
        public void WebAppHandlerが返されるテスト() throws Exception {
            try (InputStream is = new FileInputStream(new File("./src/test/resources/samplePostRequest.txt"));
                 BufferedInputStream bis = new BufferedInputStream(is)) {

                Handler sut = Handler.getHandler(bis);

                assertThat(sut, is(instanceOf(ProgramBoardHandler.class)));
            }
        }

        @Test
        public void 誤ったリクエストメッセージを送る() throws Exception {
            try (InputStream is = new FileInputStream(new File("./src/test/resources/empty.txt"));
                 BufferedInputStream bis = new BufferedInputStream(is)) {

                Handler sut = Handler.getHandler(bis);

                assertThat(sut, is(instanceOf(StaticHandler.class)));
            }
        }
    }

    public static class requestComesメソッドのテスト {
        @Test
        public void ステータスコード200のテスト() throws Exception {
            try (InputStream is = new FileInputStream(new File("./src/test/resources/requestMessage.txt"));
                 BufferedInputStream bis = new BufferedInputStream(is)) {

                bis.mark(bis.available());
                Handler sut = Handler.getHandler(bis);
                bis.reset();
                sut.requestComes(bis);
                assertThat(sut.getStatusCode(), is(200));
            }
        }

        @Test
        public void ステータスコード400のテスト() throws Exception {
            try (InputStream is = new FileInputStream(new File("./src/test/resources/empty.txt"));
                 BufferedInputStream bis = new BufferedInputStream(is)) {

                bis.mark(bis.available());
                Handler sut = Handler.getHandler(bis);
                bis.reset();
                sut.requestComes(bis);
                assertThat(sut.getStatusCode(), is(400));
            }
        }

        @Test
        public void ステータスコード404のテスト() throws Exception {
            try (InputStream is = new FileInputStream(new File("./src/test/resources/NotExistTest.txt"));
                 BufferedInputStream bis = new BufferedInputStream(is)) {

                bis.mark(bis.available());
                Handler sut = Handler.getHandler(bis);
                bis.reset();
                sut.requestComes(bis);
                assertThat(sut.getStatusCode(), is(404));
            }
        }

        @Test
        public void ステータスコード501のテスト() throws Exception {
            try (InputStream is = new FileInputStream(new File("./src/test/resources/HTTPMethodTest.txt"));
                 BufferedInputStream bis = new BufferedInputStream(is)) {

                bis.mark(bis.available());
                Handler sut = Handler.getHandler(bis);
                bis.reset();
                sut.requestComes(bis);
                assertThat(sut.getStatusCode(), is(501));
            }
        }

        @Test
        public void ステータスコード505のテスト() throws Exception {
            try (InputStream is = new FileInputStream(new File("./src/test/resources/HTTPVersionTest.txt"));
                 BufferedInputStream bis = new BufferedInputStream(is)) {

                bis.mark(bis.available());
                Handler sut = Handler.getHandler(bis);
                bis.reset();
                sut.requestComes(bis);
                assertThat(sut.getStatusCode(), is(505));
            }
        }
    }

    public static class getFilePathメソッドのテスト {
        @Test
        public void 引数に空が渡された場合() {
            String s = Handler.getFilePath("");
            assertThat(s, is("./src/main/resources/"));
        }

        @Test
        public void 引数にnullが渡された場合() {
            String s = Handler.getFilePath(null);
            assertThat(s, is("./src/main/resources/"));
        }

        @Test
        public void 登録されていないURIが渡された場合() {
            String s = Handler.getFilePath("/index.html");
            assertThat(s, is("./src/main/resources/index.html"));
        }

        @Test
        public void 登録されているUIRが渡された場合() {
            String s = Handler.getFilePath("/program/board/index.html");
            assertThat(s, is("./src/main/resources/2/index.html"));
        }
    }
}
