package jp.co.topgate.asada.web;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * RequestMessageクラスのテスト
 *
 * @author asada
 */
public class RequestMessageTest {
    @Test
    public void コンストラクタのテスト() {
        String method = "GET";
        String uri = "/";
        String protocolVersion = "HTTP/1.1";
        Map<String, String> headerField = new HashMap<>();
        headerField.put("hoge", "hogehoge");

        RequestMessage sut = new RequestMessage(method, uri, protocolVersion, headerField);

        assertThat(sut.getMethod(), is("GET"));
        assertThat(sut.getUri(), is("/"));
        assertThat(sut.getProtocolVersion(), is("HTTP/1.1"));
        assertThat(sut.findHeaderByName("hoge"), is("hogehoge"));
    }

    @Test
    public void getMessageBodyメソッドのテスト() {
        String s = "name%3dasada%26title%3dtest%26text%3d%e3%81%93%e3%82%93%e3%81%ab%e3%81%a1%e3%81%af%26password%3dtest%26param%3dcontribution";

        String method = "GET";
        String uri = "/";
        String protocolVersion = "HTTP/1.1";
        Map<String, String> headerField = new HashMap<>();
        headerField.put("Content-Type", "application/x-www-form-urlencoded");
        RequestMessage sut = new RequestMessage(method, uri, protocolVersion, headerField);
        sut.setMessageBody(s.getBytes());

        assertThat(sut.findHeaderByName("Content-Type"), is("application/x-www-form-urlencoded"));

        assertThat(sut.getMessageBody(), is("name%3dasada%26title%3dtest%26text%3d%e3%81%93%e3%82%93%e3%81%ab%e3%81%a1%e3%81%af%26password%3dtest%26param%3dcontribution".getBytes()));
    }

    @Test
    public void getMessageBodyToMapStringメソッドのテスト() {
        String s = "name%3dasada%26title%3dtest%26text%3d%e3%81%93%e3%82%93%e3%81%ab%e3%81%a1%e3%81%af%26password%3dtest%26param%3dcontribution";

        String method = "GET";
        String uri = "/";
        String protocolVersion = "HTTP/1.1";
        Map<String, String> headerField = new HashMap<>();
        headerField.put("Content-Type", "application/x-www-form-urlencoded");
        RequestMessage sut = new RequestMessage(method, uri, protocolVersion, headerField);
        sut.setMessageBody(s.getBytes());

        assertThat(sut.findHeaderByName("Content-Type"), is("application/x-www-form-urlencoded"));

        Map<String, String> messageBody = sut.getMessageBodyToMapString();
        assertThat(messageBody.get("name"), is("asada"));
        assertThat(messageBody.get("title"), is("test"));
        assertThat(messageBody.get("text"), is("こんにちは"));
        assertThat(messageBody.get("password"), is("test"));
        assertThat(messageBody.get("param"), is("contribution"));
    }

    @Test
    public void uriQueryのテスト() {
        Map<String, String> uriQuery = new HashMap<>();
        uriQuery.put("name", "asada");
        uriQuery.put("from", "japan");

        String method = "GET";
        String uri = "/";
        String protocolVersion = "HTTP/1.1";
        Map<String, String> headerField = new HashMap<>();
        headerField.put("Content-Type", "application/x-www-form-urlencoded");
        RequestMessage sut = new RequestMessage(method, uri, protocolVersion, headerField);

        sut.setUriQuery(uriQuery);

        assertThat(sut.findUriQuery("name"), is("asada"));
        assertThat(sut.findUriQuery("from"), is("japan"));
        assertThat(sut.findUriQuery("hoge"), is(nullValue()));
    }
}
