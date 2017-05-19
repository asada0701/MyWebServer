package jp.co.topgate.asada.web.app;

import jp.co.topgate.asada.web.html.EditHtmlList;
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
        assertThat(EditHtmlList.INDEX_HTML.getId(), is(0));
        assertThat(EditHtmlList.INDEX_HTML.getPath(), is("./src/main/resources/2/index.html"));
    }

    @Test
    public void searchHtmlテスト() {
        assertThat(EditHtmlList.SEARCH_HTML.getId(), is(1));
        assertThat(EditHtmlList.SEARCH_HTML.getPath(), is("./src/main/resources/2/search.html"));
    }

    @Test
    public void deleteHtmlテスト() {
        assertThat(EditHtmlList.DELETE_HTML.getId(), is(2));
        assertThat(EditHtmlList.DELETE_HTML.getPath(), is("./src/main/resources/2/delete.html"));
    }
}
