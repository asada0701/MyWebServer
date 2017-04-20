package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.exception.FileNotRegisteredRuntimeException;
import jp.co.topgate.asada.web.exception.ResourceFileRuntimeException;

import java.io.File;
import java.util.HashMap;

/**
 * リソースファイルの管理をするクラス
 *
 * @author asada
 */
class ResourceFile extends File {
    /**
     * URIのドット
     */
    private static final String FILE_NAME_DOT = "\\.";

    /**
     * の中にドットは一つかの確認
     */
    private static final int FILE_NAME_DOT_NUM_ITEMS = 2;

    /**
     * ファイル拡張子とコンテンツタイプのハッシュマップ
     */
    private HashMap<String, String> fileType = new HashMap<>();

    /**
     * URIに含まれるファイルの拡張子
     */
    private String uri_extension;

    /**
     * コンストラクタ
     *
     * @param filePath リクエストメッセージで指定されたファイルのパス
     * @throws ResourceFileRuntimeException      ディレクトリを指定された場合や、存在しないファイルを指定された
     * @throws FileNotRegisteredRuntimeException 指定されたファイルのコンテツタイプが登録されていない
     */
    ResourceFile(String filePath) throws ResourceFileRuntimeException, FileNotRegisteredRuntimeException {
        super(filePath);
        if (!this.exists()) {
            throw new ResourceFileRuntimeException();
        }
        if (!this.isFile()) {
            throw new ResourceFileRuntimeException();
        }

        String[] s = this.getName().split(FILE_NAME_DOT);
        if (s.length == FILE_NAME_DOT_NUM_ITEMS) {
            uri_extension = s[1];
        }

        fileType.put("htm", "text/html");
        fileType.put("html", "text/html");
        fileType.put("css", "text/css");
        fileType.put("js", "text/javascript");

        fileType.put("txt", "text/plain");

        fileType.put("jpg", "image/jpg");
        fileType.put("jpeg", "image/jpeg");
        fileType.put("png", "image/png");
        fileType.put("gif", "image/gif");

        if (!this.isRegistered()) {
            throw new FileNotRegisteredRuntimeException();
        }
    }

    /**
     * すでに登録されている種類のファイルかを返すメソッド
     *
     * @return 登録済みかどうか
     */
    boolean isRegistered() {
        return fileType.containsKey(uri_extension);
    }

    /**
     * ファイルの拡張子、コンテンツタイプの登録メソッド
     *
     * @param extension   拡張子
     * @param contentType 　コンテンツタイプ
     */
    void addFileType(String extension, String contentType) {
        if (extension != null && contentType != null) {
            fileType.put(extension, contentType);
        }
    }

    /**
     * コンテンツタイプの取得できるメソッド
     *
     * @return コンテンツタイプ
     */
    String getContentType() {
        return fileType.get(uri_extension);
    }
}
