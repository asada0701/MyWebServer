package jp.co.topgate.asada.web;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * 列挙型のServerMessageをテストする
 */
public class ServerMessageTest {
    @Test
    public void 用意されている定数(){
        assertThat(ServerMessage.START.getMessage(), is("start up http server.."));

        assertThat(ServerMessage.ALREADY_RUNNING.getMessage(), is("http server is already running.."));

        assertThat(ServerMessage.STOP.getMessage(), is("http server stops.."));

        assertThat(ServerMessage.ALREADY_STOP.getMessage(), is("http server is not running.."));

        assertThat(ServerMessage.CAN_NOT_STOP.getMessage(), is("wait a second, http server is returning a response.."));

        assertThat(ServerMessage.END.getMessage(), is("bye.."));
    }
}
