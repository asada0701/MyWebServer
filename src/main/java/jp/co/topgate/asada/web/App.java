package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.app.CsvWriter;
import jp.co.topgate.asada.web.app.HtmlEditor;
import jp.co.topgate.asada.web.exception.CsvRuntimeException;
import jp.co.topgate.asada.web.exception.ServerStateException;
import jp.co.topgate.asada.web.exception.SocketRuntimeException;
import jp.co.topgate.asada.web.model.ModelController;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
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

            ModelController.setMessageList(CsvWriter.readMessage());                            //CSVファイル読み込み
            HtmlEditor he = new HtmlEditor();                                                   //HTMLファイル読み込み

            do {
                System.out.println("--------------------");
                System.out.println(Choices.START.getId() + ": START");
                System.out.println(Choices.STOP.getId() + ": STOP");
                System.out.println(Choices.END + ": END");

                do {
                    System.out.print("please select :");
                    choices = scan.next();
                } while (!isSelect(choices));

                String msg = controlServer(server, getChoicesEnum(choices));
                System.out.println(msg);

            } while (!choices.equals(String.valueOf(Choices.END.getId())));

            CsvWriter.writeMessage(ModelController.getAllMessage());                                //CSVファイルに書き込み
            he.allInitialization();                                                                 //HTMLファイルの初期化

        } catch (ServerStateException | CsvRuntimeException | SocketRuntimeException |
                NullPointerException | IOException e) {

            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    /**
     * 文字列を渡すとChoicesの列挙型を返してくれる
     *
     * @param choices 渡す文字列
     * @return 列挙型のChoices
     */
    static boolean isSelect(final String choices) {
        return choices.equals(String.valueOf(Choices.START.getId())) ||
                choices.equals(String.valueOf(Choices.STOP.getId())) ||
                choices.equals(String.valueOf(Choices.END.getId()));
    }

    /**
     * 文字列からChoicesの列挙型で返す
     *
     * @param choices ユーザーが入力した文字
     * @return Choicesの列挙型で返す
     */
    @Contract(pure = true)  //メソッドを実行しても副作用がないことを示す
    static Choices getChoicesEnum(final String choices) {
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
     */
    @NotNull
    static String controlServer(Server server, final Choices choices) throws IOException, SocketRuntimeException,
            ServerStateException, CsvRuntimeException {

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
 * Serverクラスの制御用Enum
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
    @Contract(pure = true)
    public int getId() {
        return id;
    }
}
