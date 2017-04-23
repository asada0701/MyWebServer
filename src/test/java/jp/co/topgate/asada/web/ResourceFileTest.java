package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.exception.ResourceFileException;
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
        @Test
        public void nullチェック() {
            try {
                ResourceFile sut = new ResourceFile(null);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }

        @Test
        public void 存在しないファイルを指定してみる() {
            try {
                ResourceFile sut = new ResourceFile("./video/sample.mp4");
            } catch (ResourceFileException e) {
                assertThat(e.getMessage(), is("存在しないファイルかもしくはディレクトリを指定されました"));
            }
        }

        @Test
        public void ディレクトリを指定してみる() {
            try {
                ResourceFile sut = new ResourceFile("./video");
            } catch (ResourceFileException e) {
                assertThat(e.getMessage(), is("存在しないファイルかもしくはディレクトリを指定されました"));
            }
        }

        @Test
        public void 正しく動作するかテスト() {
            ResourceFile sut = new ResourceFile("./src/test/resources/requestMessage.txt");
            assertThat(sut.getContentType(), is("text/plain"));
        }
    }

    public static class isRegisteredメソッドのテスト {
        @Test
        public void 登録されていない拡張子のファイルのテスト() {
            ResourceFile sut = new ResourceFile("./src/main/resources/music/sample.mp3");
            assertThat(sut.isRegistered(), is(false));
        }

        @Test
        public void 登録済みの拡張子のファイルのテスト() {
            ResourceFile sut = new ResourceFile("./src/test/resources/requestMessage.txt");
            assertThat(sut.isRegistered(), is(true));
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
