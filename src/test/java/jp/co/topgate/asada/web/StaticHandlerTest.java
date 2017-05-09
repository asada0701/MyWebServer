package jp.co.topgate.asada.web;

/**
 * Created by yusuke-pc on 2017/05/09.
 */

import org.junit.Test;

import java.io.*;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class StaticHandlerTest {
    @Test
    public void returnResponseメソッドのテスト() throws Exception {

        try (OutputStream os = new FileOutputStream(new File("./src/test/resources/responseMessage.txt"));
             InputStream is = new FileInputStream(new File("./src/test/resources/requestMessage.txt"));
             BufferedInputStream bis = new BufferedInputStream(is)) {

            Handler sut = new StaticHandler();
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
            assertThat(br.readLine(), is("<head>"));
            assertThat(br.readLine(), is("    <meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">"));
            assertThat(br.readLine(), is("    <link rel=\"stylesheet\" type=\"text/css\" href=\"./css/style.css\">"));
            assertThat(br.readLine(), is("    <script type=\"text/javascript\" src=\"./js/myjs.js\"></script>"));
            assertThat(br.readLine(), is("</head>"));
            assertThat(br.readLine(), is("<body>"));
            assertThat(br.readLine(), is("<center>"));
            assertThat(br.readLine(), is("    <div id=\"header\">"));
            assertThat(br.readLine(), is("        <h1>こんにちは</h1>"));
            assertThat(br.readLine(), is("        <p>"));
            assertThat(br.readLine(), is("            <script>"));
            assertThat(br.readLine(), is("            koshin();"));
            assertThat(br.readLine(), is("            </script>"));
            assertThat(br.readLine(), is("        </p>"));
            assertThat(br.readLine(), is("    </div>"));
            assertThat(br.readLine(), is("    <div id=\"gazou\">"));
            assertThat(br.readLine(), is("        <p>"));
            assertThat(br.readLine(), is("            <img src=\"./img/s_1.jpg\" width=\"200\" height=\"180\" alt=\"猫\"/>"));
            assertThat(br.readLine(), is("            <img src=\"./img/loading-loop.gif\" width=\"200\" height=\"180\"/>"));
            assertThat(br.readLine(), is("            <img src=\"./img/s_pet_neko.png\" width=\"200\" height=\"180\"/>"));
            assertThat(br.readLine(), is("        </p>"));
            assertThat(br.readLine(), is("    </div>"));
            assertThat(br.readLine(), is("</center>"));
            assertThat(br.readLine(), is("</body>"));
            assertThat(br.readLine(), is("</html>"));
            assertThat(br.readLine(), is(nullValue()));
        }
    }
}
