package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.exception.ResourceFileRuntimeException;

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
     * ファイルの拡張子
     */
    private String extension;

    /**
     * コンストラクタ
     *
     * @param filePath リクエストメッセージで指定されたファイルのパス
     * @throws ResourceFileRuntimeException ディレクトリを指定された場合や、存在しないファイルを指定された
     */
    public ResourceFile(String filePath) throws ResourceFileRuntimeException {
        super(filePath);
        if (!this.exists()) {
            throw new ResourceFileRuntimeException();
        }
        if (!this.isFile()) {
            throw new ResourceFileRuntimeException();
        }

        String[] s = this.getName().split(FILE_NAME_DOT);
        extension = s[1];

        setUp();
    }

    /**
     * ファイル拡張子とコンテンツタイプのハッシュマップの初期設定を行う
     */
    private void setUp() {
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
     * ファイルの拡張子、コンテンツタイプの追加をする
     */
    public void addFileType(String extension, String contentType) {
        if (extension != null && contentType != null) {
            fileType.put(extension, contentType);
        }
    }

    /**
     * コンテンツタイプを取得できるメソッド
     *
     * @return コンテンツタイプを返す
     */
    public String getContentType() {
        if (extension == null || fileType.containsKey(extension)) {
            return "application/octet-stream";
        } else {
            return fileType.get(extension);
        }
    }
}
