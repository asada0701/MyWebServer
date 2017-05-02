package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.exception.RequestParseException;

import java.io.*;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by yusuke-pc on 2017/05/01.
 */
public class WebAppHandler extends Handler {

    public static int score = 1;

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
                            System.out.println("editHtmlメソッドを呼び出すところまではできてる");
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

    public void returnResponse(OutputStream os) {
        try {
            String path = HandlerFactory.getFilePath(requestLine.getUri());
            new ResponseMessage(os, statusCode, path);

        } catch (IOException e) {

        }
    }

    public void editHtml(RequestMessage requestMessage) throws IOException {
        System.out.println("editHTMLメソッドスタート");
        System.out.println("リクエストラインのURI" + requestLine.getUri());
        if (requestLine.getUri().startsWith("/program/board/")) {
            //今回のプログラムボードの場合の処理を書く場所

            String param = requestMessage.findMessageBody("param");
            System.out.println("param:" + param);
            if (param != null) {
                if ("contribution".equals(param)) {
                    String name = requestMessage.findMessageBody("name");
                    String email = requestMessage.findMessageBody("email");
                    String title = requestMessage.findMessageBody("title");
                    String message = requestMessage.findMessageBody("message");

                    String[] s = requestLine.getUri().split("/");
                    //少しややこしいがやりたいことは、/program/board/の部分を/2/にしたい
                    StringBuilder builder1 = new StringBuilder();
                    for (int i = 3; i < s.length; i++) {
                        if (i > 3) {
                            //2回目以降
                            builder1.append("/");
                        }
                        builder1.append(s[i]);
                    }

                    String path = HandlerFactory.getFilePath(requestLine.getUri());
                    System.out.println("path:" + path);

                    try (BufferedReader br = new BufferedReader(new FileReader(new File(path)))) {
                        String str;
                        StringBuilder builder2 = new StringBuilder();
                        while ((str = br.readLine()) != null) {
                            if (str.endsWith("<tr id=\"logth\">")) {
                                builder2.append(str).append("\n");
                                for (int i = 0; i < 7; i++) {
                                    str = br.readLine();
                                    builder2.append(str).append("\n");
                                }
                                builder2.append("\n").append(setData(name, title, message)).append("\n");
                            }
                            builder2.append(str).append("\n");
                        }
                        File file = new File(path);
                        file.delete();

                        try (OutputStream os = new FileOutputStream(new File(path))) {
                            os.write(builder2.toString().getBytes());
                            os.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        System.out.println("editHTMLメソッド終了");

    }

    public String setData(String name, String title, String message) {

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
        //"            </tr>";
        score++;
        return str;
    }

    public String getDate() {
        Calendar c = Calendar.getInstance();
        TimeZone tz = TimeZone.getTimeZone("Asia/Tokyo");
        c.setTimeZone(tz);

        StringBuilder builder = new StringBuilder();
        builder.append(c.get(Calendar.YEAR)).append("-").append(c.get(Calendar.MONTH)).append("-").append(c.get(Calendar.DATE));
        builder.append(" ").append(c.get(Calendar.HOUR)).append(c.get(Calendar.MINUTE)).append(c.get(Calendar.SECOND));

        return builder.toString();
    }
}
