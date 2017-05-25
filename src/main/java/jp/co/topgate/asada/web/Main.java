package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.util.CsvHelper;
import jp.co.topgate.asada.web.program.board.HtmlEditor;
import jp.co.topgate.asada.web.exception.CsvRuntimeException;
import jp.co.topgate.asada.web.exception.HtmlInitializeException;
import jp.co.topgate.asada.web.exception.ServerStateException;
import jp.co.topgate.asada.web.exception.SocketRuntimeException;
import jp.co.topgate.asada.web.model.ModelController;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Scanner;

/**
 * クライアントクラス
 * git テスト2
 *
 * @author asada
 */
public class Main {

    /**
     * サーバーのポート番号
     */
    private static final int PORT_NUMBER = 8080;

    /**
     * サーバーが扱う文字符号化スキーム
     */
    public static final String CHARACTER_ENCODING_SCHEME = "UTF-8";

    /**
     * サーバーのウェルカムページのファイル名
     */
    public static final String WELCOME_PAGE_NAME = "index.html";

    /**
     * メインメソッド
     */
    public static void main(String[] args) {
        System.out.println("this server's port number is " + PORT_NUMBER);
        try {
            String choice;
            Server server = new Server(PORT_NUMBER);
            Scanner scanner = new Scanner(System.in);

            ModelController.setMessageList(CsvHelper.readMessage());
            HtmlEditor htmlEditor = new HtmlEditor();

            do {
                System.out.println("--------------------");
                System.out.println(ServerCommand.START.getId() + ": START");
                System.out.println(ServerCommand.STOP.getId() + ": STOP");
                System.out.println(ServerCommand.END + ": END");

                do {
                    System.out.print("please select :");
                    choice = scanner.next();
                } while (!ServerCommand.contains(choice));

                ServerMessage serverMessage = controlServer(server, ServerCommand.getServerCommand(choice));
                System.out.println(serverMessage.getMessage());

            } while (!choice.equals(ServerCommand.END.getId()));

            CsvHelper.writeMessage(ModelController.getAllMessage());
            htmlEditor.resetAllFiles();

        } catch (ServerStateException | CsvRuntimeException | SocketRuntimeException | HtmlInitializeException | IOException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    /**
     * サーバーを操作するメソッド
     *
     * @param serverCommand ユーザーが選択したサーバーの操作
     * @return サーバーの状態を文字列で返す
     * @throws IOException            {@link Server}を参照
     * @throws SocketRuntimeException {@link Server#run()}を参照
     * @throws ServerStateException   サーバークラスの状態が予期しないものになった場合に発生する
     */
    @NotNull
    static ServerMessage controlServer(Server server, ServerCommand serverCommand) throws IOException, SocketRuntimeException, ServerStateException {
        switch (serverCommand) {
            case START:
                switch (server.getState()) {
                    case NEW:
                        server.startServer();
                        return ServerMessage.START;

                    case TERMINATED:
                        server = new Server(PORT_NUMBER);
                        server.startServer();
                        return ServerMessage.START;

                    case RUNNABLE:
                        return ServerMessage.ALREADY_RUNNING;

                    default:
                        server.endServer();
                        throw new ServerStateException(server.getState());
                }

            case STOP:
                switch (server.getState()) {
                    case NEW:
                        return ServerMessage.ALREADY_STOP;

                    case TERMINATED:
                        return ServerMessage.ALREADY_STOP;

                    case RUNNABLE:
                        if (server.stopServer()) {
                            return ServerMessage.STOP;
                        } else {
                            return ServerMessage.CAN_NOT_STOP;
                        }

                    default:
                        server.endServer();
                        throw new ServerStateException(server.getState());
                }

            case END:
                server.endServer();
                return ServerMessage.END;

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
enum ServerCommand {
    /**
     * Serverを立ち上げる
     */
    START("1"),

    /**
     * Serverを停止させる
     */
    STOP("2"),

    /**
     * Serveを終了させる
     */
    END("3");

    private final String id;

    ServerCommand(String id) {
        this.id = id;
    }

    @Contract(pure = true)
    public String getId() {
        return id;
    }

    /**
     * このメソッドは、文字列を元に、enumを返します。
     *
     * @param str 文字列（例）1
     * @return Enum（例）Param.START
     */
    public static ServerCommand getServerCommand(String str) {
        ServerCommand[] array = ServerCommand.values();

        for (ServerCommand serverCommand : array) {
            if (str.equals(serverCommand.id)) {
                return serverCommand;
            }
        }
        return ServerCommand.END;
    }

    /**
     * ユーザーが入力した文字がServerCommandに登録されているか判定するメソッド
     *
     * @param str ユーザーが入力した文字
     * @return trueの場合はServerCommandに登録されている。falseの場合は登録されていない。
     */
    static boolean contains(String str) {
        ServerCommand[] array = ServerCommand.values();

        for (ServerCommand serverCommand : array) {
            if (str.equals(serverCommand.id)) {
                return true;
            }
        }
        return false;
    }
}

/**
 * サーバーの状態をユーザーに伝える時に使うメッセージのEnum
 */
enum ServerMessage {

    /**
     * サーバーを立ち上げた
     */
    START("start up http server.."),

    /**
     * サーバーがすでに立ち上がっていたので、立ち上げれなかった
     */
    ALREADY_RUNNING("http server is already running.."),

    /**
     * サーバーを停止した
     */
    STOP("http server stops.."),

    /**
     * サーバーがすでに停止していたので、停止できなかった
     */
    ALREADY_STOP("http server is not running.."),

    /**
     * サーバーがクライアントにレスポンスをしている途中であるため、停止できなかった
     */
    CAN_NOT_STOP("wait a second, http server is returning a response.."),

    /**
     * サーバーを終了した
     */
    END("bye..");

    private final String message;

    ServerMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
