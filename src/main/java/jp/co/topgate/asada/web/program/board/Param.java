package jp.co.topgate.asada.web.program.board;

/**
 * POSTで送られてくるparam
 *
 * @author asada
 */
enum Param {

    /**
     * index.htmlでメッセージを投稿する場合
     */
    WRITE("write"),

    /**
     * index.htmlで投稿者の名前で検索する場合
     */
    SEARCH("search"),

    /**
     * index.htmlで投稿したメッセージを削除する場合
     */
    DELETE_STEP_1("delete_step_1"),

    /**
     * delete.htmlでパスワードの確認する場合
     */
    DELETE_STEP_2("delete_step_2"),

    /**
     * index.htmlのページに戻る
     */
    BACK("back");

    private String name;

    public String getName() {
        return name;
    }

    Param(String name) {
        this.name = name;
    }

    /**
     * このメソッドは、文字列を元に、enumを返します。
     *
     * @param str 文字列（例）search
     * @return Enum（例）Param.SEARCH
     */
    public static Param getParam(String str) {
        if (str == null) {
            return null;
        }

        Param[] array = Param.values();

        for (Param param : array) {
            if (str.equals(param.name)) {
                return param;
            }
        }
        return null;
    }
}
