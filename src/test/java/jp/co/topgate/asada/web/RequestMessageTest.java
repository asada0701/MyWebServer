package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.exception.RequestParseException;
import org.junit.Before;
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
    private RequestMessage sut;

    @Before
    public void setUp() {
        String method = "GET";

        String uri = "/index.html";

        Map<String, String> uriQuery = new HashMap<>();
        uriQuery.put("name", "asada");
        uriQuery.put("like", "cat");

        Map<String, String> headerField = new HashMap<>();
        headerField.put("Content-Type", "application/x-www-form-urlencoded");

        //name=asada&title=test&text=こんにちは&password=test&param=contribution
        String messageBody = "name%3dasada%26title%3dtest%26text%3d%e3%81%93%e3%82%93%e3%81%ab%e3%81%a1%e3%81%af%26password%3dtest%26param%3dcontribution";

        sut = new RequestMessage(method, uri, uriQuery, headerField, messageBody.getBytes());
    }

    @Test
    public void メソッド一覧() {
        assertThat(sut.getMethod(), is("GET"));

        assertThat(sut.getUri(), is("/index.html"));

        assertThat(sut.findUriQuery("name"), is("asada"));
        assertThat(sut.findUriQuery("like"), is("cat"));

        assertThat(sut.findHeaderByName("Content-Type"), is("application/x-www-form-urlencoded"));

        Map<String, String> messageBody = sut.parseMessageBodyToMap();
        assertThat(messageBody.get("name"), is("asada"));
        assertThat(messageBody.get("title"), is("test"));
        assertThat(messageBody.get("text"), is("こんにちは"));
        assertThat(messageBody.get("password"), is("test"));
        assertThat(messageBody.get("param"), is("contribution"));
    }

    @Test
    public void findUriQueryメソッドのテスト() {
        assertThat(sut.findUriQuery("name"), is("asada"));
        assertThat(sut.findUriQuery("like"), is("cat"));
        assertThat(sut.findUriQuery("hoge"), is(nullValue()));
        assertThat(sut.findUriQuery(null), is(nullValue()));
    }

    @Test
    public void findHeaderByNameメソッドのテスト() {
        assertThat(sut.findHeaderByName("Content-Type"), is("application/x-www-form-urlencoded"));
        assertThat(sut.findHeaderByName("nothing"), is(nullValue()));
        assertThat(sut.findHeaderByName(null), is(nullValue()));
    }

    public void setMessageBodyAndHeader(Map<String, String> headerField, byte[] messageBody) {
        sut = new RequestMessage(null, null, null, headerField, messageBody);
    }

    @Test
    public void parseMessageBodyToMapメソッドのテスト() {
        Map<String, String> headerField = new HashMap<>();
        headerField.put("Content-Type", "multipart/form-data");
        byte[] messageBody = "".getBytes();
        setMessageBodyAndHeader(headerField, messageBody);
        assertThat(sut.parseMessageBodyToMap(), is(nullValue()));

        headerField = new HashMap<>();
        headerField.put("Content-Type", "application/x-www-form-urlencoded");

        messageBody = "name%3dasada%26title%3dtest%26text%3d%e3%81%93%e3%82%93%e3%81%ab%e3%81%a1%e3%81%af%26password%3dtest%26param".getBytes();
        setMessageBodyAndHeader(headerField, messageBody);
    }

    @Test(expected = RequestParseException.class)
    public void 不正な文字列としてイコールがないものを渡す() {
        Map<String, String> headerField = new HashMap<>();
        headerField.put("Content-Type", "application/x-www-form-urlencoded");

        byte[] messageBody = "name%3dasada%26title%3dtest%26text%3d%e3%81%93%e3%82%93%e3%81%ab%e3%81%a1%e3%81%af%26password%3dtest%26param".getBytes();
        setMessageBodyAndHeader(headerField, messageBody);

        sut.parseMessageBodyToMap();
    }

    @Test(expected = RequestParseException.class)
    public void 不正な文字列としてアンパサンドがないものを渡す() {
        Map<String, String> headerField = new HashMap<>();
        headerField.put("Content-Type", "application/x-www-form-urlencoded");

        byte[] messageBody = "name%3Dasadatitle%3Dtest%26text%3D%82%B1%82%F1%82%C9%82%BF%82%CD%26password%3Dtest%26param%3Dcontribution".getBytes();
        setMessageBodyAndHeader(headerField, messageBody);

        sut.parseMessageBodyToMap();
    }
}
