package jp.co.topgate.asada.web;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by yusuke-pc on 2017/05/01.
 */
public class StaticHandler extends Handler {

    @Override
    public void returnResponse(OutputStream os) {
        try {
            String path = HandlerFactory.getFilePath(requestLine.getUri());
            new ResponseMessage(os, statusCode, path);
        } catch (IOException e) {

        }
    }
}
