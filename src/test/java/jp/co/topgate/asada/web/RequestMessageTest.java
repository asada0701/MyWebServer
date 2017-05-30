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

    //name=asada&title=test&text=こんにちは&password=test&param=contribution
    private String exapmleMessageBody = "name%3dasada%26title%3dtest%26text%3d%e3%81%93%e3%82%93%e3%81%ab%e3%81%a1%e3%81%af%26password%3dtest%26param%3dcontribution";

    @Before
    public void setUp() {
        sut = new RequestMessage();
    }

    /**
     * リクエストメッセージのオブジェクトにコンテンツタイプを追加するヘルパーメソッドです
     */
    private void setHeaderWithContentType() {
        Map<String, String> headerField = new HashMap<>();
        headerField.put("Content-Type", "application/x-www-form-urlencoded");
        sut.setHeaderField(headerField);
    }

    @Test
    public void methodのテスト() {
        assertThat(sut.getMethod(), is(nullValue()));

        sut.setMethod("GET");
        assertThat(sut.getMethod(), is("GET"));

        sut.setMethod(null);
        assertThat(sut.getMethod(), is(nullValue()));
    }

    @Test
    public void uriのテスト() {
        assertThat(sut.getUri(), is(nullValue()));

        sut.setUri("/index.html");
        assertThat(sut.getUri(), is("/index.html"));

        sut.setUri(null);
        assertThat(sut.getUri(), is(nullValue()));
    }

    @Test
    public void headerFieldのテスト() {
        Map<String, String> headerField = new HashMap<>();
        headerField.put("hoge", "hogehoge");
        headerField.put("Content-Type", "application/x-www-form-urlencoded");
        sut.setHeaderField(headerField);

        assertThat(sut.findHeaderByName("hoge"), is("hogehoge"));
        assertThat(sut.findHeaderByName("Content-Type"), is("application/x-www-form-urlencoded"));
        assertThat(sut.findHeaderByName("nothing"), is(nullValue()));
        assertThat(sut.findHeaderByName(null), is(nullValue()));
    }

    @Test
    public void messageBodyのテストコンテンツタイプが想定外のもの() {
        Map<String, String> headerField = new HashMap<>();
        headerField.put("Content-Type", "multipart/form-data");
        sut.setHeaderField(headerField);

        sut.setMessageBody(exapmleMessageBody.getBytes());

        assertThat(sut.parseMessageBodyToMap(), is(nullValue()));
    }

    @Test
    public void parseMessageBodyToMapメソッドのテスト() {
        setHeaderWithContentType();
        sut.setMessageBody(exapmleMessageBody.getBytes());
        Map<String, String> messageBody = sut.parseMessageBodyToMap();

        assertThat(messageBody.get("name"), is("asada"));
        assertThat(messageBody.get("title"), is("test"));
        assertThat(messageBody.get("text"), is("こんにちは"));
        assertThat(messageBody.get("password"), is("test"));
        assertThat(messageBody.get("param"), is("contribution"));
    }

    @Test(expected = RequestParseException.class)
    public void 空チェック() {
        setHeaderWithContentType();

        sut.setMessageBody("".getBytes());
        Map<String, String> messageBody = sut.parseMessageBodyToMap();
        assertThat(messageBody.size(), is(0));
        sut.parseMessageBodyToMap();
    }

    @Test(expected = RequestParseException.class)
    public void 不正な文字列としてイコールがないものを渡す() {
        setHeaderWithContentType();

        //name=asada&title=test&text=こんにちは&password=test&param
        String target = "name%3dasada%26title%3dtest%26text%3d%e3%81%93%e3%82%93%e3%81%ab%e3%81%a1%e3%81%af%26password%3dtest%26param";
        sut.setMessageBody(target.getBytes());
        sut.parseMessageBodyToMap();
    }

    @Test(expected = RequestParseException.class)
    public void 不正な文字列としてアンパサンドがないものを渡す() {
        setHeaderWithContentType();

        //name=asadatitle=test&text=こんにちは&password=test&param=contribution
        String target = "name%3Dasadatitle%3Dtest%26text%3D%82%B1%82%F1%82%C9%82%BF%82%CD%26password%3Dtest%26param%3Dcontribution";
        sut.setMessageBody(target.getBytes());
        sut.parseMessageBodyToMap();
    }

    @Test
    public void uriQueryのテスト() {
        Map<String, String> uriQuery = new HashMap<>();
        uriQuery.put("name", "asada");
        uriQuery.put("from", "japan");
        sut.setUriQuery(uriQuery);

        assertThat(sut.findUriQuery("name"), is("asada"));
        assertThat(sut.findUriQuery("from"), is("japan"));
        assertThat(sut.findUriQuery("hoge"), is(nullValue()));
        assertThat(sut.findUriQuery(null), is(nullValue()));
    }
}
