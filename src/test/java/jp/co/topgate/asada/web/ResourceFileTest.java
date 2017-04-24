package jp.co.topgate.asada.web;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by yusuke-pc on 2017/04/20.
 */
@RunWith(Enclosed.class)
public class ResourceFileTest {
    public static class コンストラクタのテスト {
        @Test(expected = NullPointerException.class)
        public void nullチェック() {
            ResourceFile sut = new ResourceFile(null);
        }

        @Test
        public void 正しく動作するかテスト() {
            ResourceFile sut = new ResourceFile("./src/test/resources/requestMessage.txt");
            assertThat(sut.getContentType(), is("text/plain"));
        }
    }

    public static class getContentTypeメソッドのテスト {
        @Test
        public void 登録されていないファイルを指定してみる() {
            ResourceFile sut = new ResourceFile("./src/main/resources/music/sample.mp3");
            assertThat(sut.getContentType(), is("application/octet-stream"));
        }

        @Test
        public void txtファイルを指定してみる() {
            ResourceFile sut = new ResourceFile("./src/test/resources/empty.txt");
            assertThat(sut.getContentType(), is("text/plain"));
        }

        @Test
        public void htmlファイルを指定してみる() {
            ResourceFile sut = new ResourceFile("./src/test/resources/empty.html");
            assertThat(sut.getContentType(), is("text/html"));
        }
    }
}
