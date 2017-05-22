package jp.co.topgate.asada.web;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * 列挙型のChoicesをテストする
 *
 * @author asada
 */
public class ChoicesTest {
    @Test
    public void IDのテスト() {
        assertThat(Choices.START.getId(), is("1"));
        assertThat(Choices.STOP.getId(), is("2"));
        assertThat(Choices.END.getId(), is("3"));
    }

    @Test(expected = NullPointerException.class)
    public void nullチェック() {
        Choices.getEnum(null);
    }

    @Test
    public void 空チェック() {
        Choices sut = Choices.getEnum("");
        assertThat(sut, is(Choices.END));
    }

    @Test
    public void Choices_STARTが返ってくる() {
        Choices sut = Choices.getEnum("1");
        assertThat(sut, is(Choices.START));
    }

    @Test
    public void Choices_STOPが返ってくる() {
        Choices sut = Choices.getEnum("2");
        assertThat(sut, is(Choices.STOP));
    }

    @Test
    public void Choices_ENDが返ってくる() {
        Choices sut = Choices.getEnum("3");
        assertThat(sut, is(Choices.END));
    }

}
