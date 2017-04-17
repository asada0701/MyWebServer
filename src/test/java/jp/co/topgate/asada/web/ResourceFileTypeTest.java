package jp.co.topgate.asada.web;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by yusuke-pc on 2017/04/17.
 */
@RunWith(Enclosed.class)
public class ResourceFileTypeTest {
    public static class isTxtメソッドのテスト {
        @Test
        public void trueが返ってくる () throws Exception{
            ResourceFileType sut = new ResourceFileType();
            assertThat(sut.isTxt("/index.html"), is(true));
        }
        @Test
        public void uriの途中で入れてみるfalseのはず () throws Exception{
            ResourceFileType sut = new ResourceFileType();
            assertThat(sut.isTxt("aaahtmlaaa"), is(false));
        }
        @Test
        public void uriの最後の一文字を間違えてみるfalseのはず () throws Exception{
            ResourceFileType sut = new ResourceFileType();
            assertThat(sut.isTxt("aaa.htmll"), is(false));
        }
    }

    public static class isImgメソッドのテスト {
        @Test
        public void trueが返ってくる () {
            ResourceFileType sut = new ResourceFileType();
            assertThat(sut.isImg("/img/1.jpg"), is(true));
        }
        @Test
        public void uriの途中で入れてみるfalseのはず () {
            ResourceFileType sut = new ResourceFileType();
            assertThat(sut.isImg("/img/1.jpg/"), is(false));
        }
        @Test
        public void uriの最後の一文字を間違えてみるfalseのはず () {
            ResourceFileType sut = new ResourceFileType();
            assertThat(sut.isImg("/img/1.jpgg"), is(false));
        }
    }
}
