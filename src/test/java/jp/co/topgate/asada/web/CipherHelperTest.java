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
                    is("IhtXErS0+ZoF1hhWZSll8A=="));
        }

        @Test
        public void 正しく暗号化できるか() throws Exception {
            assertThat(CipherHelper.encrypt("正しく暗号化できるか"),
                    is("w2YZ69B3kqQzmNcnIGKxbygnKf7cvzGKG5tDwU04OiU="));
        }
    }

    public static class decryptメソッドのテスト {
        @Test(expected = NullPointerException.class)
        public void nullチェック() throws Exception {
            CipherHelper.decrypt(null);
        }

        @Test
        public void 空チェック() throws Exception {
            assertThat(CipherHelper.decrypt("IhtXErS0+ZoF1hhWZSll8A=="),
                    is(""));
        }

        @Test
        public void 正しく暗号化できるか() throws Exception {
            assertThat(CipherHelper.decrypt("w2YZ69B3kqQzmNcnIGKxbygnKf7cvzGKG5tDwU04OiU="),
                    is("正しく暗号化できるか"));
        }
    }
}
