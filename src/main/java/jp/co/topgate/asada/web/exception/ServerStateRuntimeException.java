package jp.co.topgate.asada.web.exception;

/**
 * Created by yusuke-pc on 2017/04/19.
 */
public class ServerStateRuntimeException extends RuntimeException {

    private Thread.State state;

    public ServerStateRuntimeException(Thread.State state) {
        this.state = state;
    }

    public Thread.State getState() {
        return state;
    }
}
