package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.exception.ServerStateRuntimeException;

import java.io.IOException;
import java.util.Scanner;

/**
 * クライアントクラス
 *
 * @author asada
 */
public class App {
    private static final String START_NUM = "1";
    private static final String STOP_NUM = "2";
    private static final String END_NUM = "3";

    public static void main(String[] args) {
        try {
            Server server = new Server();
            String choices;
            Scanner scan = new Scanner(System.in);
            do {
                System.out.println("--------------------");
                System.out.println(START_NUM + ": START");
                System.out.println(STOP_NUM + ": STOP");
                System.out.println(END_NUM + ": END");
                do {
                    System.out.print("please select :");
                    choices = scan.next();
                } while (!(choices.equals(START_NUM) || choices.equals(STOP_NUM) || choices.equals(END_NUM)));
                String msg = controlServer(server, choices);
                if (msg != null) {
                    System.out.println(msg);
                } else {
                    choices = null;
                }
            } while (!choices.equals(END_NUM));

        } catch (ServerStateRuntimeException e) {
            e.printStackTrace();
            System.out.println("Unexpected Server State! state = " + e.getState().toString());

        } catch (IOException e) {
            System.out.println("Input/Output of Server is wrong state..");
            e.printStackTrace();
        }
    }

    /**
     * サーバーを操作するメソッド
     *
     * @param choices 選択した文字
     * @return サーバーの状態をメッセージで返す
     * @throws IOException サーバークラスで発生する
     */
    public static String controlServer(Server server, String choices) throws IOException {
        String msg;
        switch (choices) {
            case START_NUM:
                switch (server.getState()) {
                    case TERMINATED:
                        server = new Server();
                    case NEW:
                        server.startServer();
                        msg = "start up http server..";
                        break;
                    case RUNNABLE:
                        msg = "http server is already running..";
                        break;
                    default:
                        server.endServer();
                        throw new ServerStateRuntimeException(server.getState());
                }
                break;
            case STOP_NUM:
                switch (server.getState()) {
                    case NEW:
                    case TERMINATED:
                        msg = "http server is not running..";
                        break;
                    case RUNNABLE:
                        if (server.stopServer()) {
                            msg = "http server stops..";
                        } else {
                            msg = "wait a second, http server is returning a response..";
                        }
                        break;
                    default:
                        server.endServer();
                        throw new ServerStateRuntimeException(server.getState());
                }
                break;
            case END_NUM:
                if (server.stopServer()) {
                    msg = "bye..";
                } else {
                    msg = "wait a second, http server is returning a response..";
                }
                break;
            default:
                if (Thread.State.RUNNABLE.equals(server.getState())) {
                    server.endServer();
                }
                msg = null;
        }
        return msg;
    }
}
