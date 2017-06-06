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

        assertThat(sut.findUriQueryOrNull("name"), is("asada"));
        assertThat(sut.findUriQueryOrNull("like"), is("cat"));

        assertThat(sut.findHeaderByNameOrNull("Content-Type"), is("application/x-www-form-urlencoded"));

        Map<String, String> messageBody = sut.parseMessageBodyToMapOrNull();
        assertThat(messageBody.get("name"), is("asada"));
        assertThat(messageBody.get("title"), is("test"));
        assertThat(messageBody.get("text"), is("こんにちは"));
        assertThat(messageBody.get("password"), is("test"));
        assertThat(messageBody.get("param"), is("contribution"));
    }

    @Test
    public void findUriQueryOrNullメソッドのテスト() {
        assertThat(sut.findUriQueryOrNull("name"), is("asada"));
        assertThat(sut.findUriQueryOrNull("like"), is("cat"));
        assertThat(sut.findUriQueryOrNull("hoge"), is(nullValue()));
        assertThat(sut.findUriQueryOrNull(null), is(nullValue()));
    }

    @Test
    public void findHeaderByNameOrNullメソッドのテスト() {
        assertThat(sut.findHeaderByNameOrNull("Content-Type"), is("application/x-www-form-urlencoded"));
        assertThat(sut.findHeaderByNameOrNull("nothing"), is(nullValue()));
        assertThat(sut.findHeaderByNameOrNull(null), is(nullValue()));
    }

    private void setMessageBodyAndHeader(String contentType, byte[] messageBody) {
        Map<String, String> headerField = new HashMap<>();
        headerField.put("Content-Type", contentType);

        sut = new RequestMessage(null, null, null, headerField, messageBody);
    }

    @Test
    public void parseMessageBodyToMapOrNullメソッドのテスト() {
        byte[] messageBody = "".getBytes();
        setMessageBodyAndHeader("multipart/form-data", messageBody);
        assertThat(sut.parseMessageBodyToMapOrNull(), is(nullValue()));

        messageBody = "name%3dasada%26title%3dtest%26text%3d%e3%81%93%e3%82%93%e3%81%ab%e3%81%a1%e3%81%af%26password%3dtest%26param%3Dcontribution".getBytes();
        setMessageBodyAndHeader("application/x-www-form-urlencoded", messageBody);

        Map<String, String> result = sut.parseMessageBodyToMapOrNull();
        assertThat(result.get("name"), is("asada"));
    }

    @Test(expected = RequestParseException.class)
    public void 不正な文字列としてイコールがないものを渡す() {
        byte[] messageBody = "name%3dasada%26title%3dtest%26text%3d%e3%81%93%e3%82%93%e3%81%ab%e3%81%a1%e3%81%af%26password%3dtest%26param".getBytes();
        setMessageBodyAndHeader("application/x-www-form-urlencoded", messageBody);

        sut.parseMessageBodyToMapOrNull();
    }

    @Test(expected = RequestParseException.class)
    public void 不正な文字列としてアンパサンドがないものを渡す() {
        byte[] messageBody = "name%3Dasadatitle%3Dtest%26text%3D%82%B1%82%F1%82%C9%82%BF%82%CD%26password%3Dtest%26param%3Dcontribution".getBytes();
        setMessageBodyAndHeader("application/x-www-form-urlencoded", messageBody);

        sut.parseMessageBodyToMapOrNull();
    }
}
