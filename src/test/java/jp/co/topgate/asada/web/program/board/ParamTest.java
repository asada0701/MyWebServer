package jp.co.topgate.asada.web.program.board;

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
        Param sut = Param.getEnum("write");
        assertThat(sut, is(Param.WRITE));
    }

    @Test
    public void searchのテスト() {
        Param sut = Param.getEnum("search");
        assertThat(sut, is(Param.SEARCH));
    }

    @Test
    public void delete1のテスト() {
        Param sut = Param.getEnum("delete_step_1");
        assertThat(sut, is(Param.DELETE_STEP_1));
    }

    @Test
    public void delete2のテスト() {
        Param sut = Param.getEnum("delete_step_2");
        assertThat(sut, is(Param.DELETE_STEP_2));
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
