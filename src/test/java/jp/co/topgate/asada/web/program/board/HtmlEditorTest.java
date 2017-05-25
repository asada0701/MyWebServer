package jp.co.topgate.asada.web.program.board;

import jp.co.topgate.asada.web.model.Message;
import org.junit.*;

import java.io.*;
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
    private HtmlEditor htmlEditor;

    @Before
    public void setUp() {
        htmlEditor = new HtmlEditor();

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
    public void editIndexOrSearchHtmlメソッドのテスト() throws Exception {
        HtmlEditor sut = new HtmlEditor();
        ProgramBoardHtmlList indexEnum = ProgramBoardHtmlList.INDEX_HTML;
        ProgramBoardHtmlList searchEnum = ProgramBoardHtmlList.SEARCH_HTML;

        String resultIndex = sut.editIndexOrSearchHtml(indexEnum, messageList, "index4message");
        String resultSearch = sut.editIndexOrSearchHtml(searchEnum, messageList, "search4message");

        String[] index = resultIndex.split("\n");
        String[] search = resultSearch.split("\n");

        try (BufferedReader br = new BufferedReader(new FileReader(new File("./src/test/resources/html/index4message.html")))) {
            for (String s : index) {
                assertThat(s, is(br.readLine()));
            }
        }

        try (BufferedReader br = new BufferedReader(new FileReader(new File("./src/test/resources/html/search4message.html")))) {
            for (String s : search) {
                assertThat(s, is(br.readLine()));
            }
        }
    }

    @Test
    public void editDeleteメソッドのテスト() throws Exception {
        HtmlEditor sut = new HtmlEditor();
        Message m;
        m = new Message();
        m.setMessageID(1);
        m.setPassword("test");
        m.setName("管理者");
        m.setTitle("test");
        m.setText("こんにちは");
        m.setDate("2017/5/11 11:56");

        String resultHtml = sut.editDeleteHtml(m);

        String[] delete = resultHtml.split("\n");

        try (BufferedReader br = new BufferedReader(new FileReader(new File("./src/test/resources/html/delete.html")))) {
            for (String s : delete) {
                assertThat(s, is(br.readLine()));
            }
        }
    }

    @Test
    public void changeMessageToHtmlメソッドのテスト() throws Exception {
        Message m;
        m = new Message();
        m.setMessageID(1);
        m.setPassword("test");
        m.setName("管理者");
        m.setTitle("test");
        m.setText("こんにちは");
        m.setDate("2017/5/11 11:56");
        String index = HtmlEditor.changeMessageToHtml(ProgramBoardHtmlList.INDEX_HTML, m);
        String search = HtmlEditor.changeMessageToHtml(ProgramBoardHtmlList.SEARCH_HTML, m);
        String delete = HtmlEditor.changeMessageToHtml(ProgramBoardHtmlList.DELETE_HTML, m);

        assert index != null;
        String[] result = index.split("\n");
        int i = 0;
        assertThat(result[i++], is("            <tr id=\"No.1\">"));
        assertThat(result[i++], is("                <td>No.1</td>"));
        assertThat(result[i++], is("                <td>test</td>"));
        assertThat(result[i++], is("                <td>こんにちは</td>"));
        assertThat(result[i++], is("                <td>管理者</td>"));
        assertThat(result[i++], is("                <td>2017/5/11 11:56</td>"));
        assertThat(result[i++], is("                <td>"));
        assertThat(result[i++], is("                    <form action=\"/program/board/\" method=\"post\">"));
        assertThat(result[i++], is("                        <input type=\"hidden\" name=\"param\" value=\"search\">"));
        assertThat(result[i++], is("                        <input type=\"hidden\" name=\"number\" value=\"1\">"));
        assertThat(result[i++], is("                        <input type=\"submit\" value=\"このコメントを投稿した人の他のコメントを見てみる\">"));
        assertThat(result[i++], is("                    </form>"));
        assertThat(result[i++], is("                </td>"));
        assertThat(result[i++], is("                <td>"));
        assertThat(result[i++], is("                    <form action=\"/program/board/\" method=\"post\">"));
        assertThat(result[i++], is("                        <input type=\"hidden\" name=\"param\" value=\"delete_step_1\">"));
        assertThat(result[i++], is("                        <input type=\"hidden\" name=\"number\" value=\"1\">"));
        assertThat(result[i++], is("                        <input type=\"submit\" value=\"このコメントを削除する\">"));
        assertThat(result[i++], is("                    </form>"));
        assertThat(result[i], is("                </td>"));

        assert search != null;
        result = search.split("\n");
        i = 0;
        assertThat(result[i++], is("            <tr id=\"No.1\">"));
        assertThat(result[i++], is("                <td>No.1</td>"));
        assertThat(result[i++], is("                <td>test</td>"));
        assertThat(result[i++], is("                <td>こんにちは</td>"));
        assertThat(result[i++], is("                <td>管理者</td>"));
        assertThat(result[i++], is("                <td>2017/5/11 11:56</td>"));
        assertThat(result[i++], is("                <td>"));
        assertThat(result[i++], is("                    <form action=\"/program/board/\" method=\"post\">"));
        assertThat(result[i++], is("                        <input type=\"hidden\" name=\"param\" value=\"delete_step_1\">"));
        assertThat(result[i++], is("                        <input type=\"hidden\" name=\"number\" value=\"1\">"));
        assertThat(result[i++], is("                        <input type=\"submit\" value=\"このコメントを削除する\">"));
        assertThat(result[i++], is("                    </form>"));
        assertThat(result[i], is("                </td>"));

        assert delete != null;
        result = delete.split("\n");
        i = 0;
        assertThat(result[i++], is("            <tr id=\"No.1\">"));
        assertThat(result[i++], is("                <td>No.1</td>"));
        assertThat(result[i++], is("                <td>test</td>"));
        assertThat(result[i++], is("                <td>こんにちは</td>"));
        assertThat(result[i++], is("                <td>管理者</td>"));
        assertThat(result[i], is("                <td>2017/5/11 11:56</td>"));
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
    public void writeHtmlメソッドのテスト() throws Exception {
        HtmlEditor he = new HtmlEditor();

        he.writeHtml(ProgramBoardHtmlList.INDEX_HTML, "test");

        String path = "./src/main/resources/2/index.html";
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path))))) {
            assertThat(br.readLine(), is("test"));
        }

        he.resetAllFiles();
    }

    @After
    public void tearDown() {
        htmlEditor.resetAllFiles();
    }
}
