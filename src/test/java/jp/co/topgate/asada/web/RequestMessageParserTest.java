package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.exception.RequestParseException;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
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

        @Test
        public void nullチェック() throws Exception {
            sut = new RequestMessage(null, null, null);
            assertThat(sut.getMethod(), is(nullValue()));
            assertThat(sut.getUri(), is(nullValue()));
            assertThat(sut.getProtocolVersion(), is(nullValue()));
        }

        @Test(expected = RequestParseException.class)
        public void 空チェック() throws Exception {
            String path = "./src/test/resources/emptyRequestMessage.txt";
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(path)))) {
                sut = RequestMessageParser.parse(bis);
            }
        }

        @Test
        public void GETの場合正しく動作するか() throws Exception {
            String path = "./src/test/resources/GetRequestMessage.txt";
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(path)))) {
                sut = RequestMessageParser.parse(bis);
                assertThat(sut.getMethod(), is("GET"));
                assertThat(sut.getUri(), is("/index.html"));
                assertThat(sut.getProtocolVersion(), is("HTTP/1.1"));

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
        }

        @Test
        public void POSTの場合正しく動作するか() throws Exception {
            String path = "./src/test/resources/PostRequestMessage.txt";
            try (FileInputStream fis = new FileInputStream(new File(path))) {
                sut = RequestMessageParser.parse(fis);
                assertThat(sut.getMethod(), is("POST"));
                assertThat(sut.getUri(), is("/program/board/index.html"));
                assertThat(sut.getProtocolVersion(), is("HTTP/1.1"));

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

                Map<String, String> messageBody = RequestMessageBodyParser.parseToMapString(sut.getMessageBody());
                assertThat(messageBody.get("name"), is("asada"));
                assertThat(messageBody.get("title"), is("test"));
                assertThat(messageBody.get("text"), is("こんにちは"));
                assertThat(messageBody.get("password"), is("test"));
                assertThat(messageBody.get("param"), is("contribution"));
            }
        }
    }

    public static class readRequestLineAndHeaderFieldメソッドのテスト {

        @Test(expected = NullPointerException.class)
        public void nullチェック() throws Exception {
            RequestMessageParser.readRequestLineAndHeaderField(null);
        }

        @Test
        public void 正しく動作するか() throws Exception {
            String path = "./src/test/resources/GetRequestMessage.txt";
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(path)))) {

                String[] sut = RequestMessageParser.readRequestLineAndHeaderField(bis);
                assertThat(sut.length, is(2));
                assertThat(sut[0], is("GET /index.html?name=asada&like=cat HTTP/1.1"));
                assertThat(sut[1], is("Host: localhost:8080\n" +
                        "Connection: keep-alive\n" +
                        "Pragma: no-cache\n" +
                        "Cache-Control: no-cache\n" +
                        "Upgrade-Insecure-Requests: 1\n" +
                        "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36\n" +
                        "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8\n" +
                        "Accept-Encoding: gzip, deflate, sdch, br\n" +
                        "Accept-Language: ja,en-US;q=0.8,en;q=0.6\n" +
                        "Foo: Bar:Fizz:Buzz\n"));
            }
        }
    }

    public static class readMessageBodyメソッドのテスト {
        @Test(expected = NullPointerException.class)
        public void nullチェック() throws Exception {
            RequestMessageParser.readMessageBody(null, 0);
        }

        @Test
        public void 正しく動作するか() throws Exception {
            String path = "./src/test/resources/PostRequestMessage.txt";
            try (FileInputStream fis = new FileInputStream(new File(path))) {
                RequestMessage sut = RequestMessageParser.parse(fis);

                Map<String, String> messageBody = RequestMessageBodyParser.parseToMapString(sut.getMessageBody());
                assertThat(messageBody.get("name"), is("asada"));
                assertThat(messageBody.get("title"), is("test"));
                assertThat(messageBody.get("text"), is("こんにちは"));
                assertThat(messageBody.get("password"), is("test"));
                assertThat(messageBody.get("param"), is("contribution"));
            }
        }
    }

    public static class splitUriメソッドのテスト {
        @Test(expected = NullPointerException.class)
        public void nullチェック() {
            RequestMessageParser.splitUri(null);
        }

        @Test
        public void 空チェック() throws Exception {
            String[] sut = RequestMessageParser.splitUri("");
            assertThat(sut.length, is(2));
            assertThat(sut[0], is(""));
            assertThat(sut[1], is(nullValue()));
        }

        @Test(expected = RequestParseException.class)
        public void URIシンタックス例外のテスト() throws Exception {
            RequestMessageParser.splitUri("{hello world!}");
        }

        @Test
        public void 正しく動作するか() throws Exception {
            String[] sut = RequestMessageParser.splitUri("/index.html?name=%e6%9c%9d%e7%94%b0&like=cat");
            assertThat(sut.length, is(2));
            assertThat(sut[0], is("/index.html"));
            assertThat(sut[1], is("name=朝田&like=cat"));
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

    public static class parseHeaderFieldメソッドのテスト {

        @Test(expected = NullPointerException.class)
        public void nullチッェク() throws Exception {
            RequestMessageParser.parseHeaderField(null);
        }

        @Test(expected = RequestParseException.class)
        public void 空チェック() throws Exception {
            Map<String, String> sut = RequestMessageParser.parseHeaderField("");
            assertThat(sut.size(), is(1));
        }

        @Test
        public void 正しく動作するか() throws Exception {
            Map<String, String> sut = RequestMessageParser.parseHeaderField(
                    "Host: localhost:8080\n" +
                            "Connection: keep-alive\n" +
                            "Pragma: no-cache\n" +
                            "Cache-Control: no-cache\n" +
                            "Upgrade-Insecure-Requests: 1\n" +
                            "User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36\n" +
                            "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8\n" +
                            "Accept-Encoding: gzip, deflate, sdch, br\n" +
                            "Accept-Language: ja,en-US;q=0.8,en;q=0.6\n" +
                            "Foo: Bar:Fizz:Buzz\n");
            assertThat(sut.size(), is(10));
            assertThat(sut.get("Host"), is("localhost:8080"));
            assertThat(sut.get("Connection"), is("keep-alive"));

            assertThat(sut.get("hoge"), is(nullValue()));
        }
    }
}
