package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.model.Message;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by yusukenakashima0701 on 2017/05/06.
 */
public class HtmlEditor {

    private RequestLine requestLine;
    private String path;

    public HtmlEditor(RequestLine requestLine) {
        this.requestLine = requestLine;
        path = HandlerFactory.getFilePath(requestLine.getUri());
    }

    /**
     * 投稿するメソッド
     *
     * @param message
     */
    public void contribution(Message message) {
        try (BufferedReader br = new BufferedReader(new FileReader(new File(path)))) {
            String str;
            StringBuilder builder = new StringBuilder();
            while ((str = br.readLine()) != null) {
                if (str.endsWith("<div id=\"log\">")) {
                    builder.append(str).append("\n");
                    do {
                        str = br.readLine();
                        builder.append(str).append("\n");
                    } while (!str.endsWith("</tr>"));
                    builder.append(getContribution(message));
                }
                builder.append(str).append("\n");
            }
            File file = new File(path);
            if (!file.delete()) {
                throw new IOException("存在しないファイルを編集しようとしました。");
            }

            try (OutputStream os = new FileOutputStream(new File(path))) {
                os.write(builder.toString().getBytes());
                os.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 投稿する文字をHTMLに編集する
     *
     * @param message
     * @return
     */
    public String getContribution(Message message) {
        if (message.getText().contains("\n")) {
            message.setText(message.getText().replaceAll("\n", "<br>"));    //改行文字\nを<br>に変換する
        }

        String str = "            <tr id=\"No." + message.getMessageID() + "\">\n" +
                "                <td>No." + message.getMessageID() + "</td>\n" +
                "                <td>" + message.getTitle() + "</td>\n" +
                "                <td>" + message.getText() + "</td>\n" +
                "                <td>" + message.getName() + "</td>\n" +
                "                <td>" + message.getDate() + "</td>\n" +
                "                <td>\n" +
                "                    <form action=\"/program/board/\" method=\"post\">\n" +
                "                        <input type=\"hidden\" name=\"param\" value=\"search\">\n" +
                "                        <input type=\"hidden\" name=\"number\" value=\"" + message.getMessageID() + "\">\n" +
                "                        <input type=\"submit\" value=\"このコメントを投稿した人の他のコメントを見てみる\">\n" +
                "                    </form>\n" +
                "                </td>\n" +
                "                <td>\n" +
                "                    <form action=\"/program/board/\" method=\"post\">\n" +
                "                        <input type=\"hidden\" name=\"param\" value=\"delete1\">\n" +
                "                        <input type=\"hidden\" name=\"number\" value=\"" + message.getMessageID() + "\">\n" +
                "                        <input type=\"submit\" value=\"このコメントを削除する\">\n" +
                "                    </form>\n" +
                "                </td>\n";
        return str;
    }

    /**
     * 投稿した人で抽出するメソッド
     */
    public void search(ArrayList<Message> al) {
        path = HandlerFactory.getFilePath(requestLine.getUri());
        //search.htmlを初期化
        searchInitialization();

        try (BufferedReader br = new BufferedReader(new FileReader(new File(path)))) {
            String str;
            StringBuilder builder = new StringBuilder();
            while ((str = br.readLine()) != null) {
                if (str.endsWith("<div id=\"log\">")) {
                    builder.append(str).append("\n");
                    do {
                        str = br.readLine();
                        builder.append(str).append("\n");
                    } while (!str.endsWith("</tr>"));

                    for (int i = al.size() - 1; i > -1; i--) {
                        builder.append(getContribution(al.get(i)));
                    }
                }
                builder.append(str).append("\n");
            }
            File file = new File(path);
            if (!file.delete()) {
                throw new IOException("存在しないファイルを編集しようとしました。");
            }

            try (OutputStream os = new FileOutputStream(new File(path))) {
                os.write(builder.toString().getBytes());
                os.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void delete1(Message message) {
        path = HandlerFactory.getFilePath(requestLine.getUri());
        deleteInitialization();

        try (BufferedReader br = new BufferedReader(new FileReader(new File(path)))) {
            String str;
            StringBuilder builder = new StringBuilder();
            while ((str = br.readLine()) != null) {
                if (str.endsWith("<div id=\"log\">")) {
                    builder.append(str).append("\n");
                    do {
                        str = br.readLine();
                        builder.append(str).append("\n");
                    } while (!str.endsWith("</tr>"));

                    builder.append(getDelete(message));
                }
                if (str.endsWith("<input type=\"hidden\" name=\"number\" value=\"\">")) {
                    builder.append("            <input type=\"hidden\" name=\"number\" value=\"");
                    builder.append(message.getMessageID()).append("\">").append("\n");

                    str = br.readLine();
                }
                builder.append(str).append("\n");
            }
            File file = new File(path);
            if (!file.delete()) {
                throw new IOException("存在しないファイルを編集しようとしました。");
            }

            try (OutputStream os = new FileOutputStream(new File(path))) {
                os.write(builder.toString().getBytes());
                os.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String getDelete(Message message) {
        if (message.getText().contains("\n")) {
            message.setText(message.getText().replaceAll("\n", "<br>"));    //改行文字\nを<br>に変換する
        }

        String str = "            <tr id=\"No." + message.getMessageID() + "\">\n" +
                "                <td>No." + message.getMessageID() + "</td>\n" +
                "                <td>" + message.getTitle() + "</td>\n" +
                "                <td>" + message.getText() + "</td>\n" +
                "                <td>" + message.getName() + "</td>\n" +
                "                <td>" + message.getDate() + "</td>\n";
        return str;
    }

    public void delete2(Message message) {
        try (BufferedReader br = new BufferedReader(new FileReader(new File(path)))) {
            String str;
            StringBuilder builder = new StringBuilder();
            while ((str = br.readLine()) != null) {
                if (str.endsWith("<tr id=\"No." + message.getMessageID() + "\">")) {
                    do {
                        str = br.readLine();
                    } while (!str.endsWith("</tr>"));
                }
                builder.append(str).append("\n");
            }
            File file = new File(path);
            if (!file.delete()) {
                throw new IOException("存在しないファイルを編集しようとしました。");
            }

            try (OutputStream os = new FileOutputStream(new File(path))) {
                os.write(builder.toString().getBytes());
                os.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void indexInitialization() {
        try (BufferedReader br = new BufferedReader(new FileReader(new File(path)))) {
            String str = "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "\n" +
                    "<head>\n" +
                    "    <meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">\n" +
                    "    <link rel=\"stylesheet\" type=\"text/css\" href=\"./css/style.css\">\n" +
                    "</head>\n" +
                    "\n" +
                    "<body>\n" +
                    "<center>\n" +
                    "    <div id=\"header\">\n" +
                    "        <h1>掲示板-LightBoard</h1>\n" +
                    "    </div>\n" +
                    "    <div id=\"form\">\n" +
                    "        <form action=\"/program/board/\" method=\"post\">\n" +
                    "            <p>\n" +
                    "                名前<input type=\"text\" name=\"name\" size=\"40\" required>\n" +
                    "            </p>\n" +
                    "            <p>\n" +
                    "                タイトル<input type=\"text\" name=\"title\" size=\"40\" required>\n" +
                    "            </p>\n" +
                    "            <p>\n" +
                    "                メッセージ<br>\n" +
                    "                <textarea name=\"text\" rows=\"4\" cols=\"40\"></textarea>\n" +
                    "            </p>\n" +
                    "            <p>\n" +
                    "                パスワード<input type=\"password\" name=\"password\" size=\"10\" required>(投稿した文を削除するときに使います。)\n" +
                    "            </p>\n" +
                    "            <input type=\"hidden\" name=\"param\" value=\"contribution\">\n" +
                    "            <input type=\"submit\" value=\"投稿\">\n" +
                    "        </form>\n" +
                    "    </div>\n" +
                    "    <div id=\"log\">\n" +
                    "        <table border=\"1\">\n" +
                    "            <tr>\n" +
                    "                <th>ナンバー</th>\n" +
                    "                <th>タイトル</th>\n" +
                    "                <th>本文</th>\n" +
                    "                <th>ユーザー名</th>\n" +
                    "                <th>日付</th>\n" +
                    "                <th></th>\n" +
                    "                <th></th>\n" +
                    "            </tr>\n" +
                    "        </table>\n" +
                    "    </div>\n" +
                    "</center>\n" +
                    "</body>\n" +
                    "\n" +
                    "</html>\n";
            File file = new File(path);
            if (!file.delete()) {
                throw new IOException("存在しないファイルを編集しようとしました。");
            }

            try (OutputStream os = new FileOutputStream(new File(path))) {
                os.write(str.getBytes());
                os.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void searchInitialization() {
        String str = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "\n" +
                "<head>\n" +
                "    <meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">\n" +
                "    <link rel=\"stylesheet\" type=\"text/css\" href=\"./css/style.css\">\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                "<center>\n" +
                "    <div id=\"header\">\n" +
                "        <h1>掲示板-LightBoard</h1>\n" +
                "    </div>\n" +
                "    <div id=\"form\">\n" +
                "        <form action=\"/program/board/\" method=\"post\">\n" +
                "            <p>\n" +
                "                名前<input type=\"text\" name=\"name\" size=\"40\" required>\n" +
                "            </p>\n" +
                "            <p>\n" +
                "                タイトル<input type=\"text\" name=\"title\" size=\"40\" required>\n" +
                "            </p>\n" +
                "            <p>\n" +
                "                メッセージ<br>\n" +
                "                <textarea name=\"text\" rows=\"4\" cols=\"40\"></textarea>\n" +
                "            </p>\n" +
                "            <p>\n" +
                "                パスワード<input type=\"password\" name=\"password\" size=\"10\" required>(投稿した文を削除するときに使います。)\n" +
                "            </p>\n" +
                "            <input type=\"hidden\" name=\"param\" value=\"contribution\">\n" +
                "            <input type=\"submit\" value=\"投稿\">\n" +
                "        </form>\n" +
                "    </div>\n" +
                "    <div id=\"log\">\n" +
                "        <table border=\"1\">\n" +
                "            <tr>\n" +
                "                <th>ナンバー</th>\n" +
                "                <th>タイトル</th>\n" +
                "                <th>本文</th>\n" +
                "                <th>ユーザー名</th>\n" +
                "                <th>日付</th>\n" +
                "                <th></th>\n" +
                "                <th></th>\n" +
                "            </tr>\n" +
                "        </table>\n" +
                "    </div>\n" +
                "    <div id=\"back\">\n" +
                "        <form action=\"/program/board/\" method=\"post\">\n" +
                "            <input type=\"hidden\" name=\"param\" value=\"back\">\n" +
                "            <input type=\"submit\" value=\"topへ戻る\">\n" +
                "        </form>\n" +
                "    </div>\n" +
                "</center>\n" +
                "</body>\n" +
                "\n" +
                "</html>\n";
        File file = new File(path);
        if (!file.delete()) {
            System.out.println("存在しないファイルを編集しようとしました。");
        }

        try (OutputStream os = new FileOutputStream(new File(path))) {
            os.write(str.getBytes());
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteInitialization() {
        String str = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "\n" +
                "<head>\n" +
                "    <meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">\n" +
                "    <link rel=\"stylesheet\" type=\"text/css\" href=\"./css/deleteStyle.css\">\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                "<center>\n" +
                "    <div id=\"header\">\n" +
                "        <h1>掲示板-LightBoard</h1>\n" +
                "    </div>\n" +
                "    <div id=\"log\">\n" +
                "        <h2>削除するメッセージ</h2>\n" +
                "        <table border=\"1\">\n" +
                "            <tr>\n" +
                "                <th>ナンバー</th>\n" +
                "                <th>タイトル</th>\n" +
                "                <th>本文</th>\n" +
                "                <th>ユーザー名</th>\n" +
                "                <th>日付</th>\n" +
                "            </tr>\n" +
                "        </table>\n" +
                "    </div>\n" +
                "    <div id=\"form\">\n" +
                "        <p>投稿した時に入力したパスワードを入力してください。</p>\n" +
                "        <form action=\"/program/board/\" method=\"post\">\n" +
                "            <p>\n" +
                "                パスワード<input type=\"password\" name=\"pw\" size=\"10\" required>\n" +
                "            </p>\n" +
                "            <input type=\"hidden\" name=\"number\" value=\"\">\n" +
                "            <input type=\"hidden\" name=\"param\" value=\"delete2\">\n" +
                "            <input type=\"submit\" value=\"削除する\">\n" +
                "        </form>\n" +
                "    </div>\n" +
                "    <div id=\"back\">\n" +
                "        <form action=\"/program/board/\" method=\"post\">\n" +
                "            <input type=\"hidden\" name=\"param\" value=\"back\">\n" +
                "            <input type=\"submit\" value=\"戻る\">\n" +
                "        </form>\n" +
                "    </div>\n" +
                "</center>\n" +
                "</body>\n" +
                "\n" +
                "</html>\n";
        File file = new File(path);
        if (!file.delete()) {
            System.out.println("存在しないファイルを編集しようとしました。");
        }

        try (OutputStream os = new FileOutputStream(new File(path))) {
            os.write(str.getBytes());
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
