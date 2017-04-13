import jp.co.topgate.asada.web.RequestMessage;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

/**
 * Created by yusuke-pc on 2017/04/13.
 */
public class RequestMessageTest {
    @Test
    public void パースのテストをしようと思う () throws Exception{
        RequestMessage rm = new RequestMessage();

        assertNull(rm.getMethod());
        assertNull(rm.getUri());
        assertNull(rm.getProtocolVersion());

        File file = new File("./src/test/java/requestMessage.txt");
        InputStream is = new FileInputStream(file);
        try {
            rm.parse(is);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assertThat("GET", is(rm.getMethod()));
        assertThat("/",is(rm.getUri()));
        assertThat("HTTP/1.1",is(rm.getProtocolVersion()));
        assertThat("www.xxx.zzz",is(rm.findHeaderByName("Host")));
    }
}
