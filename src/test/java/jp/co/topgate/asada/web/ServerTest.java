package jp.co.topgate.asada.web;

import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by yusuke-pc on 2017/04/13.
 */
public class ServerTest {
    public Server sut;
    @Test
    public void startServerとstopServerメソッドのテスト() throws IOException {
        try {
            sut = new Server();
            sut.startServer();
            assertThat(sut.getState(), is(Thread.State.RUNNABLE));
            sut.endServer();
        } catch (IOException e) {
            throw e;
        }
    }
}
