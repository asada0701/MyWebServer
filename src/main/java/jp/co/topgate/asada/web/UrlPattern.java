package jp.co.topgate.asada.web;

/**
 * URLパターンのEnum
 */
public enum UrlPattern {

    /**
     * URLのパターン、ファイルのディレクトリ
     * で登録する
     */
    PROGRAM_BOARD("/program/board/", "/2/");

    private final String urlPattern;

    private final String filePath;

    UrlPattern(String urlPattern, String filePath) {
        this.urlPattern = urlPattern;
        this.filePath = filePath;
    }

    /**
     * 登録したURLのパターンを取得するメソッド
     *
     * @return URLのパターンを返す
     */
    public String getUrlPattern() {
        return this.urlPattern;
    }

    /**
     * URLパターンに対応するファイルのディレクトリを取得するメソッド
     *
     * @return ファイルのディレクトリのパスを返す
     */
    public String getFilePath() {
        return this.filePath;
    }
}
