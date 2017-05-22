package jp.co.topgate.asada.web;

import org.junit.Test;

import java.io.IOException;
import java.net.BindException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Serverクラスのテスト
 *
 * @author asada
 */
public class ServerTest {
    @Test
    public void startServerメソッドのテスト() throws IOException {
        Server sut = new Server();
        sut.startServer();
        assertThat(sut.getState(), is(Thread.State.RUNNABLE));
        assertThat(sut.stopServer(), is(false));
        assertThat(sut.getState(), is(Thread.State.RUNNABLE));
        sut.endServer();
    }
}
