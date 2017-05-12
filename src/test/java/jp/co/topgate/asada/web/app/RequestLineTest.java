package jp.co.topgate.asada.web.app;

import jp.co.topgate.asada.web.exception.RequestParseException;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by yusuke-pc on 2017/05/03.
 */
@RunWith(Enclosed.class)
public class RequestLineTest {
    public static class コンストラクタのテスト {
        @Test
        public void nullチェック() throws Exception {
            String path = "./src/test/resources/empty.txt";
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(path)))) {
                try {
                    RequestLine sut = new RequestLine(bis);
                } catch (RequestParseException e) {
                    assertThat(e.getMessage(), is("BufferedReaderのreadLineメソッドの戻り値がnullだった"));
                }
            }
        }

        @Test
        public void パースのテスト() throws IOException {
            String path = "./src/test/resources/requestMessage.txt";
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(path)))) {
                RequestLine sut = new RequestLine(bis);

                assertThat(sut.getMethod(), is("GET"));
                assertThat(sut.getUri(), is("/index.html"));
                assertThat(sut.getProtocolVersion(), is("HTTP/1.1"));
                assertThat(sut.findUriQuery("name"), is("asada"));
                assertThat(sut.findUriQuery("like"), is("cat"));
            }
        }

        @Test
        public void POSTでメッセージボディに何か入れてみる() throws Exception {
            String path = "./src/test/resources/PostRequestMessage.txt";
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(path)))) {
                RequestLine sut = new RequestLine(bis);

                assertThat(sut.getMethod(), is("POST"));
                assertThat(sut.getUri(), is("/index.html"));
                assertThat(sut.getProtocolVersion(), is("HTTP/1.1"));
            }
        }
    }

    public static class methodのテスト {
        @Test
        public void 正しく値を得られるか() throws Exception {
            String path = "./src/test/resources/requestMessage.txt";
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(path)))) {
                RequestLine sut = new RequestLine(bis);
                assertThat(sut.getMethod(), is("GET"));
            }
        }
    }

    public static class URIのテスト {
        @Test
        public void 正しく値を得られるか() throws Exception {
            String path = "./src/test/resources/requestMessage.txt";
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(path)))) {
                RequestLine sut = new RequestLine(bis);
                assertThat(sut.getUri(), is("/index.html"));
            }
        }
    }

    public static class URIのクエリーのテスト {
        RequestLine sut;

        @Before
        public void setUp() throws Exception {
            String path = "./src/test/resources/requestMessage.txt";
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(path)))) {
                sut = new RequestLine(bis);
            }
        }

        @Test
        public void nullチェック() {
            assertThat(sut.findUriQuery(null), is(nullValue()));
        }

        @Test
        public void 正しく値を得られるか() {
            assertThat(sut.findUriQuery("name"), is("asada"));
        }
    }

    public static class プロトコルバージョンのテスト {
        @Test
        public void 正しく値を得られるか() throws Exception {
            String path = "./src/test/resources/requestMessage.txt";
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(path)))) {
                RequestLine sut = new RequestLine(bis);
                assertThat(sut.getProtocolVersion(), is("HTTP/1.1"));
            }
        }
    }
}
