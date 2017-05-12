package jp.co.topgate.asada.web.app;

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
//    @Test
//    public void readメソッドのテスト() throws Exception {
//        CsvWriter.setFilePath("./src/test/resources/data/message.csv");
//        List<Message> sut = CsvWriter.readToMessage();
//        Message m;
//
//        m = sut.get(0);
//        assertThat(m.getMessageID(), is(1));
//        assertThat(m.getPassword(), is("test"));
//        assertThat(m.getName(), is("管理者"));
//        assertThat(m.getTitle(), is("test"));
//        assertThat(m.getText(), is("こんにちは"));
//        assertThat(m.getDate(), is("2017/5/11 11:56"));
//
//        m = sut.get(2);
//        assertThat(m.getMessageID(), is(3));
//        assertThat(m.getPassword(), is("t"));
//        assertThat(m.getName(), is("asada"));
//        assertThat(m.getTitle(), is("t"));
//        assertThat(m.getText(), is("今日は天気がいいですね"));
//        assertThat(m.getDate(), is("2017/5/11 11:57"));
//    }
//
//    @Test
//    public void writeメソッドのテスト() throws Exception {
//        CsvWriter.setFilePath("./src/test/resources/data/message.csv");
//        List<Message> sut = new ArrayList<>();
//
//        Message m = new Message();
//        m.setMessageID(100);
//        m.setPassword("pass");
//        m.setName("管理者");
//        m.setTitle("title");
//        m.setText("text");
//        m.setDate("2017/5/9 17:24");
//        sut.add(m);
//
//        CsvWriter.write(CsvMode.MESSAGE_MODE, sut);
//
//        sut = CsvWriter.readToMessage();
//        Message m2 = sut.get(0);
//
//        assertThat(m.getMessageID(), is(m2.getMessageID()));
//        assertThat(m.getPassword(), is(m2.getPassword()));
//        assertThat(m.getName(), is(m2.getName()));
//        assertThat(m.getTitle(), is(m2.getTitle()));
//        assertThat(m.getText(), is(m2.getText()));
//        assertThat(m.getDate(), is(m2.getDate()));
//
//        //初期化する
//        sut = new ArrayList<>();
//
//        m = new Message();
//        m.setMessageID(1);
//        m.setPassword("test");
//        m.setName("管理者");
//        m.setTitle("test");
//        m.setText("こんにちは");
//        m.setDate("2017/5/11 11:56");
//        sut.add(m);
//
//        m = new Message();
//        m.setMessageID(2);
//        m.setPassword("t");
//        m.setName("asada");
//        m.setTitle("t");
//        m.setText("こんにちは");
//        m.setDate("2017/5/11 11:57");
//        sut.add(m);
//
//        m = new Message();
//        m.setMessageID(3);
//        m.setPassword("t");
//        m.setName("asada");
//        m.setTitle("t");
//        m.setText("今日は天気がいいですね");
//        m.setDate("2017/5/11 11:57");
//        sut.add(m);
//
//        m = new Message();
//        m.setMessageID(4);
//        m.setPassword("t");
//        m.setName("管理者");
//        m.setTitle("t");
//        m.setText("そうですね");
//        m.setDate("2017/5/11 11:57");
//        sut.add(m);
//
//        CsvWriter.write(CsvMode.MESSAGE_MODE, sut);
//    }
}
