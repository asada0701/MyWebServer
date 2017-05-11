package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.model.ModelController;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by yusuke-pc on 2017/05/09.
 */
public class WebAppHandlerTest {
    @Before
    public void setUp() {
        CsvWriter.setFilePath("./src/test/resources/data/message.csv");
        ModelController.setMessageList(CsvWriter.readToMessage());
    }

    @Test
    public void GETの場合のreturnResponseメソッド() throws Exception {
        try (OutputStream os = new FileOutputStream(new File("./src/test/resources/responseMessage.txt"));
             InputStream is = new FileInputStream(new File("./src/test/resources/GetProgramBoardTest.txt"));
             BufferedInputStream bis = new BufferedInputStream(is)) {

            Handler sut = new WebAppHandler();
            sut.setStatusCode(200);

            sut.setRequestLine(new RequestLine(bis));
            sut.returnResponse(os);
        }
        try (InputStream is = new FileInputStream(new File("./src/test/resources/responseMessage.txt"));
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

            assertThat(br.readLine(), is("HTTP/1.1 200 OK"));
            assertThat(br.readLine(), is("Content-Type: text/html; charset=UTF-8"));
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
            assertThat(br.readLine(), is("                名前<input type=\"text\" name=\"name\" size=\"40\" required>"));
            assertThat(br.readLine(), is("            </p>"));
            assertThat(br.readLine(), is("            <p>"));
            assertThat(br.readLine(), is("                タイトル<input type=\"text\" name=\"title\" size=\"40\" required>"));
            assertThat(br.readLine(), is("            </p>"));
            assertThat(br.readLine(), is("            <p>"));
            assertThat(br.readLine(), is("                メッセージ<br>"));
            assertThat(br.readLine(), is("                <textarea name=\"text\" rows=\"4\" cols=\"40\" required></textarea>"));
            assertThat(br.readLine(), is("            </p>"));
            assertThat(br.readLine(), is("            <p>"));
            assertThat(br.readLine(), is("                パスワード<input type=\"password\" name=\"password\" size=\"10\" required>(投稿した文を削除するときに使います。)"));
            assertThat(br.readLine(), is("            </p>"));
            assertThat(br.readLine(), is("            <input type=\"hidden\" name=\"param\" value=\"contribution\">"));
            assertThat(br.readLine(), is("            <input type=\"submit\" value=\"投稿\">"));
            assertThat(br.readLine(), is("        </form>"));
            assertThat(br.readLine(), is("    </div>"));
            assertThat(br.readLine(), is("    <div id=\"log\">"));
            assertThat(br.readLine(), is("        <table border=\"1\">"));
            assertThat(br.readLine(), is("            <tr>"));
            assertThat(br.readLine(), is("                <th>ナンバー</th>"));
            assertThat(br.readLine(), is("                <th>タイトル</th>"));
            assertThat(br.readLine(), is("                <th>本文</th>"));
            assertThat(br.readLine(), is("                <th>ユーザー名</th>"));
            assertThat(br.readLine(), is("                <th>日付</th>"));
            assertThat(br.readLine(), is("                <th></th>"));
            assertThat(br.readLine(), is("                <th></th>"));
            assertThat(br.readLine(), is("            </tr>"));
            assertThat(br.readLine(), is("        </table>"));
            assertThat(br.readLine(), is("    </div>"));
            assertThat(br.readLine(), is("</center>"));
            assertThat(br.readLine(), is("</body>"));
            assertThat(br.readLine(), is(""));
            assertThat(br.readLine(), is("</html>"));
            assertThat(br.readLine(), is(nullValue()));
        }
    }

    @Test
    public void POSTの場合のreturnResponseメソッド() throws Exception {
        try (OutputStream os = new FileOutputStream(new File("./src/test/resources/responseMessage.txt"));
             InputStream is = new FileInputStream(new File("./src/test/resources/PostProgramBoardTest.txt"));
             BufferedInputStream bis = new BufferedInputStream(is)) {

            Handler sut = new WebAppHandler();
            sut.setStatusCode(200);

            sut.setRequestLine(new RequestLine(bis));
            sut.returnResponse(os);
        }
        try (InputStream is = new FileInputStream(new File("./src/test/resources/responseMessage.txt"));
             BufferedReader br = new BufferedReader(new InputStreamReader(is))) {

            assertThat(br.readLine(), is("HTTP/1.1 200 OK"));
            assertThat(br.readLine(), is("Content-Type: text/html; charset=UTF-8"));
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
            assertThat(br.readLine(), is("                名前<input type=\"text\" name=\"name\" size=\"40\" required>"));
            assertThat(br.readLine(), is("            </p>"));
            assertThat(br.readLine(), is("            <p>"));
            assertThat(br.readLine(), is("                タイトル<input type=\"text\" name=\"title\" size=\"40\" required>"));
            assertThat(br.readLine(), is("            </p>"));
            assertThat(br.readLine(), is("            <p>"));
            assertThat(br.readLine(), is("                メッセージ<br>"));
            assertThat(br.readLine(), is("                <textarea name=\"text\" rows=\"4\" cols=\"40\" required></textarea>"));
            assertThat(br.readLine(), is("            </p>"));
            assertThat(br.readLine(), is("            <p>"));
            assertThat(br.readLine(), is("                パスワード<input type=\"password\" name=\"password\" size=\"10\" required>(投稿した文を削除するときに使います。)"));
            assertThat(br.readLine(), is("            </p>"));
            assertThat(br.readLine(), is("            <input type=\"hidden\" name=\"param\" value=\"contribution\">"));
            assertThat(br.readLine(), is("            <input type=\"submit\" value=\"投稿\">"));
            assertThat(br.readLine(), is("        </form>"));
            assertThat(br.readLine(), is("    </div>"));
            assertThat(br.readLine(), is("    <div id=\"log\">"));
            assertThat(br.readLine(), is("        <table border=\"1\">"));
            assertThat(br.readLine(), is("            <tr>"));
            assertThat(br.readLine(), is("                <th>ナンバー</th>"));
            assertThat(br.readLine(), is("                <th>タイトル</th>"));
            assertThat(br.readLine(), is("                <th>本文</th>"));
            assertThat(br.readLine(), is("                <th>ユーザー名</th>"));
            assertThat(br.readLine(), is("                <th>日付</th>"));
            assertThat(br.readLine(), is("                <th></th>"));
            assertThat(br.readLine(), is("                <th></th>"));
            assertThat(br.readLine(), is("            </tr>"));
            assertThat(br.readLine(), is("        </table>"));
            assertThat(br.readLine(), is("    </div>"));
            assertThat(br.readLine(), is("</center>"));
            assertThat(br.readLine(), is("</body>"));
            assertThat(br.readLine(), is(""));
            assertThat(br.readLine(), is("</html>"));
            assertThat(br.readLine(), is(nullValue()));
        }
    }
}
