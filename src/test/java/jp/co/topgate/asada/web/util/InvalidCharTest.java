package jp.co.topgate.asada.web.util;

import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * InvalidCharクラスのテスト
 *
 * @author asada
 */
@RunWith(Enclosed.class)
public class InvalidCharTest {
    public static class replaceInputValueメソッドのテスト {
        @Test
        public void 置き換えが発生するパターン() {
            String result = UnsafeChar.replace("<script>");
            assertThat(result, is("&lt;script&gt;"));

            result = UnsafeChar.replace("te&st");
            assertThat(result, is("te&amp;st"));

            result = UnsafeChar.replace("t\"e\'s&t");
            assertThat(result, is("t&quot;e&#39;s&amp;t"));
        }

        @Test
        public void 発生しないパターン() {
            String result = UnsafeChar.replace("アイウエオ");
            assertThat(result, is("アイウエオ"));

            result = "script";
            assertThat(result, is("script"));
        }
    }
}
