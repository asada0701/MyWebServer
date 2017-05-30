package jp.co.topgate.asada.web.util;

import java.util.HashMap;
import java.util.Map;

/**
 * HTML文章上で安全ではない文字列を、HTMLの特殊文字に置き換えるクラス
 *
 * @author asada
 */
public class UnsafeChar {

    private static Map<String, String> invalidChar = new HashMap<>();

    static {
        invalidChar.put("<", "&lt;");
        invalidChar.put(">", "&gt;");
        invalidChar.put("\"", "&quot;");
        invalidChar.put("\'", "&#39;");
    }

    /**
     * セキュリティに問題のある入力値(<script></script>など)をHTMLの特殊文字に置き換えるメソッド
     *
     * @param unsafeChar 安全ではない文字列
     * @return 置き換えた文字列
     */
    public static String replace(String unsafeChar) {
        String safeChar = unsafeChar.replaceAll("&", "&amp;");
        for (String key : invalidChar.keySet()) {
            safeChar = safeChar.replaceAll(key, invalidChar.get(key));
        }
        return safeChar;
    }
}
