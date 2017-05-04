package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.exception.RequestParseException;

import java.io.*;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * WebAppの処理を行うハンドラークラス
 *
 * @author asada
 */
public class WebAppHandler extends Handler implements HtmlEditor {
    private int score = 1;

    /**
     * リクエストが来たときに呼び出すメソッド
     *
     * @param bis SocketのInputStreamをBufferedInputStreamにラップして渡す
     */
    @Override
    public void requestComes(BufferedInputStream bis) {
        try {
            RequestMessage requestMessage = new RequestMessage(bis, requestLine);
            String method = requestLine.getMethod();

            String protocolVersion = requestLine.getProtocolVersion();

            if (!"HTTP/1.1".equals(protocolVersion)) {
                statusCode = ResponseMessage.HTTP_VERSION_NOT_SUPPORTED;

            } else if (!"GET".equals(method) && !"POST".equals(method)) {
                statusCode = ResponseMessage.NOT_IMPLEMENTED;

            } else {
                File file = new File(HandlerFactory.getFilePath(requestLine.getUri()));
                if (!file.exists() || !file.isFile()) {
                    statusCode = ResponseMessage.NOT_FOUND;
                } else {
                    statusCode = ResponseMessage.OK;

                    if ("POST".equals(requestLine.getMethod())) {
                        //POSTの時のみHTMLを編集する
                        try {
                            editHtml(requestMessage);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (RequestParseException e) {
            statusCode = ResponseMessage.BAD_REQUEST;
        }
    }

    @Override
    public void editHtml(RequestMessage requestMessage) throws IOException {
        System.out.println("editHTMLメソッドスタート");
        System.out.println("リクエストラインのURI" + requestLine.getUri());
        if (requestLine.getUri().startsWith("/program/board/")) {
            String param = requestMessage.findMessageBody("param");
            if (param != null) {
                switch (param) {
                    case "contribution":
                        contribution(requestMessage);
                        break;
                    case "search":
                        break;
                    case "delete1":
                        break;
                    case "delete2":
                        break;
                    default:
                }
            }
        }
        System.out.println("editHTMLメソッド終了");
    }

    public void contribution(RequestMessage requestMessage) {
        String name = requestMessage.findMessageBody("name");
        String email = requestMessage.findMessageBody("email");
        String title = requestMessage.findMessageBody("title");
        String message = requestMessage.findMessageBody("message");

        String path = HandlerFactory.getFilePath(requestLine.getUri());

        try (BufferedReader br = new BufferedReader(new FileReader(new File(path)))) {
            String str;
            StringBuilder builder = new StringBuilder();
            while ((str = br.readLine()) != null) {
                if (str.endsWith("<div id=\"log\">")) {
                    builder.append(str).append("\n");
                    for (int i = 0; i < 9; i++) {
                        str = br.readLine();
                        builder.append(str).append("\n");
                    }
                    builder.append("\n").append(setData(name, title, message)).append("\n");
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

    private String setData(String name, String title, String message) {
        String str =
                "            <tr>\n" +
                        "                <td>" + score + "</td>\n" +
                        "                <td>" + title + "</td>\n" +
                        "                <td>" + message + "</td>\n" +
                        "                <td>" + name + "</td>\n" +
                        "                <td>" + getDate() + "</td>\n" +
                        "                <td>\n" +
                        "                    <form action=\"/program/board/\" method=\"post\">\n" +
                        "                        <input type=\"hidden\" name=\"param\" value=\"delete1\">\n" +
                        "                        <input type=\"submit\" value=\"このコメントを削除する\">\n" +
                        "                    </form>\n" +
                        "                </td>\n";
        score++;
        return str;
    }

    private String getDate() {
        Calendar c = Calendar.getInstance();
        TimeZone tz = TimeZone.getTimeZone("Asia/Tokyo");
        c.setTimeZone(tz);

        String s = String.valueOf(c.get(Calendar.YEAR)) + "-" + c.get(Calendar.MONTH) + "-" + c.get(Calendar.DATE) +
                " " + c.get(Calendar.HOUR) + ":" + c.get(Calendar.MINUTE);

        return s;
    }
}
