package jp.co.topgate.asada.web.app;

import java.util.HashMap;
import java.util.Map;

/**
 * 不正な文字列をHTMLの特殊文字に置き換えるクラス
 *
 * @author asada
 */
class InvalidChar {

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
    static String replace(String rawStr) {
        rawStr = rawStr.replaceAll("&", "&amp;");
        for (String key : invalidChar.keySet()) {
            rawStr = rawStr.replaceAll(key, invalidChar.get(key));
        }
        return rawStr;
    }
}
