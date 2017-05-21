package jp.co.topgate.asada.web.app;

import java.util.HashMap;
import java.util.Map;

/**
 * 不正な文字列のクラス
 *
 * @author asada
 */
public class InvalidChar {
    /**
     * 不正な文字列
     */
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
     * @param rawStr 生の文字列
     * @return 置き換えた文字列
     */
    public static String replaceInputValue(String rawStr) {
        rawStr = rawStr.replaceAll("&", "&amp;");
        for (String key : invalidChar.keySet()) {
            rawStr = rawStr.replaceAll(key, invalidChar.get(key));
        }
        return rawStr;
    }
}
