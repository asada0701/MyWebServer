package jp.co.topgate.asada.web;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Appクラスのテスト
 *
 * @author asada
 */
@RunWith(Enclosed.class)
public class AppTest {
    public static class controlServerメソッドのテスト {
        @Test
        public void サーバーテスト() throws Exception {
            Server server = new Server();

            assertThat(App.controlServer(server, Choices.STOP), is("http server is not running.."));
            assertThat(App.controlServer(server, Choices.START), is("start up http server.."));
            assertThat(App.controlServer(server, Choices.START), is("http server is already running.."));
            assertThat(App.controlServer(server, Choices.STOP), is("wait a second, http server is returning a response.."));
            assertThat(App.controlServer(server, Choices.END), is("bye.."));
        }
    }

    public static class isSelectメソッドのテスト {
        @Test(expected = NullPointerException.class)
        public void nullチェック() {
            App.isSelect(null);
        }

        @Test
        public void 空チェック() {
            assertThat(App.isSelect(""), is(false));
        }

        @Test
        public void テスト1を渡すとtrue() {
            assertThat(App.isSelect("1"), is(true));
        }

        @Test
        public void テスト2を渡すとtrue() {
            assertThat(App.isSelect("2"), is(true));
        }

        @Test
        public void テスト3を渡すとtrue() {
            assertThat(App.isSelect("3"), is(true));
        }
    }
}
