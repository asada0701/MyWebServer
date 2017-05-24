package jp.co.topgate.asada.web;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.io.*;

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
    public static class 定数のテスト {
        @Test
        public void プロトコルバージョンのテスト() {
            assertThat(StaticHandler.PROTOCOL_VERSION, is("HTTP/1.1"));
        }

        @Test
        public void メソッドのテスト() {
            assertThat(StaticHandler.METHOD, is("GET"));
        }
    }

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
            String path = "./src/test/resources/responseMessage.txt";
            try (FileOutputStream fos = new FileOutputStream(path);
                 FileInputStream is = new FileInputStream(new File("./src/test/resources/GetRequestMessage.txt"))) {

                RequestMessage rm = RequestMessageParser.parse(is);
                StaticHandler sut = new StaticHandler(rm);

                assertThat(StaticHandler.decideStatusLine(rm.getMethod(), rm.getUri(), rm.getProtocolVersion()), is(StatusLine.OK));

                //Exercise
                sut.handleRequest(fos);
            }

            //Verify
            StringBuilder builder1 = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path))))) {
                String str;
                while ((str = br.readLine()) != null) {
                    builder1.append(str).append("\n");
                }
            }
            path = "./src/test/resources/html/index.html";
            StringBuilder builder2 = new StringBuilder();
            builder2.append("HTTP/1.1 200 OK").append("\n");
            builder2.append("Content-Type: text/html; charset=UTF-8").append("\n");
            builder2.append("Content-Length: 714").append("\n").append("\n");
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path))))) {
                String str;
                while ((str = br.readLine()) != null) {
                    builder2.append(str).append("\n");
                }
            }

            assertThat(builder1.toString(), is(builder2.toString()));
        }

        @Test
        public void ステータスコード200以外のテスト() throws Exception {
            //SetUp
            String path = "./src/test/resources/responseMessage.txt";
            try (FileOutputStream fos = new FileOutputStream(path);
                 FileInputStream is = new FileInputStream(new File("./src/test/resources/NotFound.txt"))) {

                RequestMessage rm = RequestMessageParser.parse(is);
                StaticHandler sut = new StaticHandler(rm);

                assertThat(StaticHandler.decideStatusLine(rm.getMethod(), rm.getUri(), rm.getProtocolVersion()), is(StatusLine.NOT_FOUND));

                //Exercise
                sut.handleRequest(fos);
            }
            //Verify
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path))))) {
                assertThat(br.readLine(), is("HTTP/1.1 404 Not Found"));
                assertThat(br.readLine(), is("Content-Type: text/html; charset=UTF-8"));
                assertThat(br.readLine(), is(""));
                assertThat(br.readLine(), is("<html><head><title>404 Not Found</title></head><body><h1>Not Found</h1><p>お探しのページは見つかりませんでした。</p></body></html>"));
                assertThat(br.readLine(), is(nullValue()));
            }
        }
    }

    public static class decideStatusLineのテスト {
        @Test
        public void nullチェック() throws Exception {
            StatusLine sut = StaticHandler.decideStatusLine(null, null, null);
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
