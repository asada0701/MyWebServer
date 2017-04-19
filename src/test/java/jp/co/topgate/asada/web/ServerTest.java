package jp.co.topgate.asada.web;

import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by yusuke-pc on 2017/04/13.
 */
public class ServerTest {
    @Test
    public void startServerとstopServerメソッドのテスト() throws IOException {
        try {
            Server sut = new Server();
            sut.startServer();
            assertThat(sut.getState(), is(Thread.State.RUNNABLE));

            sut.stopServer();
            assertThat(sut.getState(), is(Thread.State.TERMINATED));
        } catch (IOException e) {
            throw e;
        }
    }

    @Test
    public void endServerメソッドのテスト() throws IOException {
        try {
            Server sut = new Server();
            sut.startServer();
            assertThat(sut.getState(), is(Thread.State.RUNNABLE));

            sut.endServer();
            assertThat(sut.getState(), is(Thread.State.TERMINATED));
        } catch (IOException e) {
            throw e;
        }
    }
}
