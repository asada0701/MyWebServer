package jp.co.topgate.asada.web;

import java.util.HashMap;
import java.util.Map;

/**
 * コンテンツタイプクラス
 *
 * @author asada
 */
class ContentType {

    /**
     * fileTypeマップになかった拡張子のコンテンツタイプ
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
     * 指定されたファイルの拡張子
     */
    private String extension;

    /**
     * コンストラクタ
     * ファイル拡張子とコンテンツタイプのハッシュマップの初期設定を行う
     *
     * @param filePath リクエストメッセージで指定されたファイルのパス
     */
    ContentType(String filePath) throws NullPointerException {
        if (filePath == null) {
            throw new NullPointerException("ContentTypeのコンストラクでnullを渡した。");
        }
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
