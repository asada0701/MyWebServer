package jp.co.topgate.asada.web;

import org.junit.Test;

import java.io.*;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by yusuke-pc on 2017/04/13.
 */
public class ResponseMessageTest {
    static ResponseMessage sut = null;

    @Test
    public void プロトコルバージョンのテスト() {
        sut = new ResponseMessage();
        sut.setProtocolVersion("HTTP/1.1");
        assertThat("HTTP/1.1", is(sut.getProtocolVersion()));
    }

    @Test
    public void ヘッダーボディのテスト() {
        sut = new ResponseMessage();
        sut.addHeader("Date", "Thu,13 Api 2017 18:33:23 GMT");
        sut.addHeader("Server", "mywebserver/1.0");
        assertThat(sut.getHeaderField().get(0), is("Date: Thu,13 Api 2017 18:33:23 GMT"));
        assertThat(sut.getHeaderField().get(1), is("Server: mywebserver/1.0"));
    }

    @Test
    public void メッセージボディのテスト() throws Exception{
        sut = new ResponseMessage();
        File testFile = new File("./src/test/resources/responseMessage.txt");
        OutputStream os = new FileOutputStream(testFile);
        ResourceFile rf = new ResourceFile("./src/test/resources/requestMessage.txt");
        sut.setMessageBody(rf);
        assertThat(sut.getMessageBody(), is(notNullValue()));
        sut.returnResponse(os,200, rf);
    }

//    @Test
//    public void レスポンスメッセージの生成テスト() throws Exception {
//        sut = new ResponseMessage();
//        OutputStream os;
//        File resources = new File();
//        ResourceFileType rft = new ResourceFileType();
//        sut.returnResponse(os, 200, resources, rft);
//        File file = new File("./src/test/resources/responseMessage.txt");
//        if (file.exists()) {
//            file.delete();
//        }
//        FileOutputStream fos = new FileOutputStream(file);
//        ResourceFileType rft = new ResourceFileType("/index.html");
//        sut.addHeader("Content-Type", "text/html");
//        sut.returnResponse(fos, 200, new File("./src/main/java/resources/index.html"), rft);
//        fos.close();
//
//        InputStream is = new FileInputStream(file);
//        BufferedReader br = new BufferedReader(new InputStreamReader(is));
//        assertThat(br.readLine(), is("HTTP/1.1 200 OK"));
//        assertThat(br.readLine(), is("Content-Type: text/html"));
//        assertThat(br.readLine(), is(""));
//        assertThat(br.readLine(), is("<!DOCTYPE html>" +
//                "<html>" +
//                "<head>" +
//                "  <meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">" +
//                "  <link rel=\"stylesheet\" type=\"text/css\" href=\"./css/style.css\">" +
//                "  <script type=\"text/javascript\" src=\"./js/myjs.js\"></script>" +
//                "</head>" +
//                "<body>" +
//                "  <div id=\"header\">" +
//                "    <h1>こんにちは</h1>" +
//                "    <p>" +
//                "      <script>" +
//                "        koshin();" +
//                "      </script>" +
//                "    </p>" +
//                "  </div>" +
//                "  <div id=\"gazou\">" +
//                "    <p>私の好きな猫の画像です<img src=\"./img/1.jpg\" width=\"400\" height=\"360\" alt=\"猫\" /></p>" +
//                "  </div>" +
//                "  <!--<div id=\"douga\">-->" +
//                "    <!--<p>こちらは好きな動画になります。-->" +
//                "    <!--<video src=\"./video/cat.mp3\" controls>-->" +
//                "    <!--</video>-->" +
//                "  <!--</div>-->" +
//                "</body>" +
//                "</html>"));
//    }
}
