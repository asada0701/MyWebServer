package jp.co.topgate.asada.web;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by yusuke-pc on 2017/04/12.
 */
public class Server extends Thread{
    public static final int portNumber = 8080;
    private ServerSocket serverSocket = null;
    private Socket socket = null;
    private HTTPHandler httpHandler = new HTTPHandler();
    private boolean isServerRun = false;                      //serverRunがtrueの時は動いている

    public Server() throws IOException {
        serverSocket = new ServerSocket(portNumber);
    }

    public void serverStart() {
        isServerRun = true;
        this.start();
    }

    public void serverStop() {
       isServerRun = false;
    }

    public void serverEnd() throws IOException{
        serverStop();
        serverSocket.close();
    }

    public boolean isServerRun() {
        return isServerRun;
    }

    public void run() {
        while (isServerRun) {
            try{
                socket = serverSocket.accept();
                System.out.println("リクエストが来た");
                //レスポンスを返している途中かの判断を行う。trueの場合は終了している
                httpHandler.requestComes(socket.getInputStream(), socket.getOutputStream());
                System.out.println("レスポンス返した");
                socket.close();
                System.out.println("ソケットクローズ");
            }catch(IOException e){
                //ここにくるってことはソケットがおかしい
                e.printStackTrace();
            }
        }
        try{
            if(socket != null){
                socket.close();
            }
        }catch(IOException e){
            //ソケットのクローズに失敗
            e.printStackTrace();
        }
    }
}
