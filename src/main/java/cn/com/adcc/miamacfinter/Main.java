package cn.com.adcc.miamacfinter;

import cn.com.adcc.miamacfinter.aid.clients.*;
import cn.com.adcc.miamacfinter.aid.handlers.IFileHandler;
import cn.com.adcc.miamacfinter.aid.beans.CommandFileBean;

import java.util.TimerTask;

/**
 * Created by bluven on 14-8-5.
 */
public class Main {

    public static void main(String[] args) throws Exception {

        testClient();
    }


    public static void testClient(){

        final IClient client = SocketClient.newInstance();

        client.registerFileHandler(new IFileHandler() {

            public void onReceived(String content) {

                System.out.println(content.length());
                System.out.println(content);

                System.out.println("File received");
            }

            public void onSentResult(int fileId, boolean result) {
                System.out.println("File Sent Result: " + fileId  + ", " + result);
            }
        });

        // 374: cabin
        // 304: cmu
        client.connect("374");
//        client.connect("127.0.0.1", 1234, "374", "304");


        /*
        client.schedule(new TimerTask() {
            @Override
            public void run() {

                StringBuilder content = new StringBuilder();

                content.append("Hello,");

                for(int i = 0; i < 1319; i++){
                    content.append("A");
                }

                content.append(",AID");

                //client.sendFile(1, content.toString());
            }
        }, 5000);
        */
    }

}
