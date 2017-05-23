package jp.co.topgate.asada.web;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * 列挙型のUrlPatternのテスト
 *
 * @author asada
 */
public class UrlPatternTest {
    @Test
    public void programBoardテスト() {
        assertThat(UrlPattern.PROGRAM_BOARD.getUrlPattern(), is("/program/board/"));
        assertThat(UrlPattern.PROGRAM_BOARD.getFilePath(), is("/2/"));
    }
}
