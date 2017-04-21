package jp.co.topgate.asada.web;

/*
 * This Java source file was generated by the Gradle 'init' task.
 */

import jp.co.topgate.asada.web.exception.ScanChoicesRuntimeException;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class AppTest {
    public static Server server;

    @Test
    public void サーバースタートテスト() throws Exception {
        server = new Server();
        assertThat("start up http server..", is(App.controlServer(server, "1")));
        server.endServer();
    }

    @Test
    public void 想定していない文字が入力されたテスト() throws IOException {
        server = new Server();
        try {
            App.controlServer(server, "4");
        } catch (ScanChoicesRuntimeException expected) {
            assertThat(expected.getMessage(), equalTo("想定されていない文字が入力されました"));
        }
    }
}
