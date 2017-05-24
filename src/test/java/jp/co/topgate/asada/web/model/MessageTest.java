package jp.co.topgate.asada.web.model;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Messageクラスのテスト
 *
 * @author asada
 */
public class MessageTest {
    @Test
    public void セッターゲッターのテスト() throws Exception {
        Message sut = new Message();
        sut.setMessageID(1);
        sut.setPassword("password");
        sut.setName("name");
        sut.setTitle("title");
        sut.setText("text");
        sut.setDate("2017/5/9 15:17");

        assertThat(sut.getMessageID(), is(1));
        assertThat(sut.getPassword(), is("password"));
        assertThat(sut.getName(), is("name"));
        assertThat(sut.getTitle(), is("title"));
        assertThat(sut.getText(), is("text"));
        assertThat(sut.getDate(), is("2017/5/9 15:17"));
    }

    @Test
    public void セッターを呼び出す前にゲッターを呼び出す() throws Exception {
        Message sut = new Message();

        assertThat(sut.getMessageID(), is(0));
        assertThat(sut.getPassword(), is(nullValue()));
        assertThat(sut.getName(), is(nullValue()));
        assertThat(sut.getTitle(), is(nullValue()));
        assertThat(sut.getText(), is(nullValue()));
        assertThat(sut.getDate(), is(nullValue()));
    }
}
