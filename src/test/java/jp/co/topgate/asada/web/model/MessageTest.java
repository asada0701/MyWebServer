package jp.co.topgate.asada.web.model;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by yusuke-pc on 2017/05/09.
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
}
