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
    public static class コンストラクタのテスト {
        ResourceFileType sut = null;
        @Test
        public void nullチェック() {
            sut = new ResourceFileType(null);
        }
        @Test
        public void 正しくセットできるか() {
            sut = new ResourceFileType("src/test/java/jp/co/topgate/asada/web/Documents/empty.txt");
        }
    }
    public static class isRegisteredメソッドのテスト {
        ResourceFileType sut = null;
        @Before
        public void setUp() {
            sut = new ResourceFileType("src/test/java/jp/co/topgate/asada/web/Documents/empty.txt");
        }
        @Test
        public void 存在する拡張子ならtrue() {
            assertThat(sut.isRegistered(), is(true));
        }
    }
}
