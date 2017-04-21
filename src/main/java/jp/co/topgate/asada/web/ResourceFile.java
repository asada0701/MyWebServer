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
public class ResourceFile extends File {
    /**
     * ファイル名のドット
     */
    private static final String FILE_NAME_DOT = "\\.";

    /**
     * URIの中にドットは一つかの確認
     */
    private static final int URI_DOT_NUM_ITEMS = 3;

    /**
     * ファイル拡張子とコンテンツタイプのハッシュマップ
     */
    private static HashMap<String, String> fileType = new HashMap<>();

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
    public ResourceFile(String filePath) throws ResourceFileRuntimeException, FileNotRegisteredRuntimeException {
        super(filePath);
        if (!this.exists()) {
            throw new ResourceFileRuntimeException();
        }
        if (!this.isFile()) {
            throw new ResourceFileRuntimeException();
        }

        String[] s = this.getName().split(FILE_NAME_DOT);
        uri_extension = s[1];

        setUp();

        if (!isRegistered(filePath)) {
            throw new FileNotRegisteredRuntimeException();
        }
    }

    /**
     * ファイル拡張子とコンテンツタイプのハッシュマップの初期設定を行う
     */
    private static void setUp() {
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
     * すでに登録されている種類のファイルかを返すメソッド
     *
     * @param filePath ファイルのパスを渡す
     * @return 登録済みかどうかを返す
     */
    public static boolean isRegistered(String filePath) {
        boolean result = false;
        setUp();
        String[] s = filePath.split(FILE_NAME_DOT);
        if (s.length == URI_DOT_NUM_ITEMS) {
            result = fileType.containsKey(s[2]);
        }
        return result;
    }

    /**
     * ファイルの拡張子、コンテンツタイプの追加をする
     */
    public static void addFileType(String extension, String contentType) {
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
        return fileType.get(uri_extension);
    }
}
