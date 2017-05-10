package jp.co.topgate.asada.web;

/**
 * Created by yusuke-pc on 2017/05/09.
 */

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(Enclosed.class)
public class CipherHelperTest {
    public static class encryptメソッドのテスト {
        @Test(expected = NullPointerException.class)
        public void nullチェック() throws Exception {
            CipherHelper.encrypt(null);
        }

        @Test
        public void 空チェック() throws Exception {
            assertThat(CipherHelper.encrypt(""),
                    is("0QKsxhjpm/+eVac5lo3Jbw=="));
        }

        @Test
        public void 正しく暗号化できるか() throws Exception {
            assertThat(CipherHelper.encrypt("正しく暗号化できるか"),
                    is("4tZb8SxYjuKq+vzVOaAtQxZj7bvgK1eioZHcogvZMfQ="));
        }
    }

    public static class decryptメソッドのテスト {
        @Test(expected = NullPointerException.class)
        public void nullチェック() throws Exception {
            CipherHelper.decrypt(null);
        }

        @Test
        public void 空チェック() throws Exception {
            assertThat(CipherHelper.decrypt("0QKsxhjpm/+eVac5lo3Jbw=="),
                    is(""));
        }

        @Test
        public void 正しく暗号化できるか() throws Exception {
            assertThat(CipherHelper.decrypt("4tZb8SxYjuKq+vzVOaAtQxZj7bvgK1eioZHcogvZMfQ="),
                    is("正しく暗号化できるか"));
        }
    }
}
