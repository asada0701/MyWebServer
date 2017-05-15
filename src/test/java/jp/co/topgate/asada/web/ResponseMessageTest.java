package jp.co.topgate.asada.web;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.io.*;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by yusuke-pc on 2017/04/13.
 */
@RunWith(Enclosed.class)
public class ResponseMessageTest {

    public static class プロトコルバージョンのテスト {
        ResponseMessage sut;

        @Before
        public void setUp() throws Exception {
            File file = new File("./src/test/resources/responseMessage.txt");
            FileOutputStream fos = new FileOutputStream(file);
            sut = new ResponseMessage(fos, StatusLine.OK, "./src/main/resources/index.html");
        }

        @Test
        public void 初期設定の確認() {
            assertThat(sut.getProtocolVersion(), is("HTTP/1.1"));
        }

        @Test
        public void nullチェック() {
            sut.setProtocolVersion(null);
            assertThat(sut.getProtocolVersion(), is("HTTP/1.1"));
        }

        @Test
        public void プロトコルバージョンを設定してみる() throws Exception {
            File file = new File("./src/test/resources/responseMessage.txt");

            FileOutputStream fos = new FileOutputStream(file);
            ResponseMessage sut2 = new ResponseMessage(fos, StatusLine.OK, "./src/main/resources/index.html");
            sut2.setProtocolVersion("HTTP/2");
            assertThat(sut2.getProtocolVersion(), is("HTTP/2"));
        }
    }

    public static class ヘッダーフィールドのテスト {
        ResponseMessage sut;

        @Before
        public void setUp() throws Exception {
            File file = new File("./src/test/resources/responseMessage.txt");

            FileOutputStream fos = new FileOutputStream(file);
            sut = new ResponseMessage(fos, StatusLine.OK, "./src/main/resources/index.html");
        }

        @Test
        public void nullチェック() {
            sut.addHeader(null, null);
            sut.addHeader("Date", null);
            sut.addHeader(null, "Thu,13 Api 2017 18:33:23 GMT");
            assertThat(sut.getHeaderField().size(), is(1));
        }

        @Test
        public void ヘッダーフィールドに追加してみる() {
            sut.addHeader("Date", "Thu,13 Api 2017 18:33:23 GMT");
            sut.addHeader("Server", "mywebserver/1.0");
            assertThat(sut.getHeaderField().get(1), is("Date: Thu,13 Api 2017 18:33:23 GMT"));
            assertThat(sut.getHeaderField().get(2), is("Server: mywebserver/1.0"));
        }
    }

    public static class returnResponseメソッドのテスト {
        @Test
        public void レスポンスメッセージの生成テスト() throws Exception {
            String path = "./src/test/resources/responseMessage.txt";
            try (FileOutputStream fos = new FileOutputStream(path);
                 BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path))))) {
                new ResponseMessage(fos, StatusLine.OK, "./src/main/resources/index.html");

                assertThat(br.readLine(), is("HTTP/1.1 200 OK"));
                assertThat(br.readLine(), is("Content-Type: text/html; charset=UTF-8"));
                assertThat(br.readLine(), is(""));
                assertThat(br.readLine(), is("<!DOCTYPE html>"));
                assertThat(br.readLine(), is("<html>"));
                assertThat(br.readLine(), is("<head>"));
            }
        }
    }

    public static class returnErrorResponseメソッドのテスト {
        @Test
        public void バッドリクエストのテスト() throws Exception {
            String path = "./src/test/resources/responseMessage.txt";
            try (FileOutputStream fos = new FileOutputStream(path);
                 BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path))))) {
                new ResponseMessage(fos, StatusLine.BAD_REQUEST, "");

                assertThat(br.readLine(), is("HTTP/1.1 400 Bad Request"));
                assertThat(br.readLine(), is("Content-Type: text/html; charset=UTF-8"));
                assertThat(br.readLine(), is(""));
                assertThat(br.readLine(), is("<html><head><title>400 Bad Request</title></head>" +
                        "<body><h1>Bad Request</h1>" +
                        "<p>Your browser sent a request that this server could not understand.<br /></p></body></html>"));
            }
        }
    }
}
