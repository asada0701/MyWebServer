package jp.co.topgate.asada.web.program.board;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * ProgramBoardHandlerのGETの処理で使用するEnum
 *
 * @author asada
 */
public enum HtmlList {
    INDEX_HTML(Paths.get("./src/main/resources/program/board/index.html")),
    SEARCH_HTML(Paths.get("./src/main/resources/program/board/search.html")),
    DELETE_HTML(Paths.get("./src/main/resources/program/board/delete.html"));

    private Path path;

    HtmlList(Path path) {
        this.path = path;
    }

    public Path getPath() {
        return path;
    }
}
