package jp.co.topgate.asada.web.app;

/**
 * URLパターンのEnum
 */
enum UrlPattern {

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

    public String getUrlPattern() {
        return this.urlPattern;
    }

    public String getFilePath() {
        return this.filePath;
    }
}
