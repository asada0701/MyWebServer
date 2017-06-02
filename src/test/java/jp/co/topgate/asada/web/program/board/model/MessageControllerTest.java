package jp.co.topgate.asada.web.program.board.model;

/**
 * ModelControllerクラスのテスト
 * TODO ModelControllerがクラス変数で持っていた値をCSVだけが持つように変更したので、テストを凍結した。
 *
 * @author asada
 */
public class MessageControllerTest {
//    @Before
//    public void setUp() {
//        MessageController.setMessageList(new ArrayList<>());
//        MessageController.resetNextMessageID();
//        MessageController.addMessage("name1", "title1", "text1", "password1", "timeID1");
//        MessageController.addMessage("name2", "title2", "text2", "password2", "timeID2");
//        MessageController.addMessage("name2", "title3", "text3", "password3", "timeID3");
//        MessageController.addMessage("name4", "title4", "text4", "password4", "timeID4");
//    }
//
//    @Test
//    public void setMessageListメソッドのテスト() throws Exception {
//        List<Message> list = new ArrayList<>();
//        Message m = new Message();
//        m.setMessageID(1);
//        m.setPassword("password1");
//        m.setName("name1");
//        m.setTitle("title1");
//        m.setText("text1");
//        m.setDate("2017/5/9 16:34");
//        list.add(m);
//        MessageController.setMessageList(list);
//
//        assertThat(MessageController.size(), is(1));
//    }
//
//    @Test
//    public void addMessageメソッドのテスト() {
//        MessageController.addMessage("name5", "title5", "text5", "password5", "timeID5");
//
//        assertThat(MessageController.size(), is(5));
//    }
//
//    @Test
//    public void getNowDateメソッドのテスト() {
//        LocalDateTime ldt = LocalDateTime.now();
//        String now = String.valueOf(ldt.getYear()) + "/" + ldt.getMonthValue() + "/" + ldt.getDayOfMonth() +
//                " " + ldt.getHour() + ":" + ldt.getMinute();
//
//        assertThat(MessageController.getNowDate(), is(now));
//    }
//
//    @Test
//    public void getAllMessageメソッドのテスト() {
//        List<Message> sut = MessageController.getAllMessage();
//
//        assertThat(sut.size(), is(4));
//        assertThat(sut.get(0).getName(), is("name1"));
//        assertThat(sut.get(1).getName(), is("name2"));
//        assertThat(sut.get(2).getName(), is("name2"));
//        assertThat(sut.get(3).getName(), is("name4"));
//    }
//
//    @Test
//    public void findSameNameMessageメソッドのテスト() {
//        List<Message> list = MessageController.findMessageByName("name2");
//
//        assertThat(list.size(), is(2));
//    }
//
//    @Test
//    public void deleteMessageメソッドのテスト() {
//        assertThat(MessageController.deleteMessage(2, "password2"), is(true));
//        assertThat(MessageController.size(), is(3));
//        assertThat(MessageController.getNextMessageID(), is(5));
//    }
//
//    @Test
//    public void 未登録状態でgetNameメソッドを呼び出す() {
//        MessageController.setMessageList(new ArrayList<>());
//        MessageController.resetNextMessageID();
//
//        List<Message> messageList = MessageController.getAllMessage();
//        assertThat(messageList.size(), is(0));
//
//        assertThat(MessageController.getName(0), is(nullValue()));
//
//        assertThat(MessageController.getNextMessageID(), is(1));
//    }
//
//    @Test(expected = NullPointerException.class)
//    public void setMessageListメソッドにnullを渡す() {
//        MessageController.setMessageList(null);
//    }
}
