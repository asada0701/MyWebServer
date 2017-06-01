package jp.co.topgate.asada.web;

import org.junit.Test;

import java.nio.file.Paths;

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
        assertThat(getContentType(Paths.get("")), is("application/octet-stream"));

        assertThat(getContentType(Paths.get("hoge")), is("application/octet-stream"));

        assertThat(getContentType(Paths.get("sample.mp4")), is("application/octet-stream"));

        assertThat(getContentType(Paths.get("sample.html.")), is("application/octet-stream"));
    }

    @Test
    public void 正しく動作するかテスト() {
        assertThat(getContentType(Paths.get("////////////////index.html")), is("text/html; charset=UTF-8"));

        assertThat(getContentType(Paths.get("//////日本語////////日本語.html")), is("text/html; charset=UTF-8"));

        assertThat(getContentType(Paths.get("html")), is("text/html; charset=UTF-8"));

        assertThat(getContentType(Paths.get("/////////index.html/////////////")), is("text/html; charset=UTF-8"));
    }

    @Test
    public void コンテンツタイプの網羅テスト() {
        assertThat(getContentType(Paths.get("/index.html")), is("text/html; charset=UTF-8"));

        assertThat(getContentType(Paths.get("/index.html")), is("text/html; charset=UTF-8"));

        assertThat(getContentType(Paths.get("/css/index.css")), is("text/css"));

        assertThat(getContentType(Paths.get("/javascript/sample.js")), is("application/javascript"));

        assertThat(getContentType(Paths.get("./src/main/resources/sample.txt")), is("text/plain"));

        assertThat(getContentType(Paths.get("sample.jpg")), is("image/jpg"));

        assertThat(getContentType(Paths.get("sample.jpeg")), is("image/jpeg"));

        assertThat(getContentType(Paths.get("sample.png")), is("image/png"));

        assertThat(getContentType(Paths.get("sample.gif")), is("image/gif"));
    }
}
