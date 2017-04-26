package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.exception.BindRuntimeException;
import jp.co.topgate.asada.web.exception.ServerStateException;

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

    /**
     * メインメソッド
     */
    public static void main(String[] args) {
        System.out.println("this server's port number is 8080..");
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
                } while (isSelect(choices));

                String msg = controlServer(server, choices);
                if (msg != null) {
                    System.out.println(msg);
                } else {
                    choices = "";
                }
            } while (!choices.equals(END_NUM));

        } catch (BindRuntimeException e) {
            System.out.println(e.getMessage());
            System.exit(1);

        } catch (ServerStateException e) {
            System.out.println(e.getMessage());
            System.exit(1);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean isSelect(String choices) {
        return !(choices.equals(START_NUM) || choices.equals(STOP_NUM) || choices.equals(END_NUM));
    }

    /**
     * サーバーを操作するメソッド
     *
     * @param choices 選択した文字
     * @return サーバーの状態をメッセージで返す
     * @throws IOException          サーバークラスで発生した入出力エラー
     * @throws BindRuntimeException サーバークラスで発生したバインド例外
     * @throws ServerStateException サーバークラスの状態が予期しないものになった場合に発生する
     */
    private static String controlServer(Server server, String choices) throws IOException, BindRuntimeException, ServerStateException {
        if (server == null || choices == null) {
            return null;
        }
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
                        throw new ServerStateException(server.getState());
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
                        throw new ServerStateException(server.getState());
                }
                break;

            case END_NUM:
                server.endServer();
                msg = "bye..";
                break;

            default:
                return null;
        }
        return msg;
    }
}
