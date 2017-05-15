package jp.co.topgate.asada.web;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by yusuke-pc on 2017/05/03.
 */
public class ContentTypeTest {
    @Test
    public void nullチェック() {
        ContentType sut = new ContentType(null);
        assertThat(sut.getContentType(), is("application/octet-stream"));
    }

    @Test
    public void 空文字テスト() {
        ContentType sut = new ContentType("");
        assertThat(sut.getContentType(), is("application/octet-stream"));
    }

    @Test
    public void 正しくコンテンツタイプを取得できるか() {
        ContentType sut = new ContentType("/index.html");
        assertThat(sut.getContentType(), is("text/html; charset=UTF-8"));
    }
}
