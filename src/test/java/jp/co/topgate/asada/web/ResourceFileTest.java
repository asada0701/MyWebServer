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
    public static class 拡張子とコンテンツタイプを追加してみる{
        @Test
        public void 拡張子とコンテンツタイプを追加してみる() {
            ResourceFile sut;
            String path = "./src/main/resources/music/sample.mp3";
            if(!ResourceFile.isRegistered(path)){
                ResourceFile.addFileType("mp3", "audio/mp3");
                assertThat(ResourceFile.isRegistered(path), is(true));
            }
            sut = new ResourceFile(path);
            assertThat(sut.getContentType(), is("audio/mp3"));
        }
    }
    public static class getContentTypeメソッドのテスト{
        @Test
        public void txtファイルを指定してみる() {
            ResourceFile sut = new ResourceFile("./src/test/resources/empty.txt");
            assertThat(sut.getContentType(), is("text/plain"));
        }
    }

    public static class コンストラクタのテスト{
        @Test(expected = FileNotRegisteredRuntimeException.class)
        public void 登録されていない拡張子のファイルを指定してみる(){
            ResourceFile sut = new ResourceFile("./src/main/resources/music/sample.mp3");
        }
        @Test(expected = ResourceFileRuntimeException.class)
        public void 存在しないファイルを指定してみる() {
            ResourceFile sut = new ResourceFile("./video/sample.mp4");
        }
    }


    public static class 存在するかnewする前に調べる{
        @Test
        public void 存在するかnewする前に調べる(){
            assertThat(ResourceFile.isRegistered("./src/main/resources/music/sample.mp3"), is(false));
        }
    }
}
