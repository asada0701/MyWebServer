package jp.co.topgate.asada.web;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.io.*;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by yusuke-pc on 2017/04/13.
 */
@RunWith(Enclosed.class)
public class ResponseMessageTest {
    static ResponseMessage sut = null;

    public static class プロトコルバージョンのテスト {
        @Before
        public void setUp () {
            sut = new ResponseMessage();
        }
        @Test
        public void nullチェック () {
            sut.setProtocolVersion(null);
            assertThat(sut.getProtocolVersion(), is(nullValue()));
        }
        @Test
        public void 正しくセットできているか () {
            sut.setProtocolVersion("HTTP/1.1");
            assertThat("HTTP/1.1", is(sut.getProtocolVersion()));
        }
    }

    public static class ステータスコードのテスト {
        @Before
        public void setUp () {
            sut = new ResponseMessage();
        }
        @Test
        public void nullチェック () {
            sut.setStatusCode(null);
            assertThat(sut.getStatusCode(), is(nullValue()));
        }
        @Test
        public void 正しくセットできているか () {
            sut.setStatusCode("200");
            assertThat(sut.getStatusCode(), is("200"));
        }
    }

    public static class 理由フレーズのテスト {
        @Before
        public void setUp () {
            sut = new ResponseMessage();
        }
        @Test
        public void nullチェック () {
            sut.setReasonPhrase(null);
            assertThat(sut.getReasonPhrase(), is(nullValue()));
        }
        @Test
        public void 正しくセットできているか () {
            sut.setReasonPhrase("OK");
            assertThat(sut.getReasonPhrase(), is("OK"));
        }
    }

    public static class ヘッダーボディのテスト {
        @Before
        public void setUp () {
            sut = new ResponseMessage();
        }
        @Test
        public void 両方null () {
            sut.addHeader(null,null);
            assertThat(sut.getHeaderField().size(), is(0));
        }
        @Test
        public void nameをnull () {
            sut.addHeader(null,"value");
            assertThat(sut.getHeaderField().size(), is(0));
        }
        @Test
        public void valueをnull () {
            sut.addHeader("name",null);
            assertThat(sut.getHeaderField().size(), is(0));
        }
        @Test
        public void 正しくセットできているか (){
            sut.addHeader("Date", "Thu,13 Api 2017 18:33:23 GMT");
            sut.addHeader("Server", "mywebserver/1.0");
            assertThat(sut.getHeaderField().get(0), is("Date: Thu,13 Api 2017 18:33:23 GMT"));
            assertThat(sut.getHeaderField().get(1), is("Server: mywebserver/1.0"));
        }
    }

    public static class メッセージボディのテスト {
        @Before
        public void setUp () {
            sut = new ResponseMessage();
        }
        @Test
        public void nullチェック () {
            sut.setMessageBody(null);
            assertThat(sut.getMessageBody(), is(nullValue()));
        }
        @Test
        public void 存在しないファイルを渡してみるnullのままのはず () {
            sut.setMessageBody(new File("./Documents/notExists"));
            assertThat(sut.getMessageBody(), is(nullValue()));
        }
        @Test
        public void ディレクトリのパスを渡してみるnullのままのはず () {
            sut.setMessageBody(new File("./src/test/java/jp/co/topgate/asada/web/Documents"));
            assertThat(sut.getMessageBody(), is(nullValue()));
        }
        @Test
        public void 正しくセットできているか () {
            sut.setMessageBody(
                    new File("./src/test/java/jp/co/topgate/asada/web/Documents/requestMessage.txt"));
            assertThat(sut.getMessageBody(), is(notNullValue()));
        }
    }

    public static class レスポンスメッセージの生成テスト {
        @Before
        public void setUp () {
            sut = new ResponseMessage();
            File file = new File("./src/test/java/jp/co/topgate/asada/web/Documents/responseMessage.txt");
            if(file.exists()){
                file.delete();
            }
        }
        @Test
        public void 正しく生成できるか () throws Exception{
            File file = new File("./src/test/java/jp/co/topgate/asada/web/Documents/responseMessage.txt");
            FileOutputStream fos = new FileOutputStream(file);
            sut.setProtocolVersion("HTTP/1.1");
            sut.setStatusCode("200");
            sut.setReasonPhrase("OK");
            sut.addHeader("Date", "Thu,13 Api 2017 18:33:23 GMT");
            sut.addHeader("Server", "mywebserver/1.0");
            sut.addHeader("Content-Type", "text/html");
            sut.setMessageBody(new File("./src/main/java/jp/co/topgate/asada/web/Documents/index.html"));
            sut.returnResponseChar(fos);
            fos.close();

            InputStream is = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            assertThat(br.readLine(), is("HTTP/1.1 200 OK"));
            assertThat(br.readLine(), is("Date: Thu,13 Api 2017 18:33:23 GMT"));
            assertThat(br.readLine(), is("Server: mywebserver/1.0"));
            assertThat(br.readLine(), is("Content-Type: text/html"));
            assertThat(br.readLine(), is(""));
            assertThat(br.readLine(), is("<!DOCTYPE html>" +
                    "<html>" +
                    "<head>" +
                    "  <meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">" +
                    "  <link rel=\"stylesheet\" type=\"text/css\" href=\"./css/style.css\">" +
                    "  <script type=\"text/javascript\" src=\"./js/myjs.js\"></script>" +
                    "</head>" +
                    "<body>" +
                    "  <div id=\"header\">" +
                    "    <h1>こんにちは</h1>" +
                    "    <p>" +
                    "      <script>" +
                    "        koshin();" +
                    "      </script>" +
                    "    </p>" +
                    "  </div>" +
                    "  <div id=\"gazou\">" +
                    "    <p>私の好きな猫の画像です<img src=\"./img/1.jpg\" width=\"400\" height=\"360\" alt=\"猫\" /></p>" +
                    "  </div>" +
                    "  <!--<div id=\"douga\">-->" +
                    "    <!--<p>こちらは好きな動画になります。-->" +
                    "    <!--<video src=\"./video/cat.mp3\" controls>-->" +
                    "    <!--</video>-->" +
                    "  <!--</div>-->" +
                    "</body>" +
                    "</html>"));
        }
    }
}
