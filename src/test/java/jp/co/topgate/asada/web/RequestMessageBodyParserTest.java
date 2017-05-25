package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.exception.RequestParseException;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * RequestMessageBodyParserクラスのテスト
 *
 * @author asada
 */
public class RequestMessageBodyParserTest {
    @Test
    public void parseToMapStringメソッドのテスト() {
        //name=asada&title=test&text=こんにちは&password=test&param=contribution
        String target = "name%3dasada%26title%3dtest%26text%3d%e3%81%93%e3%82%93%e3%81%ab%e3%81%a1%e3%81%af%26password%3dtest%26param%3dcontribution";
        Map<String, String> messageBody = RequestMessageBodyParser.parseToMapString(target.getBytes());

        assertThat(messageBody.get("name"), is("asada"));
        assertThat(messageBody.get("title"), is("test"));
        assertThat(messageBody.get("text"), is("こんにちは"));
        assertThat(messageBody.get("password"), is("test"));
        assertThat(messageBody.get("param"), is("contribution"));
    }

    @Test(expected = NullPointerException.class)
    public void nullチェック() {
        RequestMessageBodyParser.parseToMapString(null);
    }

    @Test(expected = RequestParseException.class)
    public void 空チェック() {
        Map<String, String> messageBody = RequestMessageBodyParser.parseToMapString("".getBytes());
        assertThat(messageBody.size(), is(0));
    }

    @Test(expected = RequestParseException.class)
    public void 不正な文字列としてイコールがないものを渡す() {
        //name=asada&title=test&text=こんにちは&password=test&param
        String target = "name%3dasada%26title%3dtest%26text%3d%e3%81%93%e3%82%93%e3%81%ab%e3%81%a1%e3%81%af%26password%3dtest%26param";
        RequestMessageBodyParser.parseToMapString(target.getBytes());
    }

    @Test(expected = RequestParseException.class)
    public void 不正な文字列としてアンパサンドがないものを渡す() {
        //name=asadatitle=test&text=こんにちは&password=test&param=contribution
        String target = "name%3Dasadatitle%3Dtest%26text%3D%82%B1%82%F1%82%C9%82%BF%82%CD%26password%3Dtest%26param%3Dcontribution";
        RequestMessageBodyParser.parseToMapString(target.getBytes());
    }
}
