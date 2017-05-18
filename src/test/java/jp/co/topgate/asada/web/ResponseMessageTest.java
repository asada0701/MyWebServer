package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.app.StatusLine;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by yusuke-pc on 2017/04/13.
 */
@RunWith(Enclosed.class)
public class ResponseMessageTest {

    public static class getResponseLineメソッドのテスト {
        @Test(expected = NullPointerException.class)
        public void 引数StatusLineのnullチェック() {
            ResponseMessage.getResponseLine("HTTP/1.1", null);
        }

        @Test
        public void 引数protocolVersionのnullチェック() {
            ResponseMessage.getResponseLine(null, StatusLine.OK);
        }

        @Test(expected = NullPointerException.class)
        public void 引数両方のnullチェック() {
            ResponseMessage.getResponseLine(null, null);
        }

        @Test
        public void ステータスコード200のテスト() {
            String responseLine = ResponseMessage.getResponseLine("HTTP/2", StatusLine.OK);
            assertThat(responseLine, is("HTTP/2 200 OK\n"));
        }

        @Test
        public void ステータスコード400のテスト() {
            String responseLine = ResponseMessage.getResponseLine("HTTP/1.1", StatusLine.BAD_REQUEST);
            assertThat(responseLine, is("HTTP/1.1 400 Bad Request\n"));
        }

        @Test
        public void ステータスコード404のテスト() {
            String responseLine = ResponseMessage.getResponseLine("HTTP/1.1", StatusLine.NOT_FOUND);
            assertThat(responseLine, is("HTTP/1.1 404 Not Found\n"));
        }

        @Test
        public void ステータスコード500のテスト() {
            String responseLine = ResponseMessage.getResponseLine("HTTP/1.1", StatusLine.INTERNAL_SERVER_ERROR);
            assertThat(responseLine, is("HTTP/1.1 500 Internal Server Error\n"));
        }

        @Test
        public void ステータスコード501のテスト() {
            String responseLine = ResponseMessage.getResponseLine("HTTP/1.1", StatusLine.NOT_IMPLEMENTED);
            assertThat(responseLine, is("HTTP/1.1 501 Not Implemented\n"));
        }

        @Test
        public void ステータスコード505のテスト() {
            String responseLine = ResponseMessage.getResponseLine("HTTP/1.1", StatusLine.HTTP_VERSION_NOT_SUPPORTED);
            assertThat(responseLine, is("HTTP/1.1 505 HTTP Version Not Supported\n"));
        }
    }

    public static class getHeaderメソッドのテスト {
        @Test(expected = NullPointerException.class)
        public void nullチェック() {
            ResponseMessage.getHeader(null);
        }

        @Test
        public void 空チェック() {
            List<String> list = new ArrayList<>();
            String sut = ResponseMessage.getHeader(list);
            assertThat(sut, is("\n"));
        }

        @Test
        public void 正しく動作するか() {
            List<String> list = new ArrayList<>();
            list.add("Connection: Keep-Alive");
            list.add("Content-Type: text/html; charset=UTF-8");

            String sut = ResponseMessage.getHeader(list);
            assertThat(sut, is("Connection: Keep-Alive\nContent-Type: text/html; charset=UTF-8\n\n"));
        }
    }

    public static class getErrorMessageBodyメソッドのテスト {
        @Test
        public void nullチェック() {
            String str = ResponseMessage.getErrorMessageBody(null);
            assertThat(str, is("<html><head><title>500 Internal Server Error</title></head>" +
                    "<body><h1>Internal Server Error</h1>" +
                    "<p>サーバー内部のエラーにより表示できません。ごめんなさい。</p></body></html>"));
        }

        @Test
        public void BadRequest() {
            String str = ResponseMessage.getErrorMessageBody(StatusLine.BAD_REQUEST);
            assertThat(str, is("<html><head><title>400 Bad Request</title></head>" +
                    "<body><h1>Bad Request</h1>" +
                    "<p>Your browser sent a request that this server could not understand.<br /></p></body></html>"));
        }

        @Test
        public void NotFound() {
            String str = ResponseMessage.getErrorMessageBody(StatusLine.NOT_FOUND);
            assertThat(str, is("<html><head><title>404 Not Found</title></head>" +
                    "<body><h1>Not Found</h1>" +
                    "<p>お探しのページは見つかりませんでした。</p></body></html>"));
        }

        @Test
        public void InternalServerError() {
            String str = ResponseMessage.getErrorMessageBody(StatusLine.INTERNAL_SERVER_ERROR);
            assertThat(str, is("<html><head><title>500 Internal Server Error</title></head>" +
                    "<body><h1>Internal Server Error</h1>" +
                    "<p>サーバー内部のエラーにより表示できません。ごめんなさい。</p></body></html>"));
        }

        @Test
        public void NotImplemented() {
            String str = ResponseMessage.getErrorMessageBody(StatusLine.NOT_IMPLEMENTED);
            assertThat(str, is("<html><head><title>501 Not Implemented</title></head>" +
                    "<body><h1>Not Implemented</h1>" +
                    "<p>Webサーバーでメソッドが実装されていません。</p></body></html>"));
        }

        @Test
        public void HTTPVersionNotSupported() {
            String str = ResponseMessage.getErrorMessageBody(StatusLine.HTTP_VERSION_NOT_SUPPORTED);
            assertThat(str, is("<html><head><title>505 HTTP Version Not Supported</title></head>" +
                    "<body><h1>HTTP Version Not Supported</h1></body></html>"));
        }
    }

    public static class プロトコルバージョンのテスト {
        ResponseMessage sut;

        @Before
        public void setUp() throws Exception {
            File file = new File("./src/test/resources/responseMessage.txt");
            FileOutputStream fos = new FileOutputStream(file);
            sut = new ResponseMessage(fos, StatusLine.OK);
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
            ResponseMessage sut2 = new ResponseMessage(fos, StatusLine.OK);
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
            sut = new ResponseMessage(fos, StatusLine.OK);
        }

        @Test
        public void nullチェック() {
            sut.addHeader(null, null);
            sut.addHeader("Date", null);
            sut.addHeader(null, "Thu,13 Api 2017 18:33:23 GMT");
            assertThat(sut.getHeaderField().size(), is(0));
        }

        @Test
        public void ヘッダーフィールドに追加してみる() {
            sut.addHeader("Date", "Thu,13 Api 2017 18:33:23 GMT");
            sut.addHeader("Server", "mywebserver/1.0");
            assertThat(sut.getHeaderField().get(0), is("Date: Thu,13 Api 2017 18:33:23 GMT"));
            assertThat(sut.getHeaderField().get(1), is("Server: mywebserver/1.0"));
        }
    }

    public static class コンストラクタのテスト {
        @Test
        public void 引数outputStreamのnullチェック() throws Exception {
            new ResponseMessage(null, StatusLine.OK);
        }

        @Test
        public void 引数StatusLineのnullチェック() throws Exception {
            String path = "./src/test/resources/responseMessage.txt";
            try (FileOutputStream fos = new FileOutputStream(path)) {
                new ResponseMessage(fos, null);
            }
        }

        @Test
        public void 引数ファイルパスのnullチェック() throws Exception {
            String path = "./src/test/resources/responseMessage.txt";
            try (FileOutputStream fos = new FileOutputStream(path)) {
                new ResponseMessage(fos, StatusLine.OK, null);
            }
        }

        @Test
        public void 引数3つのコンストラクタのテスト() throws Exception {
            String path = "./src/test/resources/responseMessage.txt";
            try (FileOutputStream fos = new FileOutputStream(path)) {
                ResponseMessage sut = new ResponseMessage(fos, StatusLine.OK, "");
                assertThat(sut.getProtocolVersion(), is("HTTP/1.1"));
                assertThat(sut.getStatusLine(), is(StatusLine.OK));
            }
        }

        @Test
        public void 引数2つのコンストラクタのテスト() throws Exception {
            String path = "./src/test/resources/responseMessage.txt";
            try (FileOutputStream fos = new FileOutputStream(path)) {
                ResponseMessage sut = new ResponseMessage(fos, StatusLine.OK);
                assertThat(sut.getProtocolVersion(), is("HTTP/1.1"));
                assertThat(sut.getStatusLine(), is(StatusLine.OK));
            }
        }
    }

    public static class returnResponseメソッドのテスト {
        @Test
        public void レスポンスメッセージの生成テスト() throws Exception {
            String path = "./src/test/resources/responseMessage.txt";
            try (FileOutputStream fos = new FileOutputStream(path);
                 BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path))))) {
                ResponseMessage rm = new ResponseMessage(fos, StatusLine.OK, "./src/main/resources/index.html");
                rm.addHeader("Content-Type", "text/html; charset=UTF-8");
                assertThat(rm.returnResponse(), is(true));

                assertThat(br.readLine(), is("HTTP/1.1 200 OK"));
                assertThat(br.readLine(), is("Content-Type: text/html; charset=UTF-8"));
                assertThat(br.readLine(), is(""));
                assertThat(br.readLine(), is("<!DOCTYPE html>"));
                assertThat(br.readLine(), is("<html>"));
                assertThat(br.readLine(), is("<head>"));
                assertThat(br.readLine(), is("    <meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">"));
                assertThat(br.readLine(), is("    <link rel=\"stylesheet\" type=\"text/css\" href=\"./css/style.css\">"));
                assertThat(br.readLine(), is("    <script type=\"text/javascript\" src=\"./js/myjs.js\"></script>"));
                assertThat(br.readLine(), is("</head>"));
                assertThat(br.readLine(), is("<body>"));
                assertThat(br.readLine(), is("<center>"));
                assertThat(br.readLine(), is("    <div id=\"header\">"));
                assertThat(br.readLine(), is("        <h1>こんにちは</h1>"));
                assertThat(br.readLine(), is("        <p>"));
                assertThat(br.readLine(), is("            <script>"));
                assertThat(br.readLine(), is("            koshin();"));
                assertThat(br.readLine(), is("            </script>"));
                assertThat(br.readLine(), is("        </p>"));
                assertThat(br.readLine(), is("    </div>"));
                assertThat(br.readLine(), is("    <div id=\"gazou\">"));
                assertThat(br.readLine(), is("        <p>"));
                assertThat(br.readLine(), is("            <img src=\"./img/s_1.jpg\" width=\"200\" height=\"180\" alt=\"猫\"/>"));
                assertThat(br.readLine(), is("            <img src=\"./img/loading-loop.gif\" width=\"200\" height=\"180\"/>"));
                assertThat(br.readLine(), is("            <img src=\"./img/s_pet_neko.png\" width=\"200\" height=\"180\"/>"));
                assertThat(br.readLine(), is("        </p>"));
                assertThat(br.readLine(), is("    </div>"));
                assertThat(br.readLine(), is("</center>"));
                assertThat(br.readLine(), is("</body>"));
                assertThat(br.readLine(), is("</html>"));
                assertThat(br.readLine(), is(nullValue()));
            }
        }

        @Test
        public void バッドリクエストのテスト() throws Exception {
            String path = "./src/test/resources/responseMessage.txt";
            try (FileOutputStream fos = new FileOutputStream(path);
                 BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path))))) {
                ResponseMessage rm = new ResponseMessage(fos, StatusLine.BAD_REQUEST);
                rm.addHeader("Content-Type", "text/html; charset=UTF-8");
                assertThat(rm.returnResponse(), is(true));

                assertThat(br.readLine(), is("HTTP/1.1 400 Bad Request"));
                assertThat(br.readLine(), is("Content-Type: text/html; charset=UTF-8"));
                assertThat(br.readLine(), is(""));
                assertThat(br.readLine(), is("<html><head><title>400 Bad Request</title></head>" +
                        "<body><h1>Bad Request</h1>" +
                        "<p>Your browser sent a request that this server could not understand.<br /></p></body></html>"));
            }
        }

        @Test
        public void レスポンスに失敗してみる() throws Exception {
            String path = "./src/test/resources/responseMessage.txt";
            try (FileOutputStream fos = new FileOutputStream(path);
                 BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path))))) {
                ResponseMessage rm = new ResponseMessage(fos, StatusLine.OK);
                rm.addHeader("Content-Type", "text/html; charset=UTF-8");
                assertThat(rm.returnResponse(), is(false));
            }
        }

        @Test
        public void JSONを送ってみる() throws Exception {
            String path = "./src/test/resources/responseMessage.txt";
            try (FileOutputStream fos = new FileOutputStream(path);
                 BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path))))) {
                ResponseMessage rm = new ResponseMessage(fos, StatusLine.OK);
                rm.addHeader("Content-Type", "application/json; charset=utf-8");
                rm.addHeader("Content-Length", "43");
                assertThat(rm.returnResponse("{\"status\":\"OK\",\"message\":\"Hello Guillaume\"}".getBytes()), is(true));

                assertThat(br.readLine(), is("HTTP/1.1 200 OK"));
                assertThat(br.readLine(), is("Content-Type: application/json; charset=utf-8"));
                assertThat(br.readLine(), is("Content-Length: 43"));
                assertThat(br.readLine(), is(""));
                assertThat(br.readLine(), is("{\"status\":\"OK\",\"message\":\"Hello Guillaume\"}"));
                assertThat(br.readLine(), is(nullValue()));
            }
        }
    }
}
