package jp.co.topgate.asada.web;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
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
            StaticHandler sut = new StaticHandler(null, null);
            assertThat(sut.getRequestMessage(), is(nullValue()));
            assertThat(sut.getResponseMessage(), is(nullValue()));
        }

        @Test
        public void 正しく動作するか() throws Exception {
            String path = "./src/test/resources/request/GetRequestMessage.txt";
            RequestMessage requestMessage;
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(path)))) {
                requestMessage = RequestMessageParser.parse(bis);
            }
            ResponseMessage responseMessage = new ResponseMessage(null);

            StaticHandler sut = new StaticHandler(requestMessage, responseMessage);
            assertThat(sut.getRequestMessage(), is(requestMessage));
            assertThat(sut.getResponseMessage(), is(responseMessage));
        }
    }

    public static class handleRequestのテスト {
        @Test
        public void 正しく動作するか() throws Exception {
            try (FileInputStream fileInputStream = new FileInputStream(new File("./src/test/resources/request/GetRequestMessage.txt"));
                 FileOutputStream fileOutputStream = new FileOutputStream(new File("./src/test/resources/responseMessage.txt"))) {

                RequestMessage requestMessage = RequestMessageParser.parse(fileInputStream);
                ResponseMessage responseMessage = new ResponseMessage(fileOutputStream);

                Handler handler = new StaticHandler(requestMessage, responseMessage);
                handler.handleRequest();
            }

            try (BufferedReader responseMessage = new BufferedReader(new FileReader(new File("./src/test/resources/responseMessage.txt")));
                 BufferedReader testData = new BufferedReader(new FileReader(new File("./src/test/resources/response/getIndexHtml.txt")))) {

                String str;
                while ((str = responseMessage.readLine()) != null) {
                    assertThat(str, is(testData.readLine()));
                }
            }
        }
    }

    public static class changeUriToWelcomePageのテスト {
        @Test(expected = NullPointerException.class)
        public void nullチェック() throws Exception {
            StaticHandler.changeUriToWelcomePage(null);
        }

        @Test
        public void 正しく動作するか() throws Exception {
            assertThat(StaticHandler.changeUriToWelcomePage("/"), is("/index.html"));

            assertThat(StaticHandler.changeUriToWelcomePage("///////"), is("///////index.html"));

            assertThat(StaticHandler.changeUriToWelcomePage("/hoge//"), is("/hoge//index.html"));
        }

        @Test
        public void 引数にスラッシュが含まれない場合() throws Exception {
            assertThat(StaticHandler.changeUriToWelcomePage(""), is(""));

            assertThat(StaticHandler.changeUriToWelcomePage("hoge"), is("hoge"));
        }
    }

    public static class decideStatusLineのテスト {
        @Test
        public void nullチェック() throws Exception {
            StatusLine sut;

            sut = StaticHandler.decideStatusLine(null, Paths.get("./src/resources/static/index.html"));
            assertThat(sut.getStatusCode(), is(501));

            sut = StaticHandler.decideStatusLine(null, null);
            assertThat(sut.getStatusCode(), is(501));
        }

        @Test(expected = NullPointerException.class)
        public void filePathがnull() throws Exception {
            StaticHandler.decideStatusLine("GET", null);
        }

        @Test
        public void GETリクエスト200() throws Exception {
            StatusLine sut = StaticHandler.decideStatusLine("GET", Paths.get("./src/main/resources/static/index.html"));
            assertThat(sut.getStatusCode(), is(200));
        }

        @Test
        public void 存在しないファイルを指定すると404() throws Exception {
            StatusLine sut = StaticHandler.decideStatusLine("GET", Paths.get("/hogehoge"));
            assertThat(sut.getStatusCode(), is(404));
        }

        @Test
        public void ディレクトリを指定すると404() throws Exception {
            StatusLine sut = StaticHandler.decideStatusLine("GET", Paths.get("/"));
            assertThat(sut.getStatusCode(), is(404));
        }

        @Test
        public void GET以外は501() throws Exception {
            StatusLine sut;
            sut = StaticHandler.decideStatusLine("POST", Paths.get("/"));
            assertThat(sut.getStatusCode(), is(501));

            sut = StaticHandler.decideStatusLine("PUT", Paths.get("/"));
            assertThat(sut.getStatusCode(), is(501));

            sut = StaticHandler.decideStatusLine("DELETE", Paths.get("/"));
            assertThat(sut.getStatusCode(), is(501));
        }
    }

    public static class sendResponseのテスト {
        private FileOutputStream fileOutputStream = null;
        private ResponseMessage responseMessage = null;

        @Before
        public void setUp() throws Exception {
            fileOutputStream = new FileOutputStream(new File("./src/test/resources/responseMessage.txt"));
            responseMessage = new ResponseMessage(fileOutputStream);
        }

        @Test
        public void ステータスコード200のテスト() throws Exception {
            StaticHandler.sendResponse(responseMessage, StatusLine.OK, Paths.get("./src/test/resources/html/index.html"));

            try (BufferedReader responseMessage = new BufferedReader(new FileReader(new File("./src/test/resources/responseMessage.txt")));
                 BufferedReader testData = new BufferedReader(new FileReader(new File("./src/test/resources/Response/getIndexHtml.txt")))) {

                String str;
                while ((str = responseMessage.readLine()) != null) {
                    assertThat(str, is(testData.readLine()));
                }
            }
        }

        @Test
        public void ステータスコード200以外のテスト() throws Exception {
            StaticHandler.sendResponse(responseMessage, StatusLine.NOT_FOUND, null);

            try (BufferedReader responseMessage = new BufferedReader(new FileReader(new File("./src/test/resources/responseMessage.txt")));
                 BufferedReader testData = new BufferedReader(new FileReader(new File("./src/test/resources/response/NotFound.txt")))) {

                String str;
                while ((str = responseMessage.readLine()) != null) {
                    assertThat(str, is(testData.readLine()));
                }
            }
        }

        @After
        public void tearDown() throws Exception {
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
        }
    }
}
