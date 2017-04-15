package jp.co.topgate.asada.web;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by yusuke-pc on 2017/04/13.
 */
public class RequestMessageTest {
    @Test
    public void パースのテストをしようと思う () throws Exception{
        RequestMessage rm = new RequestMessage();

        assertNull(rm.getMethod());
        assertNull(rm.getUri());
        assertNull(rm.getProtocolVersion());

        File file = new File("./src/test/java/jp/co/topgate/asada/web/requestMessage.txt");
        InputStream is = new FileInputStream(file);

        assertTrue("リクエストメッセージのエラーです", rm.parse(is));

        //以降のテストはparseメソッド前提である

        assertThat("GET", is(rm.getMethod()));
        assertThat("/index.html",is(rm.getUri()));
        assertThat("HTTP/1.1",is(rm.getProtocolVersion()));
        assertThat("localhost:8080",is(rm.findHeaderByName("Host")));
        assertThat("asada",is(rm.findUriQuery("name")));
        assertThat("cat",is(rm.findUriQuery("like")));
    }
    @Test
    public void HOSTでメッセージボディに何か入れてみる() throws Exception{
        RequestMessage rm = new RequestMessage();

        assertNull(rm.getMethod());
        assertNull(rm.getUri());
        assertNull(rm.getProtocolVersion());

        File file = new File("./src/test/java/jp/co/topgate/asada/web/HostRequestMessage.txt");
        InputStream is = new FileInputStream(file);

        assertTrue("リクエストメッセージのエラーです", rm.parse(is));

        //以降のテストはparseメソッド前提である

        assertThat("HOST", is(rm.getMethod()));
        assertThat("/index.html",is(rm.getUri()));
        assertThat("HTTP/1.1",is(rm.getProtocolVersion()));
        assertThat("localhost:8080",is(rm.findHeaderByName("Host")));
        assertThat("asada",is(rm.findMessageBody("name")));
        assertThat("cat",is(rm.findMessageBody("like")));
    }
}
