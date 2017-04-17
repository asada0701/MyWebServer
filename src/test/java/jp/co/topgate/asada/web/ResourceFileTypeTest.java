package jp.co.topgate.asada.web;

import org.junit.Before;
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
        ResourceFileType sut = null;
        @Before
        public void setUp () {
            sut = new ResourceFileType();
        }

        @Test
        public void trueが返ってくる () throws Exception{
            assertThat(sut.isTxt("/index.html"), is(true));
        }
        @Test
        public void uriの途中で入れてみるfalseのはず () throws Exception{
            assertThat(sut.isTxt("aaahtmlaaa"), is(false));
        }
        @Test
        public void uriの最後の一文字を間違えてみるfalseのはず () throws Exception{
            assertThat(sut.isTxt("aaa.htmll"), is(false));
        }
        @Test
        public void uriをnullにしてみるfalseのはず () throws Exception{
            assertThat(sut.isTxt(null), is(false));
        }
    }

    public static class isImgメソッドのテスト {
        ResourceFileType sut = null;
        @Before
        public void setUp () {
            sut = new ResourceFileType();
        }

        @Test
        public void trueが返ってくる () {
            assertThat(sut.isImg("/img/1.jpg"), is(true));
        }
        @Test
        public void uriの途中で入れてみるfalseのはず () {
            assertThat(sut.isImg("/img/1.jpg/"), is(false));
        }
        @Test
        public void uriの最後の一文字を間違えてみるfalseのはず () {
            assertThat(sut.isImg("/img/1.jpgg"), is(false));
        }
    }
}
