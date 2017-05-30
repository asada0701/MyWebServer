package jp.co.topgate.asada.web.program.board;

/**
 * 編集するHTMLのリスト
 *
 * @author asada
 */
enum ProgramBoardHtmlList {
    /**
     * index.html
     * URI、ファイルのパス
     */
    INDEX_HTML("./src/main/resources/2/index.html"),

    /**
     * search.html
     */
    SEARCH_HTML("./src/main/resources/2/search.html"),

    /**
     * delete.html
     */
    DELETE_HTML("/src/main/resources/2/delete.html"),

    /**
     * result.html
     */
    RESULT_HTML("./src/main/resources/2/result.html");


    private final String path;

    ProgramBoardHtmlList(String path) {
        this.path = path;
    }

    /**
     * ファイルのパスを取得するメソッド
     *
     * @return ファイルのパスを返す
     */
    public String getPath() {
        return path;
    }
}
