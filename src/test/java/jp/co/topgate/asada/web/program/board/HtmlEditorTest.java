package jp.co.topgate.asada.web.program.board;

import jp.co.topgate.asada.web.program.board.model.Message;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * HtmlEditorクラスのテスト
 *
 * @author asada
 */
public class HtmlEditorTest {
    private List<Message> messageList = new ArrayList<>();

    @Before
    public void setUp() {
        Message m;
        m = new Message();
        m.setMessageID(1);
        m.setPassword("test");
        m.setName("管理者");
        m.setTitle("test");
        m.setText("こんにちは");
        m.setDate("2017/5/11 11:56");
        messageList.add(m);

        m = new Message();
        m.setMessageID(2);
        m.setPassword("t");
        m.setName("asada");
        m.setTitle("t");
        m.setText("こんにちは");
        m.setDate("2017/5/11 11:57");
        messageList.add(m);

        m = new Message();
        m.setMessageID(3);
        m.setPassword("t");
        m.setName("asada");
        m.setTitle("t");
        m.setText("今日は天気がいいですね");
        m.setDate("2017/5/11 11:57");
        messageList.add(m);

        m = new Message();
        m.setMessageID(4);
        m.setPassword("t");
        m.setName("管理者");
        m.setTitle("t");
        m.setText("そうですね");
        m.setDate("2017/5/11 11:57");
        messageList.add(m);
    }

    @Test
    public void editIndexメソッドのテスト() throws Exception {
        String resultIndex = HtmlEditor.editIndexHtml(messageList, "index4message");

        String[] index = resultIndex.split("\n");

        try (BufferedReader br = new BufferedReader(new FileReader(new File("./src/test/resources/html/index4message.html")))) {
            for (String s : index) {
                assertThat(s, is(br.readLine()));
            }
        }
    }

    @Test
    public void editSearchメソッドのテスト() throws Exception {
        String resultSearch = HtmlEditor.editSearchHtml(messageList, "search4message");

        String[] search = resultSearch.split("\n");

        try (BufferedReader br = new BufferedReader(new FileReader(new File("./src/test/resources/html/search4message.html")))) {
            for (String s : search) {
                assertThat(s, is(br.readLine()));
            }
        }
    }

    @Test
    public void editDeleteメソッドのテスト() throws Exception {
        Message m;
        m = new Message();
        m.setMessageID(1);
        m.setPassword("test");
        m.setName("管理者");
        m.setTitle("test");
        m.setText("こんにちは");
        m.setDate("2017/5/11 11:56");

        String resultHtml = HtmlEditor.editDeleteHtml(m);

        String[] delete = resultHtml.split("\n");

        try (BufferedReader br = new BufferedReader(new FileReader(new File("./src/test/resources/html/delete.html")))) {
            for (String s : delete) {
                assertThat(s, is(br.readLine()));
            }
        }
    }

    @Test
    public void changeLineFeedToBrTagメソッドのテスト() throws Exception {
        String raw = "";
        String data = HtmlEditor.changeLineFeedToBrTag(raw);
        assertThat(data, is(""));

        raw = "改行\nテスト";
        data = HtmlEditor.changeLineFeedToBrTag(raw);
        assertThat(data, is("改行<br>テスト"));

        raw = "改行\r\nテスト";
        data = HtmlEditor.changeLineFeedToBrTag(raw);
        assertThat(data, is("改行<br>テスト"));

        raw = "複数\r\n改行\r\nテスト";
        data = HtmlEditor.changeLineFeedToBrTag(raw);
        assertThat(data, is("複数<br>改行<br>テスト"));
    }

    @Test
    public void getResultHtmlメソッドのテスト() throws Exception {
        String[] sut = HtmlEditor.getResultHtml().split("\n");

        try (BufferedReader br = new BufferedReader(new FileReader(new File("./src/test/resources/html/result.html")))) {
            for (String str : sut) {
                assertThat(str, is(br.readLine()));
            }
        }
    }
}
