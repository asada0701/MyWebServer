package jp.co.topgate.asada.web.exception;

/**
 * ServerStateの例外
 * Threadを継承したServerクラスのThread.Stateが予期しない状態の場合、発生する
 *
 * @author asada
 */
public class ServerStateException extends RuntimeException {

    private Thread.State state;

    public ServerStateException(Thread.State state) {
        this.state = state;
    }

    @Override
    public String getMessage() {
        if (state != null) {
            return "Unexpected Server State! state = " + state.toString();
        } else {
            return null;
        }
    }
}
