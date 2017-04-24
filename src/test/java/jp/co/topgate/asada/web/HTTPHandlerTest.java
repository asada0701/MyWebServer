package jp.co.topgate.asada.web;

import org.junit.Test;

import java.io.*;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by yusuke-pc on 2017/04/13.
 */
public class HTTPHandlerTest {
    @Test
    public void ステータスコード200のテスト() throws Exception {
        //Set Up
        File file = new File("./src/text/resources/responseMessage.txt");
        file.delete();


        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(new File("./src/test/resources/requestMessage.txt"));
            os = new FileOutputStream(new File("./src/test/resources/responseMessage.txt"));

            //Exercise
            new HttpHandler(is, os);

        } finally {
            if (is != null) {
                is.close();
            }
            if (os != null) {
                os.close();
            }
        }

        //Verify
        try {
            is = new FileInputStream(new File("./src/test/resources/responseMessage.txt"));
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            assertThat(br.readLine(), is("HTTP/1.1 200 OK"));
            assertThat(br.readLine(), is("Content-Type: text/html"));
            assertThat(br.readLine(), is(""));
            assertThat(br.readLine(), is("<!DOCTYPE html>"));
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    @Test
    public void 存在しないファイルを指定してみる() throws Exception {
        //Set Up
        File file = new File("./src/text/resources/responseMessage.txt");
        file.delete();


        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(new File("./src/test/resources/NotExistTest.txt"));
            os = new FileOutputStream(new File("./src/test/resources/responseMessage.txt"));

            //Exercise
            new HttpHandler(is, os);

        } finally {
            if (is != null) {
                is.close();
            }
            if (os != null) {
                os.close();
            }
        }

        //Verify
        try {
            is = new FileInputStream(new File("./src/test/resources/responseMessage.txt"));
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            assertThat(br.readLine(), is("HTTP/1.1 404 Not Found"));
            assertThat(br.readLine(), is("Content-Type: text/html; charset=UTF-8"));
            assertThat(br.readLine(), is(""));
            assertThat(br.readLine(), is("<html><head><title>404 Not Found</title></head>" +
                    "<body><h1>Not Found</h1>" +
                    "<p>お探しのページは見つかりませんでした。</p></body></html>"));
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    @Test
    public void ディレクトリを指定してみる() throws Exception {
        //Set Up
        File file = new File("./src/text/resources/responseMessage.txt");
        file.delete();


        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(new File("./src/test/resources/NotExistTest.txt"));
            os = new FileOutputStream(new File("./src/test/resources/responseMessage.txt"));

            //Exercise
            new HttpHandler(is, os);

        } finally {
            if (is != null) {
                is.close();
            }
            if (os != null) {
                os.close();
            }
        }

        //Verify
        try {
            is = new FileInputStream(new File("./src/test/resources/responseMessage.txt"));
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            assertThat(br.readLine(), is("HTTP/1.1 404 Not Found"));
            assertThat(br.readLine(), is("Content-Type: text/html; charset=UTF-8"));
            assertThat(br.readLine(), is(""));
            assertThat(br.readLine(), is("<html><head><title>404 Not Found</title></head>" +
                    "<body><h1>Not Found</h1>" +
                    "<p>お探しのページは見つかりませんでした。</p></body></html>"));
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    @Test
    public void 実装されていないHTTPのメソッドを指定してみる() throws Exception {
        //Set Up
        File file = new File("./src/text/resources/responseMessage.txt");
        file.delete();


        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(new File("./src/test/resources/HTTPMethodTest.txt"));
            os = new FileOutputStream(new File("./src/test/resources/responseMessage.txt"));

            //Exercise
            new HttpHandler(is, os);

        } finally {
            if (is != null) {
                is.close();
            }
            if (os != null) {
                os.close();
            }
        }

        //Verify
        try {
            is = new FileInputStream(new File("./src/test/resources/responseMessage.txt"));
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            assertThat(br.readLine(), is("HTTP/1.1 501 Not Implemented"));
            assertThat(br.readLine(), is("Content-Type: text/html; charset=UTF-8"));
            assertThat(br.readLine(), is(""));
            assertThat(br.readLine(), is("<html><head><title>501 Not Implemented</title></head>" +
                    "<body><h1>Not Implemented</h1>" +
                    "<p>Webサーバーでメソッドが実装されていません。</p></body></html>"));
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    @Test
    public void 予期されていないHTTPプロトコルバージョンを指定してみる() throws Exception {
        //Set Up
        File file = new File("./src/text/resources/responseMessage.txt");
        file.delete();


        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(new File("./src/test/resources/HTTPVersionTest.txt"));
            os = new FileOutputStream(new File("./src/test/resources/responseMessage.txt"));

            //Exercise
            new HttpHandler(is, os);

        } finally {
            if (is != null) {
                is.close();
            }
            if (os != null) {
                os.close();
            }
        }

        //Verify
        try {
            is = new FileInputStream(new File("./src/test/resources/responseMessage.txt"));
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            assertThat(br.readLine(), is("HTTP/1.1 505 HTTP Version Not Supported"));
            assertThat(br.readLine(), is("Content-Type: text/html; charset=UTF-8"));
            assertThat(br.readLine(), is(""));
            assertThat(br.readLine(), is("<html><head><title>505 HTTP Version Not Supported</title></head>" +
                    "<body><h1>HTTP Version Not Supported</h1></body></html>"));
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    @Test
    public void パースに失敗してみる() throws Exception {
        //Set Up
        File file = new File("./src/text/resources/responseMessage.txt");
        file.delete();


        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(new File("./src/test/resources/empty.txt"));
            os = new FileOutputStream(new File("./src/test/resources/responseMessage.txt"));

            //Exercise
            new HttpHandler(is, os);

        } finally {
            if (is != null) {
                is.close();
            }
            if (os != null) {
                os.close();
            }
        }

        //Verify
        try {
            is = new FileInputStream(new File("./src/test/resources/responseMessage.txt"));
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            assertThat(br.readLine(), is("HTTP/1.1 400 Bad Request"));
            assertThat(br.readLine(), is("Content-Type: text/html; charset=UTF-8"));
            assertThat(br.readLine(), is(""));
            assertThat(br.readLine(), is("<html><head><title>400 Bad Request</title></head>" +
                    "<body><h1>Bad Request</h1>" +
                    "<p>Your browser sent a request that this server could not understand.<br /></p></body></html>"));
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }
}
