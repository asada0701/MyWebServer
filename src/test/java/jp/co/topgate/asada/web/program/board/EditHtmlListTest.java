package jp.co.topgate.asada.web.program.board;

import jp.co.topgate.asada.web.program.board.EditHtmlList;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * 列挙型のEditHtmlListのテスト
 *
 * @author asada
 */
public class EditHtmlListTest {
    @Test
    public void indexHtmlテスト() {
        assertThat(EditHtmlList.INDEX_HTML.getUri(), is("/program/board/index.html"));
        assertThat(EditHtmlList.INDEX_HTML.getPath(), is("./src/main/resources/2/index.html"));
    }

    @Test
    public void searchHtmlテスト() {
        assertThat(EditHtmlList.SEARCH_HTML.getUri(), is("/program/board/search.html"));
        assertThat(EditHtmlList.SEARCH_HTML.getPath(), is("./src/main/resources/2/search.html"));
    }

    @Test
    public void deleteHtmlテスト() {
        assertThat(EditHtmlList.DELETE_HTML.getUri(), is("/program/board/delete.html"));
        assertThat(EditHtmlList.DELETE_HTML.getPath(), is("./src/main/resources/2/delete.html"));
    }
}
