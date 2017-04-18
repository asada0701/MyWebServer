package jp.co.topgate.asada.web;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

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
            sut = new ResourceFileType("./src/test/java/jp/co/topgate/asada/web/Documents/empty.txt");
        }
    }
    public static class isRegisteredメソッドのテスト {
        ResourceFileType sut = null;
        @Before
        public void setUp() {
            sut = new ResourceFileType("./src/test/java/jp/co/topgate/asada/web/Documents/empty.txt");
        }
    }
}
