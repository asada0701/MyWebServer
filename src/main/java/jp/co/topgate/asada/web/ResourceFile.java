package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.exception.ResourceFileException;

import java.io.File;
import java.util.HashMap;

/**
 * リソースファイルの管理をするクラス
 *
 * @author asada
 */
public class ResourceFile extends File {
    /**
     * ファイル名のドット
     */
    private static final String FILE_NAME_DOT = "\\.";

    /**
     * ファイル拡張子とコンテンツタイプのハッシュマップ
     */
    private static HashMap<String, String> fileType = new HashMap<>();

    /**
     * 指定されたファイルの拡張子
     */
    private String extension;

    /**
     * コンストラクタ
     * ファイル拡張子とコンテンツタイプのハッシュマップの初期設定を行う
     *
     * @param filePath リクエストメッセージで指定されたファイルのパス
     * @throws ResourceFileException ディレクトリを指定された場合や、存在しないファイルを指定された
     */
    public ResourceFile(String filePath) throws RuntimeException {
        super(filePath);
        if (!this.exists()) {
            throw new ResourceFileException();
        }
        if (!this.isFile()) {
            throw new ResourceFileException();
        }

        String[] s = this.getName().split(FILE_NAME_DOT);
        extension = s[1];

        fileType.put("htm", "text/html");
        fileType.put("html", "text/html");
        fileType.put("css", "text/css");
        fileType.put("js", "text/javascript");

        fileType.put("txt", "text/plain");

        fileType.put("jpg", "image/jpg");
        fileType.put("jpeg", "image/jpeg");
        fileType.put("png", "image/png");
        fileType.put("gif", "image/gif");
    }

    /**
     * 登録済みのファイルかの確認メソッド
     */
    public boolean isRegistered() {
        return fileType.containsKey(extension);
    }

    /**
     * コンテンツタイプを取得できるメソッド
     *
     * @return コンテンツタイプを返す
     */
    public String getContentType() {
        if (extension == null) {
            return null;
        } else {
            return fileType.getOrDefault(extension, "application/octet-stream");
        }
    }
}
