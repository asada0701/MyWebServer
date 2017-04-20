package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.exception.FileNotRegisteredRuntimeException;
import jp.co.topgate.asada.web.exception.ResourceFileRuntimeException;
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
    public static class addFileTypeメソッドのテスト {
        @Test
        public void 拡張子とコンテンツタイプを追加してみる() {
            ResourceFile sut;
            String path = "./src/main/resources/music/sample.mp3";
            if (!ResourceFile.isRegistered(path)) {
                ResourceFile.addFileType("mp3", "audio/mp3");
                assertThat(ResourceFile.isRegistered(path), is(true));
            }
            sut = new ResourceFile(path);
            assertThat(sut.getContentType(), is("audio/mp3"));
        }
    }

    public static class コンストラクタのテスト {
        @Test
        public void 登録されていない拡張子のファイルを指定してみる() {
            try {
                ResourceFile sut = new ResourceFile("./src/main/resources/music/sample.mp3");
            } catch (FileNotRegisteredRuntimeException e) {
                assertThat(e.getMessage(), is("ResourceFileクラスに登録されていない拡張子のファイルです"));
            }
        }

        @Test
        public void 存在しないファイルを指定してみる() {
            try {
                ResourceFile sut = new ResourceFile("./video/sample.mp4");
            } catch (ResourceFileRuntimeException e) {
                assertThat(e.getMessage(), is("存在しないファイルかもしくはディレクトリを指定されました"));
            }
        }
    }

    public static class getContentTypeメソッドのテスト {
        @Test
        public void txtファイルを指定してみる() {
            ResourceFile sut = new ResourceFile("./src/test/resources/empty.txt");
            assertThat(sut.getContentType(), is("text/plain"));
        }
    }
}
