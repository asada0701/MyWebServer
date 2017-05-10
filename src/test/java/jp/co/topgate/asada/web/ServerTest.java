package jp.co.topgate.asada.web;

import org.junit.Test;

import java.io.IOException;
import java.net.BindException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by yusuke-pc on 2017/04/13.
 */
public class ServerTest {
    @Test(expected = BindException.class)
    public void startServerメソッドのテスト() throws IOException {
        Server sut = new Server();
        sut.startServer();
        assertThat(sut.getState(), is(Thread.State.RUNNABLE));
        sut.endServer();
    }
}
