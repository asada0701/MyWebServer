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
    @Test
    public void エラーレスポンンスコンテンツタイプのテスト() {
        assertThat(ContentType.ERROR_RESPONSE, is("text/html; charset=UTF-8"));
    }

    @Test(expected = NullPointerException.class)
    public void nullチェック() {
        new ContentType(null);
    }

    @Test
    public void デフォルトの値が返ってくるテスト() {
        ContentType sut = new ContentType("");
        assertThat(sut.getContentType(), is("application/octet-stream"));

        sut = new ContentType("hoge");
        assertThat(sut.getContentType(), is("application/octet-stream"));

        sut = new ContentType("/////////index.html/////////////");
        assertThat(sut.getContentType(), is("application/octet-stream"));

        sut = new ContentType("日本語.html/");
        assertThat(sut.getContentType(), is("application/octet-stream"));

        sut = new ContentType("sample.mp4");
        assertThat(sut.getContentType(), is("application/octet-stream"));
    }

    @Test
    public void 正しく動作するかテスト() {
        ContentType sut = new ContentType("////////////////index.html");
        assertThat(sut.getContentType(), is("text/html; charset=UTF-8"));

        sut = new ContentType("//////日本語////////日本語.html");
        assertThat(sut.getContentType(), is("text/html; charset=UTF-8"));

        sut = new ContentType("html");
        assertThat(sut.getContentType(), is("text/html; charset=UTF-8"));
    }

    @Test
    public void コンテンツタイプの網羅テスト() {
        ContentType sut = new ContentType("/index.htm");
        assertThat(sut.getContentType(), is("text/html; charset=UTF-8"));

        sut = new ContentType("/index.html");
        assertThat(sut.getContentType(), is("text/html; charset=UTF-8"));

        sut = new ContentType("/css/index.css");
        assertThat(sut.getContentType(), is("text/css"));

        sut = new ContentType("/javascript/sample.js");
        assertThat(sut.getContentType(), is("application/javascript"));

        sut = new ContentType("./src/main/resources/sample.txt");
        assertThat(sut.getContentType(), is("text/plain"));

        sut = new ContentType("sample.jpg");
        assertThat(sut.getContentType(), is("image/jpg"));

        sut = new ContentType("sample.jpeg");
        assertThat(sut.getContentType(), is("image/jpeg"));

        sut = new ContentType("sample.png");
        assertThat(sut.getContentType(), is("image/png"));

        sut = new ContentType("sample.gif");
        assertThat(sut.getContentType(), is("image/gif"));
    }
}
