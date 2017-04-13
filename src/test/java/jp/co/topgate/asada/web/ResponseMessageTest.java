package jp.co.topgate.asada.web;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by yusuke-pc on 2017/04/13.
 */
public class ResponseMessageTest {
    @Test
    public void レスポンスメッセージのプロトコルバージョン () {
        ResponseMessage rm = new ResponseMessage();
        rm.setProtocolVersion("HTTP/1.1");
        assertThat("HTTP/1.1", is(rm.getProtocolVersion()));
    }
    @Test
    public void レスポンスメッセージのステータスコード () {
        ResponseMessage rm = new ResponseMessage();
        rm.setStatusCode("200");
        assertThat("200", is(rm.getStatusCode()));
    }
    @Test
    public void レスポンスメッセージの理由フレーズ () {
        ResponseMessage rm = new ResponseMessage();
        rm.setReasonPhrase("OK");
        assertThat("OK", is(rm.getReasonPhrase()));
    }
    @Test
    public void レスポンスメッセージのヘッダーボディ (){
        ResponseMessage rm = new ResponseMessage();
        rm.addHeader("Date", "Thu,13 Api 2017 18:33:23 GMT");
        rm.addHeader("Server", "mywebserver/1.0");
        assertThat("Date: Thu,13 Api 2017 18:33:23 GMT", is(rm.getHeaderField().get(0)));
        assertThat("Server: mywebserver/1.0", is(rm.getHeaderField().get(1)));
    }
    @Test
    public void レスポンスメッセージクラス全体の振る舞い () {
        ResponseMessage rm = new ResponseMessage();
        rm.setProtocolVersion("HTTP/1.1");
        rm.setStatusCode("200");
        rm.setReasonPhrase("OK");
        rm.addHeader("Date", "Thu,13 Api 2017 18:33:23 GMT");
        rm.addHeader("Server", "mywebserver/1.0");
        rm.addHeader("Content-Type", "text/html");

        rm.setMessageBody("/index.html");
    }
}
