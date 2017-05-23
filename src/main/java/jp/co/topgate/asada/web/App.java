package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.util.CsvHelper;
import jp.co.topgate.asada.web.program.board.HtmlEditor;
import jp.co.topgate.asada.web.exception.CsvRuntimeException;
import jp.co.topgate.asada.web.exception.HtmlInitializeException;
import jp.co.topgate.asada.web.exception.ServerStateException;
import jp.co.topgate.asada.web.exception.SocketRuntimeException;
import jp.co.topgate.asada.web.model.ModelController;
import org.apache.commons.lang3.math.NumberUtils;
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

    private static int portNumber;

    /**
     * メインメソッド
     */
    public static void main(String[] args) {
        Server server = setPortNumber();

        try {
            String choice;
            Scanner scanner = new Scanner(System.in);

            ModelController.setMessageList(CsvHelper.readMessage());                            //CSVファイル読み込み
            HtmlEditor htmlEditor = new HtmlEditor();                                                   //HTMLファイル読み込み

            do {
                System.out.println("--------------------");
                System.out.println(ServerCommand.START.getId() + ": START");
                System.out.println(ServerCommand.STOP.getId() + ": STOP");
                System.out.println(ServerCommand.END + ": END");

                do {
                    System.out.print("please select :");
                    choice = scanner.next();
                } while (!ServerCommand.contains(choice));

                String message = controlServer(server, ServerCommand.getServerCommand(choice));
                System.out.println(message);

            } while (!choice.equals(ServerCommand.END.getId()));

            CsvHelper.writeMessage(ModelController.getAllMessage());                                //CSVファイルに書き込み
            htmlEditor.resetAllFiles();                                                                 //HTMLファイルの初期化

        } catch (ServerStateException | CsvRuntimeException | SocketRuntimeException | HtmlInitializeException | IOException e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }

    private static Server setPortNumber() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("choose this server's port number : ");
            String userInput = scanner.next();
            try {
                if (NumberUtils.isNumber(userInput)) {
                    Server server = new Server(Integer.parseInt(userInput));
                    portNumber = Integer.parseInt(userInput);
                    return server;
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
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
    static String controlServer(Server server, ServerCommand serverCommand) throws IOException, SocketRuntimeException, ServerStateException {

        switch (serverCommand) {
            case START:
                switch (server.getState()) {
                    case NEW:
                        server.startServer();
                        return "start up http server..";

                    case TERMINATED:
                        server = new Server(portNumber);
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
                        return "http server is not running..";

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
        ServerCommand[] enumArray = ServerCommand.values();

        for (ServerCommand enumStr : enumArray) {
            if (str.equals(enumStr.id)) {
                return enumStr;
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
        return str.equals(START.getId()) ||
                str.equals(STOP.getId()) ||
                str.equals(END.getId());
    }
}
