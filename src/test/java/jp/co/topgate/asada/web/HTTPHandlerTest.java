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
    public void requestComesのテスト() throws Exception{
        //Set Up
        File file = new File("./src/text/resources/responseMessage.txt");
        file.delete();

        HTTPHandler httpHandler = new HTTPHandler();
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(new File("./src/test/resources/requestMessage.txt"));
            os = new FileOutputStream(new File("./src/test/resources/responseMessage.txt"));

        //Exercise
            httpHandler.requestComes(is, os);

        } finally {
            if(is != null){
                is.close();
            }
            if(os != null){
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
            if(is != null){
                is.close();
            }
        }
    }
}
