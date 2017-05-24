package jp.co.topgate.asada.web;

import org.junit.Test;
import org.junit.runner.notification.RunListener;

import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Appクラスのテスト
 *
 * @author asada
 */
public class MainTest {
    @Test
    public void 定数テスト() throws Exception {
        assertThat(Main.PORT_NUMBER, is(8080));
        assertThat(Main.CHARACTER_ENCODING_SCHEME, is("UTF-8"));
        assertThat(Main.WELCOME_PAGE_NAME, is("index.html"));
    }

    @Test
    public void サーバーテスト() throws Exception {
        Server server = new Server(8080);

        assertThat(Main.controlServer(server, ServerCommand.STOP), is(ServerMessage.ALREADY_STOP));
        assertThat(Main.controlServer(server, ServerCommand.START), is(ServerMessage.START));
        assertThat(Main.controlServer(server, ServerCommand.START), is(ServerMessage.ALREADY_RUNNING));
        assertThat(Main.controlServer(server, ServerCommand.STOP), is(ServerMessage.CAN_NOT_STOP));
        assertThat(Main.controlServer(server, ServerCommand.END), is(ServerMessage.END));
    }
}