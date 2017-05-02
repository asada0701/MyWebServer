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
            new ResponseMessage(os, statusCode, requestLine.getUri());
        } catch (IOException e) {

        }
    }
}
