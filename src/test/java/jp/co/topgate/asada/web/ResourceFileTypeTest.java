package jp.co.topgate.asada.web;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by yusuke-pc on 2017/04/17.
 */
public class ResourceFileTypeTest {
    @Test
    public void txtファイルを指定してみる() {
        ResourceFileType sut = null;
        try{
            sut = new ResourceFileType("src/test/java/jp/co/topgate/asada/web/Documents/empty.txt");
        }catch(NullPointerException e){
            e.printStackTrace();
        }
        assertThat(sut.isRegistered(), is(true));
        assertThat(sut.getContentType(), is("text/plain"));
    }

    @Test
    public void 存在しないファイルを指定してみる() {
        ResourceFileType sut = null;
        try{
            sut = new ResourceFileType("/video/sample.mp4");
        }catch(NullPointerException e){
            e.printStackTrace();
        }
        assertThat(sut.isRegistered(), is(false));
        assertThat(sut.getContentType(), is(nullValue()));
    }

    @Test
    public void 拡張子とコンテンツタイプを追加してみる() {
        ResourceFileType sut = new ResourceFileType("/video/sample.mp4");
        sut.addFileType("mp4", "audio/mp4");
        assertThat(sut.isRegistered(), is(true));
        assertThat(sut.getContentType(), is("audio/mp4"));
    }
}
