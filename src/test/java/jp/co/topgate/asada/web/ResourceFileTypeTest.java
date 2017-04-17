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
            assertThat(sut.isTxt("fjklsda;fjkljds;af.htm"), is(true));
        }
        @Test
        public void falseが返ってくる () throws Exception{
            ResourceFileType sut = new ResourceFileType();
            assertThat(sut.isTxt("aaahtmaaa"), is(false));
        }
    }

    public static class isImgメソッドのテスト {
        @Test
        public void isImgメソッドのテスト () {
            ResourceFileType sut = new ResourceFileType();
            assertThat(sut.isImg(""), is(true));
        }
    }
}
