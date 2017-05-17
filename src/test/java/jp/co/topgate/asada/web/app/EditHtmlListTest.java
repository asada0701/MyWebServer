package jp.co.topgate.asada.web.app;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by yusuke-pc on 2017/05/17.
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
