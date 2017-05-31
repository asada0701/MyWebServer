package jp.co.topgate.asada.web.program.board;

import org.junit.Test;

import java.nio.file.Paths;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * 列挙型のProgramBoardHtmlListのテスト
 *
 * @author asada
 */
public class ProgramBoardHtmlListTest {
    @Test
    public void 登録されているもの() {
        assertThat(ProgramBoardHtmlList.INDEX_HTML.getPath(), is(Paths.get("./src/main/resources/program/board/index.html")));

        assertThat(ProgramBoardHtmlList.SEARCH_HTML.getPath(), is(Paths.get("./src/main/resources/program/board/search.html")));

        assertThat(ProgramBoardHtmlList.DELETE_HTML.getPath(), is(Paths.get("./src/main/resources/program/board/delete.html")));

        assertThat(ProgramBoardHtmlList.RESULT_HTML.getPath(), is(Paths.get("./src/main/resources/program/board/result.html")));
    }
}
