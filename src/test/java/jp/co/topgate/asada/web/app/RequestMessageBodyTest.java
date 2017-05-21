package jp.co.topgate.asada.web.app;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * RequestMessageBodyクラスのテスト
 *
 * @author asada
 */
@RunWith(Enclosed.class)
public class RequestMessageBodyTest {
    public static class messageBodyParseメソッドのテスト {
        @Test
        public void 正しく取り出せるか() {
            String s = "name%3dasada%26title%3dtest%26text%3d%e3%81%93%e3%82%93%e3%81%ab%e3%81%a1%e3%81%af%26password%3dtest%26param%3dcontribution";
            RequestMessageBody rmb = new RequestMessageBody(s.getBytes());
            Map<String, String> sut = rmb.parseToStringMap();
            assertThat(sut.get("name"), is("asada"));
            assertThat(sut.get("title"), is("test"));
            assertThat(sut.get("text"), is("こんにちは"));
            assertThat(sut.get("password"), is("test"));
            assertThat(sut.get("param"), is("contribution"));
        }
    }
}
