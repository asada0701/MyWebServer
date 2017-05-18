package jp.co.topgate.asada.web.app;

/**
 * URLパターンのEnum
 */
enum UrlPattern {

    /**
     * URLのパターン、ファイルのディレクトリ
     */
    PROGRAM_BOARD("/program/board/", "/2/");

    private final String urlPattern;

    private final String filePath;

    UrlPattern(final String urlPattern, final String filePath) {
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
