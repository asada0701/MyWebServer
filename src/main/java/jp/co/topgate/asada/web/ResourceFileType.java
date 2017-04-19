package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.exception.ResourceConstructorRuntimeException;

import java.util.HashMap;

/**
 * Created by yusuke-pc on 2017/04/15.
 */
class ResourceFileType {

    /**
     * URIのドット
     */
    private static final String URI_DOT = "\\.";

    /**
     * URIの中にドットは一つかの確認
     */
    private static final int URI_DOT_NUM_ITEMS = 2;

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
     * @param uri リクエストメッセージに含まれるURI
     * @throws NullPointerException もし引数がnullの場合吐き出す
     */
    ResourceFileType(String uri) {
        if (uri != null) {
            String[] s = uri.split(URI_DOT);
            if (s.length == URI_DOT_NUM_ITEMS) {
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
        } else {
            throw new ResourceConstructorRuntimeException();
        }
    }

    /**
     * すでに登録されている種類のファイルかを返すメソッド
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
     */
    String getContentType() {
        return fileType.get(uri_extension);
    }


}
