package jp.co.topgate.asada.web;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * 列挙型のStatusLineのテスト
 *
 * @author asada
 */
public class StatusLineTest {
    @Test
    public void ステータス200テスト() {
        assertThat(StatusLine.OK.getStatusCode(), is(200));
        assertThat(StatusLine.OK.getReasonPhrase(), is("OK"));
    }

    @Test
    public void ステータス400テスト() {
        assertThat(StatusLine.BAD_REQUEST.getStatusCode(), is(400));
        assertThat(StatusLine.BAD_REQUEST.getReasonPhrase(), is("Bad request"));
    }

    @Test
    public void ステータス404テスト() {
        assertThat(StatusLine.NOT_FOUND.getStatusCode(), is(404));
        assertThat(StatusLine.NOT_FOUND.getReasonPhrase(), is("Not Found"));
    }

    @Test
    public void ステータス500テスト() {
        assertThat(StatusLine.INTERNAL_SERVER_ERROR.getStatusCode(), is(500));
        assertThat(StatusLine.INTERNAL_SERVER_ERROR.getReasonPhrase(), is("Internal Server Error"));
    }

    @Test
    public void ステータス501テスト() {
        assertThat(StatusLine.NOT_IMPLEMENTED.getStatusCode(), is(501));
        assertThat(StatusLine.NOT_IMPLEMENTED.getReasonPhrase(), is("Not Implemented"));
    }

    @Test
    public void ステータス505テスト() {
        assertThat(StatusLine.HTTP_VERSION_NOT_SUPPORTED.getStatusCode(), is(505));
        assertThat(StatusLine.HTTP_VERSION_NOT_SUPPORTED.getReasonPhrase(), is("HTTP Version Not Supported"));
    }
}
