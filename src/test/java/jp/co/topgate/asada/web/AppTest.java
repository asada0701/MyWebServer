package jp.co.topgate.asada.web;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Appクラスのテスト
 *
 * @author asada
 */
public class AppTest {
    @Test
    public void サーバーテスト() throws Exception {
        Server server = new Server(8080);

        assertThat(App.controlServer(server, ServerCommand.STOP), is("http server is not running.."));
        assertThat(App.controlServer(server, ServerCommand.START), is("start up http server.."));
        assertThat(App.controlServer(server, ServerCommand.START), is("http server is already running.."));
        assertThat(App.controlServer(server, ServerCommand.STOP), is("wait a second, http server is returning a response.."));
        assertThat(App.controlServer(server, ServerCommand.END), is("bye.."));
    }
}
