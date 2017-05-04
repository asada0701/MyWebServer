package jp.co.topgate.asada.web;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import java.io.*;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by yusuke-pc on 2017/04/13.
 */
@RunWith(Enclosed.class)
public class RequestMessageTest {
    public static class ヘッダーフィルードのテスト {
        RequestMessage sut;

        @Before
        public void setUp() throws Exception {
            String path = "./src/test/resources/requestMessage.txt";
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(path)))) {
                bis.mark(bis.available());
                RequestLine rl = new RequestLine(bis);
                bis.reset();
                sut = new RequestMessage(bis, rl);
            }
        }

        @Test
        public void nullチェック() throws Exception {
            assertThat(sut.findHeaderByName(null), is(nullValue()));
        }

        @Test
        public void 正しく値を得られるか() throws Exception {
            assertThat(sut.findHeaderByName("Host"), is("localhost:8080"));
        }
    }

    public static class メッセージボディのテスト {
        RequestMessage sut;

        @Before
        public void setUp() throws Exception {
            String path = "./src/test/resources/PostRequestMessage.txt";
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(new File(path)))) {
                bis.mark(bis.available());
                RequestLine rl = new RequestLine(bis);
                bis.reset();
                sut = new RequestMessage(bis, rl);
            }
        }

        @Test
        public void nullチェック() throws Exception {
            assertThat(sut.findMessageBody(null), is(nullValue()));
        }

        @Test
        public void ないものを要求してみる() throws Exception {
            assertThat(sut.findHeaderByName("email"), is(nullValue()));
        }

        @Test
        public void 正しく値を得られるか() throws Exception {
            assertThat(sut.findMessageBody("name"), is("asada"));
            assertThat(sut.findMessageBody("like"), is("cat"));
        }
    }
}
