package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.app.CsvWriter;
import jp.co.topgate.asada.web.app.HtmlEditor;
import jp.co.topgate.asada.web.exception.CsvRuntimeException;
import jp.co.topgate.asada.web.exception.ServerStateException;
import jp.co.topgate.asada.web.exception.SocketRuntimeException;
import jp.co.topgate.asada.web.model.ModelController;

import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

/**
 * クライアントクラス
 *
 * @author asada
 */
public class App {
    /**
     * メインメソッド
     */
    public static void main(String[] args) {
        System.out.println("this server's port number is 8080..");
        try {
            Server server = new Server();
            String choices;
            Scanner scan = new Scanner(System.in);

            ModelController.setMessageList(CsvWriter.readMessage(CsvWriter.messageCsvPath));    //CSVファイル読み込み
            HtmlEditor he = new HtmlEditor();                                                   //HTMLファイル読み込み

            do {
                System.out.println("--------------------");
                System.out.println(Choices.START.getId() + ": START");
                System.out.println(Choices.STOP.getId() + ": STOP");
                System.out.println(Choices.END + ": END");

                do {
                    System.out.print("please select :");
                    choices = scan.next();
                } while (isSelect(choices));

                String msg = controlServer(server, getChoicesEnum(choices));
                if (msg != null) {
                    System.out.println(msg);
                } else {
                    choices = "";
                }
            } while (!choices.equals(String.valueOf(Choices.END.getId())));

            CsvWriter.writeMessage(ModelController.getAllMessage(), CsvWriter.messageCsvPath);      //CSVファイルに書き込み
            he.allInitialization();                                                                 //HTMLファイルの初期化

        } catch (ServerStateException | CsvRuntimeException | SocketRuntimeException |
                NullPointerException | IOException e) {

            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    private static boolean isSelect(String choices) {
        return !(choices.equals(String.valueOf(Choices.START.getId())) ||
                choices.equals(String.valueOf(Choices.STOP.getId())) ||
                choices.equals(String.valueOf(Choices.END.getId())));
    }

    /**
     * 文字列からChoicesの列挙型で返す
     *
     * @param choices ユーザーが入力した文字
     * @return Choicesの列挙型で返す
     * @throws NullPointerException 引数が例外の場合
     */
    static Choices getChoicesEnum(String choices) throws NullPointerException {
        switch (choices) {
            case "1":
                return Choices.START;
            case "2":
                return Choices.STOP;
            case "3":
                return Choices.END;
            default:
                return Choices.END;
        }
    }

    /**
     * サーバーを操作するメソッド
     *
     * @param choices 選択した文字
     * @return サーバーの状態を文字列で返す
     * @throws IOException          {@link Server}を参照
     * @throws ServerStateException サーバークラスの状態が予期しないものになった場合に発生する
     * @throws CsvRuntimeException  {@link CsvWriter}を参照
     * @throws NullPointerException 引数がnullの場合
     */
    static String controlServer(Server server, Choices choices) throws IOException, SocketRuntimeException,
            ServerStateException, CsvRuntimeException, NullPointerException {

        Objects.requireNonNull(server);
        Objects.requireNonNull(choices);

        switch (choices) {

            case START:
                switch (server.getState()) {
                    case TERMINATED:
                        server = new Server();

                    case NEW:
                        server.startServer();
                        return "start up http server..";

                    case RUNNABLE:
                        return "http server is already running..";

                    default:
                        server.endServer();
                        throw new ServerStateException(server.getState());
                }

            case STOP:
                switch (server.getState()) {
                    case NEW:

                    case TERMINATED:
                        return "http server is not running..";

                    case RUNNABLE:
                        if (server.stopServer()) {
                            return "http server stops..";
                        } else {
                            return "wait a second, http server is returning a response..";
                        }

                    default:
                        server.endServer();
                        throw new ServerStateException(server.getState());
                }

            case END:
                server.endServer();
                return "bye..";

            default:
                return null;
        }
    }
}

/**
 * controllerServerのメソッドの引数で渡す。
 *
 * @author asada
 */
enum Choices {
    /**
     * Serverを立ち上げる
     */
    START(1),

    /**
     * Serverを停止させる
     */
    STOP(2),

    /**
     * Serveを終了させる
     */
    END(3);

    private final int id;

    Choices(final int id) {
        this.id = id;
    }

    /**
     * Choicesと対応する数字を返す
     *
     * @return Choicesの添え字
     */
    public int getId() {
        return id;
    }
}
