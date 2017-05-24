package jp.co.topgate.asada.web;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * 列挙型のServerCommandをテストする
 *
 * @author asada
 */
@RunWith(Enclosed.class)
public class ServerCommandTest {
    public static class 網羅テスト {
        @Test
        public void 用意されている定数() {
            assertThat(ServerCommand.START.getId(), is("1"));
            assertThat(ServerCommand.STOP.getId(), is("2"));
            assertThat(ServerCommand.END.getId(), is("3"));
        }
    }

    public static class getServerCommandメソッドのテスト {
        @Test(expected = NullPointerException.class)
        public void getServerCommandメソッドのnullチェック() {
            ServerCommand.getServerCommand(null);
        }

        @Test
        public void 異常な値を渡すとENDが返ってくる() {
            ServerCommand sut = ServerCommand.getServerCommand("");
            assertThat(sut, is(ServerCommand.END));

            sut = ServerCommand.getServerCommand("日本語テスト");
            assertThat(sut, is(ServerCommand.END));
        }

        @Test
        public void ServerCommand_STARTが返ってくる() {
            ServerCommand sut = ServerCommand.getServerCommand("1");
            assertThat(sut, is(ServerCommand.START));
        }

        @Test
        public void ServerCommand_STOPが返ってくる() {
            ServerCommand sut = ServerCommand.getServerCommand("2");
            assertThat(sut, is(ServerCommand.STOP));
        }

        @Test
        public void ServerCommand_ENDが返ってくる() {
            ServerCommand sut = ServerCommand.getServerCommand("3");
            assertThat(sut, is(ServerCommand.END));
        }
    }

    public static class containsメソッドのテスト {
        @Test(expected = NullPointerException.class)
        public void containsメソッドのnullチェック() {
            ServerCommand.contains(null);
        }

        @Test
        public void 異常な値を渡すとfalseが返ってくる() {
            assertThat(ServerCommand.contains("4"), is(false));

            assertThat(ServerCommand.contains(""), is(false));

            assertThat(ServerCommand.contains("0"), is(false));

            assertThat(ServerCommand.contains("寿司寿司"), is(false));
        }

        @Test
        public void String1を渡すとtrue() {
            assertThat(ServerCommand.contains("1"), is(true));
        }

        @Test
        public void String2を渡すとtrue() {
            assertThat(ServerCommand.contains("2"), is(true));
        }

        @Test
        public void String3を渡すとtrue() {
            assertThat(ServerCommand.contains("3"), is(true));
        }
    }
}
