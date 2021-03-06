package jp.co.topgate.asada.web.program.board.model;

import jp.co.topgate.asada.web.exception.CsvRuntimeException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * CsvHelperクラスのテスト
 *
 * @author asada
 */
public class CsvHelperTest {
    private static List<Message> list;

    @BeforeClass
    public static void setUp() throws Exception {
        list = CsvHelper.readMessage();
        try (FileWriter fileWriter = new FileWriter(new File("./csv/message.csv"))) {
            fileWriter.write("");
            fileWriter.flush();
        }
    }

    @Test(expected = CsvRuntimeException.class)
    public void 項目数が少ない不正なCSVをreadする() throws Exception {
        try (FileWriter fileWriter = new FileWriter(new File("./csv/message.csv"))) {
            fileWriter.write("不正なCSVのテスト,項目数が少ない");
            fileWriter.flush();
        }
        CsvHelper.readMessage();
    }

    @Test(expected = CsvRuntimeException.class)
    public void 項目数が多い場合() throws Exception {
        try (FileWriter fileWriter = new FileWriter(new File("./csv/message.csv"))) {
            fileWriter.write("不,正,な,C,S,V,の,テ,ス,ト,項,目,数,が,多,い");
            fileWriter.flush();
        }
        CsvHelper.readMessage();
    }

    @Test
    public void readMessageメソッドのテスト() throws Exception {
        List<Message> sut = CsvHelper.readMessage();
        Message m;

        m = sut.get(0);
        assertThat(m.getMessageID(), is(1));
        assertThat(m.getPassword(), is("test"));
        assertThat(m.getName(), is("管理者"));
        assertThat(m.getTitle(), is("test"));
        assertThat(m.getText(), is("こんにちは"));
        assertThat(m.getDate(), is("2017/5/11 11:56"));

        m = sut.get(2);
        assertThat(m.getMessageID(), is(3));
        assertThat(m.getPassword(), is("t"));
        assertThat(m.getName(), is("asada"));
        assertThat(m.getTitle(), is("t"));
        assertThat(m.getText(), is("今日は天気がいいですね"));
        assertThat(m.getDate(), is("2017/5/11 11:57"));
    }

    @Test(expected = NullPointerException.class)
    public void writeMessageメソッドにnullを渡す() throws Exception {
        CsvHelper.writeMessage(null);
    }

    @Test
    public void writeMessageメソッドのテスト() throws Exception {
        List<Message> sut = new ArrayList<>();

        Message m = new Message();
        m.setMessageID(100);
        m.setPassword("pass");
        m.setName("管理者");
        m.setTitle("title");
        m.setText("text");
        m.setDate("2017/5/9 17:24");
        sut.add(m);

        CsvHelper.writeMessage(sut);

        sut = CsvHelper.readMessage();
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
        m.setMessageID(1);
        m.setPassword("test");
        m.setName("管理者");
        m.setTitle("test");
        m.setText("こんにちは");
        m.setDate("2017/5/11 11:56");
        sut.add(m);

        m = new Message();
        m.setMessageID(2);
        m.setPassword("t");
        m.setName("asada");
        m.setTitle("t");
        m.setText("こんにちは");
        m.setDate("2017/5/11 11:57");
        sut.add(m);

        m = new Message();
        m.setMessageID(3);
        m.setPassword("t");
        m.setName("asada");
        m.setTitle("t");
        m.setText("今日は天気がいいですね");
        m.setDate("2017/5/11 11:57");
        sut.add(m);

        m = new Message();
        m.setMessageID(4);
        m.setPassword("t");
        m.setName("管理者");
        m.setTitle("t");
        m.setText("そうですね");
        m.setDate("2017/5/11 11:57");
        sut.add(m);

        CsvHelper.writeMessage(sut);
    }

    @AfterClass
    public static void tearDown() throws Exception {
        CsvHelper.writeMessage(list);
    }
}
