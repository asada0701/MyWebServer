package jp.co.topgate.asada.web;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * 列挙型のServerCommandをテストする
 *
 * @author asada
 */
public class ServerCommandTest {
    @Test
    public void IDのテスト() {
        assertThat(ServerCommand.START.getId(), is("1"));
        assertThat(ServerCommand.STOP.getId(), is("2"));
        assertThat(ServerCommand.END.getId(), is("3"));
    }

    @Test(expected = NullPointerException.class)
    public void getServerCommandメソッドのnullチェック() {
        ServerCommand.getServerCommand(null);
    }

    @Test
    public void getServerCommandメソッドの空チェック() {
        ServerCommand sut = ServerCommand.getServerCommand("");
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

    @Test(expected = NullPointerException.class)
    public void containsメソッドのnullチェック() {
        ServerCommand.contains(null);
    }

    @Test
    public void containsメソッドの空チェック() {
        assertThat(ServerCommand.contains(""), is(false));
    }

    @Test
    public void テスト1を渡すとtrue() {
        assertThat(ServerCommand.contains("1"), is(true));
    }

    @Test
    public void テスト2を渡すとtrue() {
        assertThat(ServerCommand.contains("2"), is(true));
    }

    @Test
    public void テスト3を渡すとtrue() {
        assertThat(ServerCommand.contains("3"), is(true));
    }

}
