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
        assertThat(Main.controlServer(server, ServerCommand.STOP), is(ServerMessage.ALREADY_STOP));
        assertThat(Main.controlServer(server, ServerCommand.START), is(ServerMessage.START));
        assertThat(Main.controlServer(server, ServerCommand.START), is(ServerMessage.ALREADY_RUNNING));
        assertThat(Main.controlServer(server, ServerCommand.STOP), is(ServerMessage.STOP));
        assertThat(Main.controlServer(server, ServerCommand.END), is(ServerMessage.END));
    }
}
