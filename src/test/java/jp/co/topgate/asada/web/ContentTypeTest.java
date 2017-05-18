package jp.co.topgate.asada.web;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * ContentTypeクラスをテストする
 *
 * @author asada
 */
public class ContentTypeTest {
    @Test(expected = NullPointerException.class)
    public void nullチェック() {
        new ContentType(null);
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
