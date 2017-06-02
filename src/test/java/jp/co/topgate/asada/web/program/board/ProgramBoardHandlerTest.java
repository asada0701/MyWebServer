package jp.co.topgate.asada.web.program.board;

import jp.co.topgate.asada.web.RequestMessage;
import jp.co.topgate.asada.web.ResponseMessage;
import jp.co.topgate.asada.web.program.board.model.Message;
import jp.co.topgate.asada.web.program.board.model.MessageController;
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
import static org.hamcrest.core.IsNull.nullValue;
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
        private ProgramBoardHandler programBoardHandler;

        @Before
        public void setUp() throws Exception {
            outputStream = new FileOutputStream(new File(responseMessagePath));
            responseMessage = new ResponseMessage(outputStream);
            programBoardHandler = new ProgramBoardHandler(requestMessage, responseMessage);

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

            MessageController.setMessageList(messageList);
        }

        @Test
        public void indexをGETしてみる() throws Exception {
            String method = "GET";
            String uri = "/program/board/index.html";
            Map<String, String> headerField = new HashMap<>();
            headerField.put("hoge", "hogehoge");
            requestMessage = new RequestMessage(method, uri, null, headerField, null);

            programBoardHandler.doGet(requestMessage, responseMessage, "timeIdOfValue");

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

            programBoardHandler.doGet(requestMessage, responseMessage, "timeIdOfValue");

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

            programBoardHandler.doGet(requestMessage, responseMessage, "timeIdOfValue");

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

            programBoardHandler.doGet(requestMessage, responseMessage, "timeIdOfValue");

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
        private static final String badRequestPath = "./src/test/resources/response/BadRequest.txt";
        private RequestMessage requestMessage;
        private FileOutputStream outputStream = null;
        private ResponseMessage responseMessage;
        private ProgramBoardHandler programBoardHandler;

        @Before
        public void setUp() throws Exception {
            outputStream = new FileOutputStream(new File(responseMessagePath));
            responseMessage = new ResponseMessage(outputStream);
            programBoardHandler = new ProgramBoardHandler(requestMessage, responseMessage);

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

            MessageController.setMessageList(messageList);
        }

        @Test(expected = NullPointerException.class)
        public void nullチェック() throws Exception {
            programBoardHandler.doPost(null, null, null);
        }

        @Test(expected = NullPointerException.class)
        public void nullチェック2() throws Exception {
            programBoardHandler.doPost(new RequestMessage(null, null, null), null, "timeID");
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

            programBoardHandler.doPost(requestMessage, responseMessage, "timeIdOfValue");

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

            programBoardHandler.doPost(requestMessage, responseMessage, "timeIdOfValue");

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

            programBoardHandler.doPost(requestMessage, responseMessage, "timeIdOfValue");

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

        @Test
        public void paramが予期しないものの場合() throws Exception {
            try {
                String method = "POST";
                String uri = "/program/board/";
                Map<String, String> headerField = new HashMap<>();
                headerField.put("Content-Type", "application/x-www-form-urlencoded");
                headerField.put("Content-Length", "12");
                byte[] messageBody = "param%3dhoge".getBytes();
                requestMessage = new RequestMessage(method, uri, null, headerField, messageBody);

                programBoardHandler.doPost(requestMessage, responseMessage, "timeIdOfValue");
            } finally {
                outputStream.close();
            }

            try (BufferedReader responseMessage = new BufferedReader(new FileReader(new File(responseMessagePath)));
                 BufferedReader testData = new BufferedReader(new FileReader(new File("./src/test/resources/response/InternalServerError.txt")))) {

                String str;
                while ((str = responseMessage.readLine()) != null) {
                    assertThat(str, is(testData.readLine()));
                }
            }
        }

        @Test
        public void paramがメッセージボディに含まれていない場合() throws Exception {
            try {
                String method = "POST";
                String uri = "/program/board/";
                Map<String, String> headerField = new HashMap<>();
                headerField.put("Content-Type", "application/x-www-form-urlencoded");
                headerField.put("Content-Length", "11");
                byte[] messageBody = "hoge%3dhoge".getBytes();
                requestMessage = new RequestMessage(method, uri, null, headerField, messageBody);

                programBoardHandler.doPost(requestMessage, responseMessage, "timeIdOfValue");
            } finally {
                outputStream.close();
            }

            try (BufferedReader responseMessage = new BufferedReader(new FileReader(new File(responseMessagePath)));
                 BufferedReader testData = new BufferedReader(new FileReader(new File(badRequestPath)))) {

                String str;
                while ((str = responseMessage.readLine()) != null) {
                    assertThat(str, is(testData.readLine()));
                }
            }
        }

        @Test
        public void メッセージボディがリクエストに含まれていない場合() throws Exception {
            try {
                String method = "POST";
                String uri = "/program/board/";
                Map<String, String> headerField = new HashMap<>();
                headerField.put("Content-Type", "application/x-www-form-urlencoded");
                headerField.put("Content-Length", "11");
                requestMessage = new RequestMessage(method, uri, null, headerField, null);

                programBoardHandler.doPost(requestMessage, responseMessage, "timeIdOfValue");
            } finally {
                outputStream.close();
            }

            try (BufferedReader responseMessage = new BufferedReader(new FileReader(new File(responseMessagePath)));
                 BufferedReader testData = new BufferedReader(new FileReader(new File(badRequestPath)))) {

                String str;
                while ((str = responseMessage.readLine()) != null) {
                    assertThat(str, is(testData.readLine()));
                }
            }
        }
    }
}
