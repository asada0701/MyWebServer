package jp.co.topgate.asada.web;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by yusuke-pc on 2017/04/13.
 */
@RunWith(Enclosed.class)
public class RequestMessageTest {
    public static class methodのテスト{
        RequestMessage sut;
        @Before
        public void setUp () {
            sut = new RequestMessage();
        }
        @Test
        public void nullチェック () {
            assertThat(sut.getMethod(), is(nullValue()));
        }
        @Test
        public void 正しく値を得られるか () {
            sut.setMethod("GET");
            assertThat(sut.getMethod(), is("GET"));
        }
    }

    public static class URIのテスト{
        RequestMessage sut;
        @Before
        public void setUp () {
            sut = new RequestMessage();
        }
        @Test
        public void nullチェック () {
            assertThat(sut.getUri(), is(nullValue()));
        }
        @Test
        public void 正しく値を得られるか () {
            sut.setUri("/index.html");
            assertThat(sut.getUri(), is("/index.html"));
        }
    }

    public static class URIのクエリーのテスト{
        RequestMessage sut;
        @Before
        public void setUp () {
            sut = new RequestMessage();
        }
        @Test
        public void nullチェック () {
            assertThat(sut.findUriQuery(null), is(nullValue()));
        }
        @Test
        public void 正しく値を得られるか () {
            sut.setUriQuery("person", "人");
            assertThat(sut.findUriQuery("person"), is("人"));
        }
    }

    public static class プロトコルバージョンのテスト{
        RequestMessage sut;
        @Before
        public void setUp () {
            sut = new RequestMessage();
        }
        @Test
        public void nullチェック () {
            assertThat(sut.getProtocolVersion(), is(nullValue()));
        }
        @Test
        public void 正しく値を得られるか () {
            sut.setProtocolVersion("HTTP/1.1");
            assertThat(sut.getProtocolVersion(), is("HTTP/1.1"));
        }
    }

    public static class ヘッダーフィルードのテスト{
        RequestMessage sut;
        @Before
        public void setUp () {
            sut = new RequestMessage();
        }
        @Test
        public void nullチェック () {
            assertThat(sut.findHeaderByName(null), is(nullValue()));
        }
        @Test
        public void 正しく値を得られるか () {
            sut.setHeaderFieldUri("person", "人");
            assertThat(sut.findHeaderByName("person"), is("人"));
        }
    }

    public static class メッセージボディのテスト{
        RequestMessage sut;
        @Before
        public void setUp () {
            sut = new RequestMessage();
        }
        @Test
        public void nullチェック () {
            assertThat(sut.findMessageBody(null), is(nullValue()));
        }
        @Test
        public void 正しく値を得られるか () {
            sut.setMessageBody("person", "人");
            assertThat(sut.findMessageBody("person"), is("人"));
        }
    }

    public static class パースのテスト{
        RequestMessage sut;
        @Before
        public void setUp () {
            sut = new RequestMessage();
        }
        @Test
        public void nullチェック () throws Exception{
            File file = new File("./src/test/java/jp/co/topgate/asada/web/Documents/empty.txt");
            InputStream is = new FileInputStream(file);
            assertThat(sut.parse(is), is(false));
        }
        @Test
        public void パースのテストをしようと思う () throws Exception{
            File file = new File("./src/test/java/jp/co/topgate/asada/web/Documents/requestMessage.txt");
            InputStream is = new FileInputStream(file);

            assertThat(sut.parse(is), is(true));

            //以降のテストはparseメソッド前提である

            assertThat(sut.getMethod(), is("GET"));
            assertThat(sut.getUri(),is("/index.html"));
            assertThat(sut.getProtocolVersion(),is("HTTP/1.1"));
            assertThat(sut.findHeaderByName("Host"),is("localhost:8080"));
            assertThat(sut.findUriQuery("name"),is("asada"));
            assertThat(sut.findUriQuery("like"),is("cat"));
        }
        @Test
        public void HOSTでメッセージボディに何か入れてみる() throws Exception{
            File file = new File("./src/test/java/jp/co/topgate/asada/web/Documents/HostRequestMessage.txt");
            InputStream is = new FileInputStream(file);

            assertThat(sut.parse(is), is(true));

            //以降のテストはparseメソッド前提である

            assertThat(sut.getMethod(), is("HOST"));
            assertThat(sut.getUri(), is("/index.html"));
            assertThat(sut.getProtocolVersion(), is("HTTP/1.1"));
            assertThat(sut.findHeaderByName("Host"), is("localhost:8080"));
            assertThat(sut.findMessageBody("name"), is("asada"));
            assertThat(sut.findMessageBody("like"), is("cat"));
        }
    }
}
