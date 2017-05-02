package jp.co.topgate.asada.web;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * コンテンツタイプクラス
 *
 * @author asada
 */
public class ContentType extends File {
    /**
     * リソースファイルのパス
     */
    private static final String FILE_PATH = "./src/main/resources";

    /**
     * ファイル拡張子とコンテンツタイプのハッシュマップ
     */
    private static Map<String, String> fileType = new HashMap<>();

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
    public ContentType(String filePath) {
        super(FILE_PATH + filePath);

        fileType.put("htm", "text/html; charset=UTF-8");
        fileType.put("html", "text/html; charset=UTF-8");
        fileType.put("css", "text/css");
        fileType.put("js", "application/javascript");

        fileType.put("txt", "text/plain");

        fileType.put("jpg", "image/jpg");
        fileType.put("jpeg", "image/jpeg");
        fileType.put("png", "image/png");
        fileType.put("gif", "image/gif");

        if (filePath != null) {
            for (String key : fileType.keySet()) {
                if (filePath.endsWith(key)) {
                    extension = key;
                }
            }
        }
    }

    /**
     * コンテンツタイプを取得できるメソッド
     *
     * @return コンテンツタイプを返す
     */
    public String getContentType() {
        return fileType.getOrDefault(extension, "application/octet-stream");
    }
}
