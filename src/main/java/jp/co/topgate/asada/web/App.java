package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.exception.BindRuntimeException;
import jp.co.topgate.asada.web.exception.CipherRuntimeException;
import jp.co.topgate.asada.web.exception.CsvRuntimeException;
import jp.co.topgate.asada.web.exception.ServerStateException;
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

            ModelController.setMessageList(CsvWriter.readToMessage());   //CSVファイル読み込み
            HtmlEditor he = new HtmlEditor();                            //HTMLファイル読み込み

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

            CsvWriter.write(CsvMode.MESSAGE_MODE, ModelController.getAllMessage());   //CSVファイルに書き込み
            he.allInitialization();                                                   //HTMLファイルの初期化

        } catch (BindRuntimeException | ServerStateException | CsvRuntimeException |
                CipherRuntimeException | NullPointerException e) {

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
     * @throws IOException            サーバークラスで発生した入出力エラー
     * @throws BindRuntimeException   サーバークラスで発生したバインド例外
     * @throws ServerStateException   サーバークラスの状態が予期しないものになった場合に発生する
     * @throws CsvRuntimeException    CSVファイルの読み込み中か、読み込む段階で例外が発生した
     * @throws CipherRuntimeException 読み込んだデータの複合に失敗した
     * @throws NullPointerException   引数がnullの場合
     */
    static String controlServer(Server server, String choices) throws IOException, BindRuntimeException,
            ServerStateException, CsvRuntimeException, CipherRuntimeException, NullPointerException {
        Objects.requireNonNull(server);
        Objects.requireNonNull(choices);

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
