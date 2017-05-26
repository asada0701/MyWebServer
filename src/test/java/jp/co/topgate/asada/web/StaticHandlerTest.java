package jp.co.topgate.asada.web;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.io.*;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

/**
 * StaticHandlerクラスのテスト
 *
 * @author asada
 */
@RunWith(Enclosed.class)
public class StaticHandlerTest {
    public static class コンストラクタのテスト {
        @Test
        public void nullチェック() throws Exception {
            StaticHandler sut = new StaticHandler(null);
            assertThat(sut.getRequestMessage(), is(nullValue()));
        }

        @Test
        public void 正しく動作するか() throws Exception {
            String path = "./src/test/resources/GetRequestMessage.txt";
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(path)))) {
                RequestMessage requestMessage = RequestMessageParser.parse(bis);

                StaticHandler sut = new StaticHandler(requestMessage);
                assertThat(sut.getRequestMessage(), is(requestMessage));
            }
        }
    }

    public static class handleRequestのテスト {
        @Test
        public void ステータスコード200のテスト() throws Exception {
            //SetUp
            try (FileInputStream is = new FileInputStream(new File("./src/test/resources/GetRequestMessage.txt"))) {

                RequestMessage rm = RequestMessageParser.parse(is);
                StaticHandler sut = new StaticHandler(rm);

                assertThat(StaticHandler.decideStatusLine(rm.getMethod(), rm.getUri(), rm.getProtocolVersion()), is(StatusLine.OK));

                //Exercise
                ResponseMessage responseMessage = sut.handleRequest();

                assertThat(responseMessage.getProtocolVersion(), is("HTTP/1.1"));
                assertThat(responseMessage.getStatusLine(), is(StatusLine.OK));
                List<String> headerField = responseMessage.getHeaderField();
                assertThat(headerField.size(), is(2));
                assertThat(headerField.get(0), is("Content-Type: text/html; charset=UTF-8"));
                assertThat(headerField.get(1), is("Content-Length: 714"));
                assertThat(responseMessage.getFilePath(), is("./src/main/resources/index.html"));
                assertThat(responseMessage.getTarget(), is(nullValue()));
            }
        }

        @Test
        public void ステータスコード200以外のテスト() throws Exception {
            //SetUp
            try (FileInputStream is = new FileInputStream(new File("./src/test/resources/NotFound.txt"))) {

                RequestMessage rm = RequestMessageParser.parse(is);
                StaticHandler sut = new StaticHandler(rm);

                assertThat(StaticHandler.decideStatusLine(rm.getMethod(), rm.getUri(), rm.getProtocolVersion()), is(StatusLine.NOT_FOUND));

                //Exercise
                ResponseMessage responseMessage = sut.handleRequest();

                assertThat(responseMessage.getProtocolVersion(), is("HTTP/1.1"));
                assertThat(responseMessage.getStatusLine(), is(StatusLine.NOT_FOUND));
                List<String> headerField = responseMessage.getHeaderField();
                assertThat(headerField.size(), is(1));
                assertThat(headerField.get(0), is("Content-Type: text/html; charset=UTF-8"));
                assertThat(responseMessage.getFilePath(), is(nullValue()));
                assertThat(responseMessage.getTarget(), is(nullValue()));
            }
        }
    }

    public static class decideStatusLineのテスト {
        @Test
        public void nullチェック() throws Exception {
            StatusLine sut = StaticHandler.decideStatusLine("GET", "/index.html", null);
            assertThat(sut.getStatusCode(), is(505));

            sut = StaticHandler.decideStatusLine(null, null, "HTTP/1.1");
            assertThat(sut.getStatusCode(), is(501));

            sut = StaticHandler.decideStatusLine("GET", null, "HTTP/1.1");
            assertThat(sut.getStatusCode(), is(404));

            sut = StaticHandler.decideStatusLine(null, null, null);
            assertThat(sut.getStatusCode(), is(505));
        }

        @Test
        public void GETリクエスト200() throws Exception {
            StatusLine sut = StaticHandler.decideStatusLine("GET", "/index.html", "HTTP/1.1");
            assertThat(sut.getStatusCode(), is(200));
        }

        @Test
        public void 存在しないファイルを指定すると404() throws Exception {
            StatusLine sut = StaticHandler.decideStatusLine("GET", "/hogehoge", "HTTP/1.1");
            assertThat(sut.getStatusCode(), is(404));
        }

        @Test
        public void ディレクトリを指定すると404() throws Exception {
            StatusLine sut = StaticHandler.decideStatusLine("GET", "/", "HTTP/1.1");
            assertThat(sut.getStatusCode(), is(404));
        }

        @Test
        public void GET以外は501() throws Exception {
            StatusLine sut;
            sut = StaticHandler.decideStatusLine("POST", "/", "HTTP/1.1");
            assertThat(sut.getStatusCode(), is(501));

            sut = StaticHandler.decideStatusLine("PUT", "/", "HTTP/1.1");
            assertThat(sut.getStatusCode(), is(501));

            sut = StaticHandler.decideStatusLine("DELETE", "/", "HTTP/1.1");
            assertThat(sut.getStatusCode(), is(501));
        }

        @Test
        public void HTTPのバージョンが指定と異なる505() throws Exception {
            StatusLine sut = StaticHandler.decideStatusLine("GET", "/", "HTTP/2.0");
            assertThat(sut.getStatusCode(), is(505));
        }
    }
}
