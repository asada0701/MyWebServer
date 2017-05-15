package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.exception.RequestParseException;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by yusuke-pc on 2017/04/13.
 */
@RunWith(Enclosed.class)
public class RequestMessageTest {
    public static class コンストラクタのテスト {
        RequestMessage sut;

        @Test(expected = IllegalArgumentException.class)
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
            }
        }

        @Test
        public void POSTの場合正しく動作するか() throws Exception {
            String path = "./src/test/resources/PostRequestMessage.txt";
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(path)))) {
                sut = new RequestMessage(bis);
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

                assertThat(sut.findMessageBody("name"), is("asada"));
                assertThat(sut.findMessageBody("title"), is("test"));
                assertThat(sut.findMessageBody("text"), is("こんにちは"));
                assertThat(sut.findMessageBody("password"), is("test"));
                assertThat(sut.findMessageBody("param"), is("contribution"));
                assertThat(sut.findMessageBody("like"), is(nullValue()));
            }
        }
    }

    public static class ヘッダーフィルードのテスト {
        RequestMessage sut;

        @Before
        public void setUp() throws Exception {
            String path = "./src/test/resources/GetRequestMessage.txt";
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(path)))) {
                sut = new RequestMessage(bis);
            }
        }

        @Test
        public void nullチェック() throws Exception {
            assertThat(sut.findHeaderByName(null), is(nullValue()));
        }

        @Test
        public void ないものを要求してみる() throws Exception {
            assertThat(sut.findHeaderByName("email"), is(nullValue()));
        }
    }

    public static class メッセージボディのテスト {
        RequestMessage sut;

        @Before
        public void setUp() throws Exception {
            String path = "./src/test/resources/PostRequestMessage.txt";
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(path)))) {
                sut = new RequestMessage(bis);
            }
        }

        @Test
        public void nullチェック() throws Exception {
            assertThat(sut.findMessageBody(null), is(nullValue()));
        }

        @Test
        public void ないものを要求してみる() throws Exception {
            assertThat(sut.findMessageBody("email"), is(nullValue()));
        }
    }
}
