package jp.co.topgate.asada.web.model;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * ModelControllerクラスのテスト
 *
 * @author asada
 */
public class ModelControllerTest {
    @Before
    public void setUp() {
        ModelController.setMessageList(new ArrayList<>());
        ModelController.resetNextMessageID();
        ModelController.addMessage("name1", "title1", "text1", "password1", "timeID1");
        ModelController.addMessage("name2", "title2", "text2", "password2", "timeID2");
        ModelController.addMessage("name2", "title3", "text3", "password3", "timeID3");
        ModelController.addMessage("name4", "title4", "text4", "password4", "timeID4");
    }

    @Test
    public void setMessageListメソッドのテスト() throws Exception {
        List<Message> list = new ArrayList<>();
        Message m = new Message();
        m.setMessageID(1);
        m.setPassword("password1");
        m.setName("name1");
        m.setTitle("title1");
        m.setText("text1");
        m.setDate("2017/5/9 16:34");
        list.add(m);
        ModelController.setMessageList(list);

        assertThat(ModelController.size(), is(1));
    }

    @Test
    public void addMessageメソッドのテスト() {
        ModelController.addMessage("name5", "title5", "text5", "password5", "timeID5");

        assertThat(ModelController.size(), is(5));
    }

    @Test
    public void getNowDateメソッドのテスト() {
        LocalDateTime ldt = LocalDateTime.now();
        String now = String.valueOf(ldt.getYear()) + "/" + ldt.getMonthValue() + "/" + ldt.getDayOfMonth() +
                " " + ldt.getHour() + ":" + ldt.getMinute();

        assertThat(ModelController.getNowDate(), is(now));
    }

    @Test
    public void getAllMessageメソッドのテスト() {
        List<Message> sut = ModelController.getAllMessage();

        assertThat(sut.size(), is(4));
        assertThat(sut.get(0).getName(), is("name1"));
        assertThat(sut.get(1).getName(), is("name2"));
        assertThat(sut.get(2).getName(), is("name2"));
        assertThat(sut.get(3).getName(), is("name4"));
    }

    @Test
    public void findSameNameMessageメソッドのテスト() {
        List<Message> list = ModelController.findMessageByName("name2");

        assertThat(list.size(), is(2));
    }

    @Test
    public void deleteMessageメソッドのテスト() {
        assertThat(ModelController.deleteMessage(2, "password2"), is(true));
        assertThat(ModelController.size(), is(3));
        assertThat(ModelController.getNextMessageID(), is(5));
    }

    @Test
    public void 未登録状態でgetNameメソッドを呼び出す() {
        ModelController.setMessageList(new ArrayList<>());
        ModelController.resetNextMessageID();

        List<Message> messageList = ModelController.getAllMessage();
        assertThat(messageList.size(), is(0));

        assertThat(ModelController.getName(0), is(nullValue()));

        assertThat(ModelController.getNextMessageID(), is(1));
    }

    @Test(expected = NullPointerException.class)
    public void setMessageListメソッドにnullを渡す() {
        ModelController.setMessageList(null);
    }
}
