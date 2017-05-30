package jp.co.topgate.asada.web;

import org.jetbrains.annotations.Contract;

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
