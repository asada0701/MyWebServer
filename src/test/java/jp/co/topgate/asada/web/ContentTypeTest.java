package jp.co.topgate.asada.web;

import org.junit.Test;

import static jp.co.topgate.asada.web.ContentType.getContentType;
import static jp.co.topgate.asada.web.ContentType.getHtmlType;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * ContentTypeクラスをテストする
 *
 * @author asada
 */
public class ContentTypeTest {
    @Test
    public void getHtmlTypeメソッドのテスト() {
        assertThat(getHtmlType(), is("text/html; charset=UTF-8"));
    }

    @Test(expected = NullPointerException.class)
    public void nullチェック() {
        getContentType(null);
    }

    @Test
    public void デフォルトの値が返ってくるテスト() {
        assertThat(getContentType(""), is("application/octet-stream"));

        assertThat(getContentType("hoge"), is("application/octet-stream"));

        assertThat(getContentType("/////////index.html/////////////"), is("application/octet-stream"));

        assertThat(getContentType("hoge.html/"), is("application/octet-stream"));

        assertThat(getContentType("sample.mp4"), is("application/octet-stream"));
    }

    @Test
    public void 正しく動作するかテスト() {
        assertThat(getContentType("////////////////index.html"), is("text/html; charset=UTF-8"));

        assertThat(getContentType("//////日本語////////日本語.html"), is("text/html; charset=UTF-8"));

        assertThat(getContentType("html"), is("text/html; charset=UTF-8"));
    }

    @Test
    public void コンテンツタイプの網羅テスト() {
        assertThat(getContentType("/index.html"), is("text/html; charset=UTF-8"));

        assertThat(getContentType("/index.html"), is("text/html; charset=UTF-8"));

        assertThat(getContentType("/css/index.css"), is("text/css"));

        assertThat(getContentType("/javascript/sample.js"), is("application/javascript"));

        assertThat(getContentType("./src/main/resources/sample.txt"), is("text/plain"));

        assertThat(getContentType("sample.jpg"), is("image/jpg"));

        assertThat(getContentType("sample.jpeg"), is("image/jpeg"));

        assertThat(getContentType("sample.png"), is("image/png"));

        assertThat(getContentType("sample.gif"), is("image/gif"));
    }
}
