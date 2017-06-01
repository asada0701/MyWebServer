package jp.co.topgate.asada.web;

import java.util.HashMap;
import java.util.Map;

/**
 * コンテンツタイプクラス
 *
 * @author asada
 */
public final class ContentType {

    /**
     * デフォルトコンテンツタイプ
     */
    private static final String DEFAULT_FILE_TYPE = "application/octet-stream";

    /**
     * ファイル拡張子とコンテンツタイプのマップ
     */
    private static Map<String, String> fileType = new HashMap<>();

    static {
        fileType.put("htm", "text/html; charset=UTF-8");
        fileType.put("html", "text/html; charset=UTF-8");
        fileType.put("css", "text/css");
        fileType.put("js", "application/javascript");

        fileType.put("txt", "text/plain");

        fileType.put("jpg", "image/jpg");
        fileType.put("jpeg", "image/jpeg");
        fileType.put("png", "image/png");
        fileType.put("gif", "image/gif");
    }

    /**
     * コンストラクタ
     * インスタンス化禁止
     */
    private ContentType() {

    }

    /**
     * HTMLのコンテンツタイプを取得するメソッド
     *
     * @return HTMLのコンテンツタイプを返す
     */
    public static String getHtmlType() {
        return fileType.get("html");
    }

    /**
     * コンテンツタイプを取得できるメソッド
     *
     * @param filePath ファイルのパス
     * @return コンテンツタイプを返す
     */
    public static String getContentType(String filePath) {
        for (String key : fileType.keySet()) {
            if (filePath.endsWith(key)) {
                return fileType.get(key);
            }
        }
        return DEFAULT_FILE_TYPE;
    }
}
