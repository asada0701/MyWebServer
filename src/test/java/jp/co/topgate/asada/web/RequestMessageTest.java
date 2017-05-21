package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.app.RequestMessageBody;
import jp.co.topgate.asada.web.exception.RequestParseException;
import org.junit.Before;
import org.junit.Ignore;
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
 * RequestMessageクラスのテスト
 *
 * @author asada
 */
@RunWith(Enclosed.class)
public class RequestMessageTest {
    public static class コンストラクタのテスト {
        RequestMessage sut;

        @Test(expected = RequestParseException.class)
        public void nullチェック() throws Exception {
            sut = new RequestMessage(null);
        }

        @Test(expected = RequestParseException.class)
        public void 空チェック() throws Exception {
            String path = "./src/test/resources/emptyRequestMessage.txt";
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(path)))) {
                sut = new RequestMessage(bis);
            }
        }

        @Test
        public void GETの場合正しく動作するか() throws Exception {
            String path = "./src/test/resources/GetRequestMessage.txt";
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(path)))) {
                sut = new RequestMessage(bis);
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
                sut = new RequestMessage(fis);
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

                RequestMessageBody rmb = sut.getMessageBody();
                assertThat(new String(rmb.getMessageBody()), is("name%3dasada%26title%3dtest%26text%3d%e3%81%93%e3%82%93%e3%81%ab%e3%81%a1%e3%81%af%26password%3dtest%26param%3dcontribution"));
            }
        }
    }

    public static class readRequestLineAndHaderFieldメソッドのテスト {

        @Test(expected = NullPointerException.class)
        public void nullチェック() throws Exception {
            RequestMessage.readRequestLineAndHeaderField(null);
        }

        @Test
        public void 正しく動作するか() throws Exception {
            String path = "./src/test/resources/GetRequestMessage.txt";
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(path)))) {

                String[] sut = RequestMessage.readRequestLineAndHeaderField(bis);
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
            RequestMessage.readMessageBody(null, 0);
        }
    }

    public static class splitUriメソッドのテスト {
        @Test(expected = NullPointerException.class)
        public void nullチェック() {
            RequestMessage.splitUri(null);
        }

        @Test
        public void 空チェック() throws Exception {
            String[] sut = RequestMessage.splitUri("");
            assertThat(sut.length, is(2));
            assertThat(sut[0], is(""));
            assertThat(sut[1], is(nullValue()));
        }

        @Test(expected = RequestParseException.class)
        public void URIシンタックス例外のテスト() throws Exception {
            RequestMessage.splitUri("{hello world!}");
        }

        @Test
        public void 正しく動作するか() throws Exception {
            String[] sut = RequestMessage.splitUri("/index.html?name=%e6%9c%9d%e7%94%b0&like=cat");
            assertThat(sut.length, is(2));
            assertThat(sut[0], is("/index.html"));
            assertThat(sut[1], is("name=朝田&like=cat"));
        }
    }

    public static class uriQueryParseメソッドのテスト {

        @Test(expected = NullPointerException.class)
        public void nullチェック() throws Exception {
            RequestMessage.uriQueryParse(null);
        }

        @Test(expected = RequestParseException.class)
        public void 空チェック() throws Exception {
            RequestMessage.uriQueryParse("");
        }

        @Test
        public void 正しく動作するか() throws Exception {
            Map<String, String> sut = RequestMessage.uriQueryParse("name=朝田&like=cat");
            assertThat(sut.get("name"), is("朝田"));
            assertThat(sut.get("like"), is("cat"));
        }
    }

    public static class headerFieldParseメソッドのテスト {

        @Test(expected = NullPointerException.class)
        public void nullチッェク() throws Exception {
            RequestMessage.headerFieldParse(null);
        }

        @Test(expected = RequestParseException.class)
        public void 空チェック() throws Exception {
            Map<String, String> sut = RequestMessage.headerFieldParse("");
            assertThat(sut.size(), is(1));
        }

        @Test
        public void 正しく動作するか() throws Exception {
            Map<String, String> sut = RequestMessage.headerFieldParse(
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
