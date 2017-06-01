package jp.co.topgate.asada.web;

import org.junit.Before;
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

    public static class checkMethodのテスト {
        private StaticHandler sut;

        @Before
        public void setUp() throws Exception {
            sut = new StaticHandler(null, null);
        }

        @Test(expected = NullPointerException.class)
        public void nullチェック() {
            sut.checkMethod(null);
        }

        @Test
        public void GETを渡すとtrue() throws Exception {
            assertThat(sut.checkMethod("GET"), is(true));
        }

        @Test
        public void GET以外はfalse() throws Exception {
            assertThat(sut.checkMethod("POST"), is(false));

            assertThat(sut.checkMethod("PUT"), is(false));

            assertThat(sut.checkMethod(""), is(false));
        }
    }
}
