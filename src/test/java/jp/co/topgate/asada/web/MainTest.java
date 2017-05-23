package jp.co.topgate.asada.web;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Appクラスのテスト
 *
 * @author asada
 */
public class MainTest {
    @Test
    public void サーバーテスト() throws Exception {
        Server server = new Server(8080);

        assertThat(Main.controlServer(server, ServerCommand.STOP), is("http server is not running.."));
        assertThat(Main.controlServer(server, ServerCommand.START), is("start up http server.."));
        assertThat(Main.controlServer(server, ServerCommand.START), is("http server is already running.."));
        assertThat(Main.controlServer(server, ServerCommand.STOP), is("wait a second, http server is returning a response.."));
        assertThat(Main.controlServer(server, ServerCommand.END), is("bye.."));
    }
}
