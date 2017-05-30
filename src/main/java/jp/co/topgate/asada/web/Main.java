package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.program.board.model.ModelController;
import jp.co.topgate.asada.web.util.CsvHelper;
import jp.co.topgate.asada.web.exception.CsvRuntimeException;
import jp.co.topgate.asada.web.exception.ServerStateException;
import jp.co.topgate.asada.web.exception.SocketRuntimeException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Scanner;

/**
 * クライアントクラス
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
     * サーバーが対応しているプロトコルバージョン
     */
    public static final String PROTOCOL_VERSION = "HTTP/1.1";

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

            do {
                System.out.println("--------------------");

                for (ServerCommand serverCommand : ServerCommand.values()) {
                    System.out.println(serverCommand.getId() + ": " + serverCommand.name());
                }

                do {
                    System.out.print("please select :");
                    choice = scanner.next();
                } while (!ServerCommand.contains(choice));

                ServerMessage serverMessage = controlServer(server, ServerCommand.getServerCommand(choice));
                System.out.println(serverMessage.getMessage());

            } while (!choice.equals(ServerCommand.END.getId()));

            CsvHelper.writeMessage(ModelController.getAllMessage());

        } catch (ServerStateException | CsvRuntimeException | SocketRuntimeException | IOException e) {
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

