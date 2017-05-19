package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.app.StatusLine;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.io.*;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

/**
 * StaticHandlerクラスのテスト
 *
 * @author asada
 */
@RunWith(Enclosed.class)
public class StaticHandlerTest {
    public static class コンストラクタのテスト {

        @Test
        public void nullチェック() throws Exception {
            StaticHandler sut = new StaticHandler(null);
            assertThat(sut.getRequestMessage(), is(nullValue()));
        }

        @Test
        public void 正しく動作するか() throws Exception {
            String path = "./src/test/resources/GetRequestMessage.txt";
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(path)))) {
                RequestMessage rm = new RequestMessage(bis);
                StaticHandler sut = new StaticHandler(rm);
                assertThat(sut.getRequestMessage(), is(rm));
            }
        }
    }

    @Test
    public void doRequestProcessのテスト() throws Exception {
        String path = "./src/test/resources/GetRequestMessage.txt";
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(path)))) {
            RequestMessage rm = new RequestMessage(bis);
            StaticHandler sut = new StaticHandler(rm);
            assertThat(sut.doRequestProcess(), is(StatusLine.OK));
        }
    }

    @Test
    public void doResponseProcessのテスト() throws Exception {
        String path = "./src/test/resources/responseMessage.txt";
        try (FileOutputStream fos = new FileOutputStream(path);
             FileInputStream is = new FileInputStream(new File("./src/test/resources/GetRequestMessage.txt"))) {

            RequestMessage rm = new RequestMessage(is);
            StaticHandler sut = new StaticHandler(rm);
            StatusLine sl = sut.doRequestProcess();

            sut.doResponseProcess(fos, sl);
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path))))) {

            assertThat(br.readLine(), is("HTTP/1.1 200 OK"));
            assertThat(br.readLine(), is("Content-Type: text/html; charset=UTF-8"));
            assertThat(br.readLine(), is("Content-Length: 714"));
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
