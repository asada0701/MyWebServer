package jp.co.topgate.asada.web.model;

import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * ModelControllerクラスのテスト
 *
 * @author asada
 */
public class ModelControllerTest {
    @Test
    public void テスト() throws Exception {
        //コンストラクタのテスト
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

        //addMessageメソッドとgetNowDateメソッドのテスト
        LocalDateTime ldt = LocalDateTime.now();
        String now = String.valueOf(ldt.getYear()) + "/" + ldt.getMonthValue() + "/" + ldt.getDayOfMonth() +
                " " + ldt.getHour() + ":" + ldt.getMinute();

        ModelController.addMessage("name2", "title2", "text2", "password2");
        ModelController.addMessage("name2", "title3", "text3", "password3");
        ModelController.addMessage("name4", "title4", "text4", "password4");

        assertThat(ModelController.size(), is(4));

        //getAllMessageメソッドのテスト
        list = ModelController.getAllMessage();

        assertThat(list.get(1).getDate(), is(now));

        //findMessageメソッドのテスト
        m = ModelController.findMessageByID(2);

        assertThat(m.getDate(), is(now));

        //findSameNameMessageメソッドのテスト
        list = ModelController.findMessageByName("name2");

        assertThat(list.size(), is(2));

        //deleteMessageメソッドのテスト
        assertThat(ModelController.deleteMessage(2, "password2"), is(true));

        assertThat(ModelController.size(), is(3));
        assertThat(ModelController.getMessageID(), is(5));
    }
}
