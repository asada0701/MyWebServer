package jp.co.topgate.asada.web;

/**
 * Created by yusuke-pc on 2017/05/09.
 */

import jp.co.topgate.asada.web.model.Message;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CsvWriterTest {
    @Test
    public void readメソッドのテスト() throws Exception {
        CsvWriter.setFilePath("./src/test/resources/data/message.csv");
        List<Message> sut = CsvWriter.read();
        Message m;

        m = sut.get(0);
        assertThat(m.getMessageID(), is(2));
        assertThat(m.getPassword(), is("t"));
        assertThat(m.getName(), is("管理者"));
        assertThat(m.getTitle(), is("test"));
        assertThat(m.getText(), is("t"));
        assertThat(m.getDate(), is("2017/5/9 15:47"));

        m = sut.get(2);
        assertThat(m.getMessageID(), is(5));
        assertThat(m.getPassword(), is("test"));
        assertThat(m.getName(), is("電子太郎"));
        assertThat(m.getTitle(), is("test"));
        assertThat(m.getText(), is("test"));
        assertThat(m.getDate(), is("2017/5/9 15:50"));
    }

    @Test
    public void writeメソッドのテスト() throws Exception {
        CsvWriter.setFilePath("./src/test/resources/data/message.csv");
        List<Message> sut = new ArrayList<>();

        Message m = new Message();
        m.setMessageID(100);
        m.setPassword("pass");
        m.setName("管理者");
        m.setTitle("title");
        m.setText("text");
        m.setDate("2017/5/9 17:24");
        sut.add(m);

        CsvWriter.write(sut);

        sut = CsvWriter.read();
        Message m2 = sut.get(0);

        assertThat(m.getMessageID(), is(m2.getMessageID()));
        assertThat(m.getPassword(), is(m2.getPassword()));
        assertThat(m.getName(), is(m2.getName()));
        assertThat(m.getTitle(), is(m2.getTitle()));
        assertThat(m.getText(), is(m2.getText()));
        assertThat(m.getDate(), is(m2.getDate()));

        //初期化する
        sut = new ArrayList<>();

        m = new Message();
        m.setMessageID(2);
        m.setPassword("t");
        m.setName("管理者");
        m.setTitle("test");
        m.setText("t");
        m.setDate("2017/5/9 15:47");
        sut.add(m);

        m = new Message();
        m.setMessageID(3);
        m.setPassword("TG");
        m.setName("管理者");
        m.setTitle("test");
        m.setText("TG");
        m.setDate("2017/5/9 15:49");
        sut.add(m);

        m = new Message();
        m.setMessageID(5);
        m.setPassword("test");
        m.setName("電子太郎");
        m.setTitle("test");
        m.setText("test");
        m.setDate("2017/5/9 15:50");
        sut.add(m);

        m = new Message();
        m.setMessageID(6);
        m.setPassword("1111");
        m.setName("asada");
        m.setTitle("こんにちは");
        m.setText("1111");
        m.setDate("2017/5/9 15:53");
        sut.add(m);

        CsvWriter.write(sut);
    }
}
