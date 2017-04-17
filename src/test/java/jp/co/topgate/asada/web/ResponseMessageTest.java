package jp.co.topgate.asada.web;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.io.File;

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

    public static class レスポンスメッセージの生成テスト

    @Ignore("未実装")
    @Test
    public void レスポンスメッセージクラス全体の振る舞い () {
        sut.setProtocolVersion("HTTP/1.1");
        sut.setStatusCode("200");
        sut.setReasonPhrase("OK");
        sut.addHeader("Date", "Thu,13 Api 2017 18:33:23 GMT");
        sut.addHeader("Server", "mywebserver/1.0");
        sut.addHeader("Content-Type", "text/html");

        //rm.setMessageBody("/index.html");
    }
}
