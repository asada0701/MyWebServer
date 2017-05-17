package jp.co.topgate.asada.web;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by yusuke-pc on 2017/05/17.
 */
public class ChoicesTest {
    @Test
    public void 添え字のテスト() {
        assertThat(Choices.START.getId(), is(1));
        assertThat(Choices.STOP.getId(), is(2));
        assertThat(Choices.END.getId(), is(3));
    }
}
