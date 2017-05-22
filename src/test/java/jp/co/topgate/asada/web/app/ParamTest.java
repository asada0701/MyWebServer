package jp.co.topgate.asada.web.app;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * 列挙型のParamのテスト
 *
 * @author asada
 */
public class ParamTest {
    @Test
    public void contributionのテスト() {
        Param sut = Param.getEnum("contribution");
        assertThat(sut, is(Param.CONTRIBUTION));
    }

    @Test
    public void searchのテスト() {
        Param sut = Param.getEnum("search");
        assertThat(sut, is(Param.SEARCH));
    }

    @Test
    public void delete1のテスト() {
        Param sut = Param.getEnum("delete1");
        assertThat(sut, is(Param.DELETE1));
    }

    @Test
    public void delete2のテスト() {
        Param sut = Param.getEnum("delete2");
        assertThat(sut, is(Param.DELETE2));
    }

    @Test
    public void backのテスト() {
        Param sut = Param.getEnum("back");
        assertThat(sut, is(Param.BACK));
    }

    @Test
    public void 未登録のテスト() {
        Param sut = Param.getEnum("未登録");
        assertThat(sut, is(nullValue()));
    }
}
