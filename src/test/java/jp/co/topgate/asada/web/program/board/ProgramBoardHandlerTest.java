package jp.co.topgate.asada.web.program.board;

import jp.co.topgate.asada.web.RequestMessage;
import jp.co.topgate.asada.web.ResponseMessage;
import jp.co.topgate.asada.web.exception.IllegalRequestException;
import jp.co.topgate.asada.web.program.board.model.Message;
import jp.co.topgate.asada.web.program.board.model.ModelController;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * ProgramBoardHandlerクラスのテスト
 *
 * @author asada
 */
@RunWith(Enclosed.class)
public class ProgramBoardHandlerTest {
    public static class コンストラクタのテスト {
        @Test
        public void nullチェック() throws Exception {
            ProgramBoardHandler sut = new ProgramBoardHandler(null, null);
            assertThat(sut.getRequestMessage(), is(nullValue()));
            assertThat(sut.getResponseMessage(), is(nullValue()));
        }

        @Test
        public void コンストラクタ() throws Exception {
            RequestMessage requestMessage = new RequestMessage(null, null, null, null, null);
            ResponseMessage responseMessage = new ResponseMessage(null);
            ProgramBoardHandler sut = new ProgramBoardHandler(requestMessage, responseMessage);

            assertThat(sut.getRequestMessage(), is(requestMessage));
            assertThat(sut.getResponseMessage(), is(responseMessage));
        }
    }

    public static class doGetメソッドのテスト {
        private static final String responseMessagePath = "./src/test/resources/responseMessage.txt";
        private RequestMessage requestMessage;
        private FileOutputStream outputStream = null;
        private ResponseMessage responseMessage;

        @Before
        public void setUp() throws Exception {
            outputStream = new FileOutputStream(new File(responseMessagePath));
            responseMessage = new ResponseMessage(outputStream);

            List<Message> messageList = new ArrayList<>();
            Message m;
            m = new Message();
            m.setMessageID(1);
            m.setPassword(new BCryptPasswordEncoder().encode("test"));
            m.setName("管理者");
            m.setTitle("test");
            m.setText("こんにちは");
            m.setDate("2017/5/11 11:56");
            messageList.add(m);

            m = new Message();
            m.setMessageID(2);
            m.setPassword(new BCryptPasswordEncoder().encode("t"));
            m.setName("asada");
            m.setTitle("t");
            m.setText("こんにちは");
            m.setDate("2017/5/11 11:57");
            messageList.add(m);

            ModelController.setMessageList(messageList);
        }

        @Test
        public void indexをGETしてみる() throws Exception {
            String method = "GET";
            String uri = "/program/board/index.html";
            Map<String, String> headerField = new HashMap<>();
            headerField.put("hoge", "hogehoge");
            requestMessage = new RequestMessage(method, uri, null, headerField, null);

            ProgramBoardHandler.doGet(requestMessage, responseMessage, "timeIdOfValue");

            outputStream.close();

            //Verify
            try (BufferedReader br1 = new BufferedReader(new FileReader(new File(responseMessagePath)));
                 BufferedReader br2 = new BufferedReader(new FileReader(new File("./src/test/resources/response/GetProgramBoard.txt")))) {

                String str;
                while ((str = br1.readLine()) != null) {
                    assertThat(str, is(br2.readLine()));
                }
            }
        }

        @Test
        public void searchをGETしてみる() throws Exception {
            String method = "GET";
            String uri = "/program/board/search.html";
            Map<String, String> uriQuery = new HashMap<>();
            uriQuery.put("param", "search");
            uriQuery.put("name", "管理者");
            requestMessage = new RequestMessage(method, uri, uriQuery);

            ProgramBoardHandler.doGet(requestMessage, responseMessage, "timeIdOfValue");

            outputStream.close();

            //Verify
            try (BufferedReader br1 = new BufferedReader(new FileReader(new File(responseMessagePath)));
                 BufferedReader br2 = new BufferedReader(new FileReader(new File("./src/test/resources/response/GetSearchProgramBoard.txt")))) {

                String str;
                while ((str = br1.readLine()) != null) {
                    assertThat(str, is(br2.readLine()));
                }
            }
        }

        @Test
        public void cssをGETしてみる() throws Exception {
            String method = "GET";
            String uri = "/program/board/css/style.css";
            requestMessage = new RequestMessage(method, uri, null);

            ProgramBoardHandler.doGet(requestMessage, responseMessage, "timeIdOfValue");

            outputStream.close();

            //verify
            try (BufferedReader br1 = new BufferedReader(new FileReader(new File(responseMessagePath)));
                 BufferedReader br2 = new BufferedReader(new FileReader(new File("./src/test/resources/response/GetProgramBoardStyleCss.txt")))) {

                String str;
                while ((str = br1.readLine()) != null) {
                    assertThat(str, is(br2.readLine()));
                }
            }
        }

        @Test
        public void 存在しないファイルをGETしてみる() throws Exception {
            String method = "GET";
            String uri = "/program/board/hoge.txt";
            requestMessage = new RequestMessage(method, uri, null);

            ProgramBoardHandler.doGet(requestMessage, responseMessage, "timeIdOfValue");

            outputStream.close();

            //verify
            try (BufferedReader br1 = new BufferedReader(new FileReader(new File(responseMessagePath)));
                 BufferedReader br2 = new BufferedReader(new FileReader(new File("./src/test/resources/response/NotFound.txt")))) {

                String str;
                while ((str = br1.readLine()) != null) {
                    assertThat(str, is(br2.readLine()));
                }
            }
        }

        @After
        public void tearDown() throws Exception {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

    public static class doPostメソッドのテスト {
        private static final String responseMessagePath = "./src/test/resources/responseMessage.txt";
        private RequestMessage requestMessage;
        private FileOutputStream outputStream = null;
        private ResponseMessage responseMessage;

        @Before
        public void setUp() throws Exception {
            outputStream = new FileOutputStream(new File(responseMessagePath));
            responseMessage = new ResponseMessage(outputStream);

            List<Message> messageList = new ArrayList<>();
            Message m;
            m = new Message();
            m.setMessageID(1);
            m.setPassword(new BCryptPasswordEncoder().encode("test"));
            m.setName("管理者");
            m.setTitle("test");
            m.setText("こんにちは");
            m.setDate("2017/5/11 11:56");
            m.setTimeID("timeID1");
            messageList.add(m);

            m = new Message();
            m.setMessageID(2);
            m.setPassword(new BCryptPasswordEncoder().encode("t"));
            m.setName("asada");
            m.setTitle("t");
            m.setText("こんにちは");
            m.setDate("2017/5/11 11:57");
            m.setTimeID("timeID2");
            messageList.add(m);

            ModelController.setMessageList(messageList);
        }

        @Test(expected = NullPointerException.class)
        public void nullチェック() throws Exception {
            ProgramBoardHandler.doPost(null, null, null);
        }

        @Test(expected = NullPointerException.class)
        public void nullチェック2() throws Exception {
            ProgramBoardHandler.doPost(new RequestMessage(null, null, null), null, "timeID");
        }

        @Test
        public void paramがwriteの場合() throws Exception {
            String method = "POST";
            String uri = "/program/board/";
            Map<String, String> headerField = new HashMap<>();
            headerField.put("Content-Type", "application/x-www-form-urlencoded");
            headerField.put("Content-Length", "132");
            byte[] messageBody = "name%3dasada%26title%3dtest%26text%3d%e3%81%93%e3%82%93%e3%81%ab%e3%81%a1%e3%81%af%26password%3dtest%26param%3dwrite%26timeID%3dtest".getBytes();
            requestMessage = new RequestMessage(method, uri, null, headerField, messageBody);

            ProgramBoardHandler.doPost(requestMessage, responseMessage, "timeIdOfValue");

            //verify
            //メッセージの投稿は、現在時刻を取り扱うため、テストは途中までとする。
            try (BufferedReader br = new BufferedReader(new FileReader(new File(responseMessagePath)))) {
                assertThat(br.readLine(), is("HTTP/1.1 200 OK"));
                assertThat(br.readLine(), is("Content-Type: text/html; charset=UTF-8"));
                assertThat(br.readLine(), is("Content-Length: 5774"));
                assertThat(br.readLine(), is(""));
                assertThat(br.readLine(), is("<!DOCTYPE html>"));
                assertThat(br.readLine(), is("<html>"));
                assertThat(br.readLine(), is(""));
                assertThat(br.readLine(), is("<head>"));
                assertThat(br.readLine(), is("    <meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">"));
                assertThat(br.readLine(), is("    <link rel=\"stylesheet\" type=\"text/css\" href=\"./css/style.css\">"));
                assertThat(br.readLine(), is("</head>"));
                assertThat(br.readLine(), is(""));
                assertThat(br.readLine(), is("<body>"));
                assertThat(br.readLine(), is("<center>"));
                assertThat(br.readLine(), is("    <div id=\"header\">"));
                assertThat(br.readLine(), is("        <h1>掲示板-LightBoard</h1>"));
                assertThat(br.readLine(), is("        <a href=\"./caution.html\" target=\"_blank\">注意事項を読む</a>"));
                assertThat(br.readLine(), is("    </div>"));
                assertThat(br.readLine(), is("    <div id=\"form\">"));
                assertThat(br.readLine(), is("        <form action=\"/program/board/\" method=\"post\">"));
                assertThat(br.readLine(), is("            <p>"));
                assertThat(br.readLine(), is("                名前<input type=\"text\" name=\"name\" size=\"21\" maxlength=\"20\" required>(20文字まで)"));
                assertThat(br.readLine(), is("            </p>"));
                assertThat(br.readLine(), is("            <p>"));
                assertThat(br.readLine(), is("                タイトル<input type=\"text\" name=\"title\" size=\"21\" maxlength=\"20\" required>(20文字まで)"));
                assertThat(br.readLine(), is("            </p>"));
                assertThat(br.readLine(), is("            <p>"));
                assertThat(br.readLine(), is("                メッセージ<br>"));
                assertThat(br.readLine(), is("                <textarea name=\"text\" rows=\"5\" cols=\"42\" maxlength=\"200\" required></textarea>"));
                assertThat(br.readLine(), is("                <br>(200文字まで)"));
                assertThat(br.readLine(), is("            </p>"));
                assertThat(br.readLine(), is("            <p>"));
                assertThat(br.readLine(), is("                パスワード<input type=\"password\" name=\"password\" size=\"21\" maxlength=\"20\" required>(20文字まで)"));
                assertThat(br.readLine(), is("                <br>(投稿した文を削除するときに使います。)"));
                assertThat(br.readLine(), is("            </p>"));
                assertThat(br.readLine(), is("            <input type=\"hidden\" name=\"param\" value=\"write\">"));
                assertThat(br.readLine(), is("            <input type=\"hidden\" name=\"timeID\" value=\"timeIdOfValue\">"));
                assertThat(br.readLine(), is("            <input type=\"submit\" value=\"投稿\">"));
                assertThat(br.readLine(), is("        </form>"));
                assertThat(br.readLine(), is("    </div>"));
                assertThat(br.readLine(), is("    <div id=\"log\">"));
                assertThat(br.readLine(), is("        <table border=\"1\" style=\"table-layout:fixed;width:100%;\">"));
                assertThat(br.readLine(), is("            <colgroup>"));
                assertThat(br.readLine(), is("                <col style=\"width:5%;\">"));
                assertThat(br.readLine(), is("                <col style=\"width:10%;\">"));
                assertThat(br.readLine(), is("                <col style=\"width:30%;\">"));
                assertThat(br.readLine(), is("                <col style=\"width:10%;\">"));
                assertThat(br.readLine(), is("                <col style=\"width:15%;\">"));
                assertThat(br.readLine(), is("                <col style=\"width:20%;\">"));
                assertThat(br.readLine(), is("                <col style=\"width:10%;\">"));
                assertThat(br.readLine(), is("            </colgroup>"));
                assertThat(br.readLine(), is("            <tbody>"));
                assertThat(br.readLine(), is("            <tr>"));
                assertThat(br.readLine(), is("                <th>No</th>"));
                assertThat(br.readLine(), is("                <th>タイトル</th>"));
                assertThat(br.readLine(), is("                <th>本文</th>"));
                assertThat(br.readLine(), is("                <th>ユーザー名</th>"));
                assertThat(br.readLine(), is("                <th>日付</th>"));
                assertThat(br.readLine(), is("                <th></th>"));
                assertThat(br.readLine(), is("                <th></th>"));
                assertThat(br.readLine(), is("            </tr>"));
                assertThat(br.readLine(), is("            <tr id=\"No.3\">"));
                assertThat(br.readLine(), is("                <td align=\"center\" style=\"word-wrap:break-word;\">No.3</td>"));
                assertThat(br.readLine(), is("                <td align=\"center\" style=\"word-wrap:break-word;\">test</td>"));
                assertThat(br.readLine(), is("                <td align=\"center\" style=\"word-wrap:break-word;\">こんにちは</td>"));
                assertThat(br.readLine(), is("                <td align=\"center\" style=\"word-wrap:break-word;\">asada</td>"));
            }

            outputStream.close();
        }

        @Test
        public void paramがdelete_step_1の場合() throws Exception {
            String method = "POST";
            String uri = "/program/board/";
            Map<String, String> headerField = new HashMap<>();
            headerField.put("Content-Type", "application/x-www-form-urlencoded");
            headerField.put("Content-Length", "34");
            byte[] messageBody = "param%3ddelete_step_1%26number%3d1".getBytes();
            requestMessage = new RequestMessage(method, uri, null, headerField, messageBody);

            ProgramBoardHandler.doPost(requestMessage, responseMessage, "timeIdOfValue");

            outputStream.close();

            //verify
            try (BufferedReader br1 = new BufferedReader(new FileReader(new File(responseMessagePath)));
                 BufferedReader br2 = new BufferedReader(new FileReader(new File("./src/test/resources/response/PostDelete1ProgramBoard.txt")))) {

                String str;
                while ((str = br1.readLine()) != null) {
                    assertThat(str, is(br2.readLine()));
                }
            }
        }

        @Test
        public void paramがdelete_step_2の場合() throws Exception {
            String method = "POST";
            String uri = "/program/board/";
            Map<String, String> headerField = new HashMap<>();
            headerField.put("Content-Type", "application/x-www-form-urlencoded");
            headerField.put("Content-Length", "36");
            byte[] messageBody = "password%3dtest%26number%3d1%26param%3ddelete_step_2".getBytes();
            requestMessage = new RequestMessage(method, uri, null, headerField, messageBody);

            ProgramBoardHandler.doPost(requestMessage, responseMessage, "timeIdOfValue");

            outputStream.close();

            //verify
            try (BufferedReader br1 = new BufferedReader(new FileReader(new File(responseMessagePath)));
                 BufferedReader br2 = new BufferedReader(new FileReader(new File("./src/test/resources/response/PostDelete2ProgramBoard.txt")))) {

                String str;
                while ((str = br1.readLine()) != null) {
                    assertThat(str, is(br2.readLine()));
                }
            }
        }

        @Test
        public void paramがbackの場合() throws Exception {
            String method = "POST";
            String uri = "/program/board/";
            Map<String, String> headerField = new HashMap<>();
            headerField.put("Content-Type", "application/x-www-form-urlencoded");
            headerField.put("Content-Length", "12");
            byte[] messageBody = "param%3dback".getBytes();
            requestMessage = new RequestMessage(method, uri, null, headerField, messageBody);

            ProgramBoardHandler.doPost(requestMessage, responseMessage, "timeIdOfValue");

            outputStream.close();

            //verify
            try (BufferedReader br1 = new BufferedReader(new FileReader(new File(responseMessagePath)));
                 BufferedReader br2 = new BufferedReader(new FileReader(new File("./src/test/resources/response/GetProgramBoard.txt")))) {

                String str;
                while ((str = br1.readLine()) != null) {
                    assertThat(str, is(br2.readLine()));
                }
            }
        }

        @Test(expected = IllegalRequestException.class)
        public void paramが予期しないものの場合() throws Exception {
            try {
                String method = "POST";
                String uri = "/program/board/";
                Map<String, String> headerField = new HashMap<>();
                headerField.put("Content-Type", "application/x-www-form-urlencoded");
                headerField.put("Content-Length", "12");
                byte[] messageBody = "param%3dhoge".getBytes();
                requestMessage = new RequestMessage(method, uri, null, headerField, messageBody);

                ProgramBoardHandler.doPost(requestMessage, responseMessage, "timeIdOfValue");
            } finally {
                outputStream.close();
            }
        }

        @Test(expected = IllegalRequestException.class)
        public void paramがメッセージボディに含まれていない場合() throws Exception {
            try {
                String method = "POST";
                String uri = "/program/board/";
                Map<String, String> headerField = new HashMap<>();
                headerField.put("Content-Type", "application/x-www-form-urlencoded");
                headerField.put("Content-Length", "11");
                byte[] messageBody = "hoge%3dhoge".getBytes();
                requestMessage = new RequestMessage(method, uri, null, headerField, messageBody);

                ProgramBoardHandler.doPost(requestMessage, responseMessage, "timeIdOfValue");
            } finally {
                outputStream.close();
            }
        }

        @Test(expected = IllegalRequestException.class)
        public void メッセージボディがリクエストに含まれていない場合() throws Exception {
            try {
                String method = "POST";
                String uri = "/program/board/";
                Map<String, String> headerField = new HashMap<>();
                headerField.put("Content-Type", "application/x-www-form-urlencoded");
                headerField.put("Content-Length", "11");
                requestMessage = new RequestMessage(method, uri, null, headerField, null);

                ProgramBoardHandler.doPost(requestMessage, responseMessage, "timeIdOfValue");
            } finally {
                outputStream.close();
            }
        }
    }
}
