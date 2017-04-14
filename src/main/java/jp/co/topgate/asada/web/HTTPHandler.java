package jp.co.topgate.asada.web;

import com.sun.org.apache.regexp.internal.RE;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by yusuke-pc on 2017/04/12.
 */
public class HTTPHandler {
    public static final String FILE_PATH = "./src/test/java/jp/co/topgate/asada/web/Documents";
    private File resource = null;
    private boolean responseFinish = true;      //一回目はtrue

    public OutputStream requestComes(InputStream is, OutputStream os){
        RequestMessage requestMessage = new RequestMessage();
        ResponseMessage responseMessage = new ResponseMessage();
        if(requestMessage.parse(is)){
            //リクエストメッセージには問題なし
            responseMessage.getResposeMessage(os);
        }else{
            //400バッドリクエスト対象、リクエストメッセージ関連での異常
        }
        return os;
    }
    public boolean isResponseFinish(){
        return responseFinish;
    }
}
