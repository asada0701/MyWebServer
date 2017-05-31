package jp.co.topgate.asada.web.program.board;

import java.nio.file.Path;
import java.nio.file.Paths;

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
    INDEX_HTML("./src/main/resources/program/board/index.html"),

    /**
     * search.html
     */
    SEARCH_HTML("./src/main/resources/program/board/search.html"),

    /**
     * delete.html
     */
    DELETE_HTML("./src/main/resources/program/board/delete.html"),

    /**
     * result.html
     */
    RESULT_HTML("./src/main/resources/program/board/result.html");


    private final String path;

    ProgramBoardHtmlList(String path) {
        this.path = path;
    }

    /**
     * ファイルのパスを取得するメソッド
     *
     * @return ファイルのパスを返す
     */
    public Path getPath() {
        return Paths.get(path);
    }
}
