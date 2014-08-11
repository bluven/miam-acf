package cn.com.adcc.miamacfinter;

import cn.com.adcc.miamacfinter.aid.clients.*;
import cn.com.adcc.miamacfinter.aid.handlers.IFileHandler;
import cn.com.adcc.miamacfinter.aid.beans.CommandFileBean;

/**
 * Created by bluven on 14-8-5.
 */
public class Main {

    public static void main(String[] args) throws Exception {

        testClient();
    }


    public static void testClient(){

        Client client = SocketClient.newInstance();

        client.registerFileHandler(new IFileHandler() {

            public void onReceived(CommandFileBean fileBean) {
                System.out.println("File received");
            }

            public void onSentResult(int fileId, boolean result) {

            }
        });

        // 374: cabin
        // 304: cmu
        client.connect("374");
//        client.connect("127.0.0.1", 1234, "374", "304");
    }
}
