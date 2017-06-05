package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.exception.RequestParseException;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * RequestMessageParserクラスのテスト
 */
@RunWith(Enclosed.class)
public class RequestMessageParserTest {
    public static class parseメソッドのテスト {

        RequestMessage sut;

        private void setUpRequestMessage(Path filePath) throws Exception {
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(filePath.toFile()))) {
                sut = RequestMessageParser.parse(bis);
            }
        }

        @Test(expected = RequestParseException.class)
        public void 空チェック() throws Exception {
            setUpRequestMessage(Paths.get("./src/test/resources/request/emptyRequestMessage.txt"));
        }

        @Test
        public void GETの場合正しく動作するか() throws Exception {
            setUpRequestMessage(Paths.get("./src/test/resources/request/GetRequestMessage.txt"));

            assertThat(sut.getMethod(), is("GET"));
            assertThat(sut.getUri(), is("/index.html"));

            assertThat(sut.findUriQuery("name"), is("asada"));
            assertThat(sut.findUriQuery("like"), is("cat"));

            assertThat(sut.findUriQuery(null), is(nullValue()));
            assertThat(sut.findUriQuery(""), is(nullValue()));

            assertThat(sut.findHeaderByName("Host"), is("localhost:8080"));
            assertThat(sut.findHeaderByName("Connection"), is("keep-alive"));
            assertThat(sut.findHeaderByName("Pragma"), is("no-cache"));
            assertThat(sut.findHeaderByName("Cache-Control"), is("no-cache"));
            assertThat(sut.findHeaderByName("Upgrade-Insecure-Requests"), is("1"));
            assertThat(sut.findHeaderByName("User-Agent"), is("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36"));
            assertThat(sut.findHeaderByName("Accept"), is("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"));
            assertThat(sut.findHeaderByName("Accept-Encoding"), is("gzip, deflate, sdch, br"));
            assertThat(sut.findHeaderByName("Accept-Language"), is("ja,en-US;q=0.8,en;q=0.6"));
            assertThat(sut.findHeaderByName("Foo"), is("Bar:Fizz:Buzz"));

            assertThat(sut.findHeaderByName(null), is(nullValue()));
            assertThat(sut.findHeaderByName(""), is(nullValue()));
        }

        @Test
        public void POSTの場合正しく動作するか() throws Exception {
            setUpRequestMessage(Paths.get("./src/test/resources/request/PostRequestMessage.txt"));

            assertThat(sut.getMethod(), is("POST"));
            assertThat(sut.getUri(), is("/program/board/"));

            assertThat(sut.findHeaderByName("Host"), is("localhost:8080"));
            assertThat(sut.findHeaderByName("Connection"), is("keep-alive"));
            assertThat(sut.findHeaderByName("Pragma"), is("no-cache"));
            assertThat(sut.findHeaderByName("Cache-Control"), is("no-cache"));
            assertThat(sut.findHeaderByName("Upgrade-Insecure-Requests"), is("1"));
            assertThat(sut.findHeaderByName("User-Agent"), is("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36"));
            assertThat(sut.findHeaderByName("Accept"), is("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"));
            assertThat(sut.findHeaderByName("Accept-Encoding"), is("gzip, deflate, sdch, br"));
            assertThat(sut.findHeaderByName("Accept-Language"), is("ja,en-US;q=0.8,en;q=0.6"));
            assertThat(sut.findHeaderByName("Content-Type"), is("application/x-www-form-urlencoded"));
            assertThat(sut.findHeaderByName("Content-Length"), is("123"));

            Map<String, String> messageBody = sut.parseMessageBodyToMap();
            assertThat(messageBody.get("name"), is("asada"));
            assertThat(messageBody.get("title"), is("test"));
            assertThat(messageBody.get("text"), is("こんにちは"));
            assertThat(messageBody.get("password"), is("test"));
            assertThat(messageBody.get("param"), is("contribution"));
        }
    }

    public static class parseUriQueryメソッドのテスト {
        @Test(expected = NullPointerException.class)
        public void nullチェック() throws Exception {
            RequestMessageParser.parseUriQuery(null);
        }

        @Test(expected = RequestParseException.class)
        public void 空チェック() throws Exception {
            RequestMessageParser.parseUriQuery("");
        }

        @Test
        public void 正しく動作するか() throws Exception {
            Map<String, String> sut = RequestMessageParser.parseUriQuery("name=朝田&like=cat");
            assertThat(sut.get("name"), is("朝田"));
            assertThat(sut.get("like"), is("cat"));
        }
    }

    public static class readRequestLineメソッドのテスト {
        @Test
        public void 正しく動作するか() throws Exception {
            try (InputStream inputStream = new ByteArrayInputStream("GET / HTTP/1.1".getBytes("UTF-8"))) {
                String[] requestLine = RequestMessageParser.readRequestLine(inputStream);
                assertThat(requestLine[0], is("GET"));
                assertThat(requestLine[1], is("/"));
                assertThat(requestLine[2], is("HTTP/1.1"));
            }
        }
    }

    public static class readHeaderFieldメソッドのテスト {
        @Test
        public void 正しく動作するか() throws Exception {
            String path = "./src/test/resources/request/GetRequestMessage.txt";
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(path)))) {

                Map<String, String> headerField = RequestMessageParser.readHeaderField(bis);
                assertThat(headerField.get("Host"), is("localhost:8080"));
                assertThat(headerField.get("Connection"), is("keep-alive"));
                assertThat(headerField.get("Pragma"), is("no-cache"));
                assertThat(headerField.get("Cache-Control"), is("no-cache"));
                assertThat(headerField.get("Upgrade-Insecure-Requests"), is("1"));
                assertThat(headerField.get("User-Agent"), is("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36"));
                assertThat(headerField.get("Accept"), is("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8"));
                assertThat(headerField.get("Accept-Encoding"), is("gzip, deflate, sdch, br"));
                assertThat(headerField.get("Accept-Language"), is("ja,en-US;q=0.8,en;q=0.6"));

            }
        }
    }

    public static class countRequestLineAndHeaderLengthメソッドのテスト {
        @Test(expected = NullPointerException.class)
        public void nullチェック() throws Exception {
            RequestMessageParser.countRequestLineAndHeaderLength(null);
        }

        @Test
        public void GETリクエストの場合正しく動作するか() throws Exception {
            String path = "./src/test/resources/request/GetRequestMessage.txt";
            try (InputStream inputStream = new FileInputStream(new File(path))) {
                int num = RequestMessageParser.countRequestLineAndHeaderLength(inputStream);
                assertThat(num, is((int) new File(path).length() + 2));
            }
        }

        @Test
        public void POSTリクエストの場合正しく動作するか() throws Exception {
            String path = "./src/test/resources/request/PostRequestMessage.txt";
            try (InputStream inputStream = new FileInputStream(new File(path))) {
                int num = RequestMessageParser.countRequestLineAndHeaderLength(inputStream);

                //ヘッダーフィールドとメッセージボディの間に入る改行文字の分の4をプラスする
                num += 4;

                //メッセージボディの分の123をプラスする
                num += 123;

                assertThat(num, is((int) new File(path).length() + 2));
            }
        }
    }

    public static class readMessageBodyメソッドのテスト {
        @Test(expected = NullPointerException.class)
        public void nullチェック() throws Exception {
            RequestMessageParser.readMessageBody(null, 0, 0);
        }

        @Test
        public void 正しく動作するか() throws Exception {
            String path = "./src/test/resources/request/PostRequestMessage.txt";
            try (FileInputStream fis = new FileInputStream(new File(path))) {
                RequestMessage requestMessage = RequestMessageParser.parse(fis);
                Map<String, String> messageBody = requestMessage.parseMessageBodyToMap();

                assertThat(messageBody.get("name"), is("asada"));
                assertThat(messageBody.get("title"), is("test"));
                assertThat(messageBody.get("text"), is("こんにちは"));
                assertThat(messageBody.get("password"), is("test"));
                assertThat(messageBody.get("param"), is("contribution"));
            }
        }
    }
}
