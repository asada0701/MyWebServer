package jp.co.topgate.asada.web;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * コンテンツタイプクラス
 *
 * @author asada
 */
class ContentType {

    /**
     * デフォルトコンテンツタイプ
     */
    static final String defaultFileType = "application/octet-stream";

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
     * コンストラクタで渡されてきたパスにあるファイルの拡張子
     */
    private String extension;

    /**
     * コンストラクタ
     * ファイル拡張子を取得する
     *
     * @param filePath リクエストメッセージで指定されたファイルのパス
     * @throws NullPointerException 引数がnullの場合
     */
    ContentType(String filePath) throws NullPointerException {
        Objects.requireNonNull(filePath);

        for (String key : fileType.keySet()) {
            if (filePath.endsWith(key)) {
                extension = key;
            }
        }
    }

    /**
     * コンテンツタイプを取得できるメソッド
     *
     * @return コンテンツタイプを返す
     */
    String getContentType() {
        return fileType.getOrDefault(extension, defaultFileType);
    }
}
