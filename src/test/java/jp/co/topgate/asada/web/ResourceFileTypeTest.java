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
    public static class isCharメソッドのテスト {
        ResourceFileType sut = null;
        @Before
        public void setUp () throws Exception{
            sut = new ResourceFileType();
        }

        @Test
        public void trueが返ってくる () throws Exception{
            assertThat(sut.isChar("/index.html"), is(true));
        }

        @Test
        public void uriの途中で入れてみるfalseのはず () throws Exception{
            assertThat(sut.isChar("aaahtmlaaa"), is(false));
        }

        @Test
        public void uriの最後の一文字を間違えてみるfalseのはず () throws Exception{
            assertThat(sut.isChar("aaa.htmll"), is(false));
        }

        @Test
        public void uriをnullにしてみるfalseのはず () throws Exception{
            assertThat(sut.isChar(null), is(false));
        }
    }

    public static class isByteメソッドのテスト {
        ResourceFileType sut = null;
        @Before
        public void setUp () throws Exception{
            sut = new ResourceFileType();
        }

        @Test
        public void trueが返ってくる () throws Exception{
            assertThat(sut.isByte("/img/1.jpg"), is(true));
        }

        @Test
        public void uriの途中で入れてみるfalseのはず () throws Exception{
            assertThat(sut.isByte("/img/1.jpg/"), is(false));
        }

        @Test
        public void uriの最後の一文字を間違えてみるfalseのはず () throws Exception{
            assertThat(sut.isByte("/img/1.jpgg"), is(false));
        }

        @Test
        public void uriをnullにしてみるfalseのはず () throws Exception {
            assertThat(sut.isByte(null), is(false));
        }
    }
}
