package jp.co.topgate.asada.web;

import jp.co.topgate.asada.web.exception.HttpVersionException;
import jp.co.topgate.asada.web.exception.RequestParseException;
import jp.co.topgate.asada.web.program.board.model.ModelController;
import jp.co.topgate.asada.web.util.CsvHelper;

import java.io.*;
import java.net.*;

/**
 * サーバークラス
 *
 * @author asada
 */
class Server extends Thread {
    /**
     * コンストラクタ
     *
     * @throws IOException サーバーソケットでエラーが発生しました
     */
    static void run(int portNumber) throws IOException {
        ServerSocket serverSocket = new ServerSocket(portNumber);
        while (true) {
            ModelController.setMessageList(CsvHelper.readMessage());

            Socket clientSocket = serverSocket.accept();

            ResponseMessage responseMessage = new ResponseMessage(clientSocket.getOutputStream());

            StatusLine statusLineOfException = null;

            try {
                RequestMessage requestMessage = RequestMessageParser.parse(clientSocket.getInputStream());

                Handler handler = Handler.getHandler(requestMessage, responseMessage);

                handler.handleRequest();

            } catch (RequestParseException e) {                 //リクエストメッセージに問題があった場合の例外処理
                statusLineOfException = StatusLine.BAD_REQUEST;

            } catch (HttpVersionException e) {                  //リクエストメッセージのプロトコルバージョンが想定外のものである
                statusLineOfException = StatusLine.HTTP_VERSION_NOT_SUPPORTED;

            } finally {
                if (statusLineOfException != null) {
                    responseMessage.addHeaderWithContentType(ContentType.ERROR_RESPONSE);
                    PrintWriter printWriter = responseMessage.getPrintWriter(statusLineOfException);
                    printWriter.write(ResponseMessage.getErrorMessageBody(statusLineOfException));
                    printWriter.flush();
                }
            }
            clientSocket.close();

            CsvHelper.writeMessage(ModelController.getAllMessage());
        }
    }
}
