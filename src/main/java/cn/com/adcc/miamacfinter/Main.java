package cn.com.adcc.miamacfinter;

import cn.com.adcc.miamacfinter.aid.clients.*;
import cn.com.adcc.miamacfinter.aid.handlers.IFileHandler;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by bluven on 14-8-5.
 */
public class Main {

    public static void main(String[] args) throws Exception {

        testClient();
    }


    public static void testClient(){

        final StringBuilder contentBuilder = new StringBuilder();

        for(int i = 0; i < 100; i++){
            contentBuilder.append('A');
        }

        contentBuilder.append('b');

        final String testContent = contentBuilder.toString();

        final IClient client = SocketClient.newInstance();

        client.registerFileHandler(new IFileHandler() {

            private int counter = 1;

            public void onReceived(String content) {

                System.out.println(content);

                if(counter < 20){

                    this.counter += 1;

                    //client.sendFile(counter, "test");
                    client.sendFile(counter, testContent + counter);


                    System.out.println(counter);
                }
            }

            public void onSentResult(int fileId, boolean result) {
                System.out.println("File Sent Result: " + fileId  + ", " + result);
            }
        });

        // 374: cabin
        // 304: cmu
        client.connect("374");
//      client.connect("127.0.0.1", 1234, "374", "304");



        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                client.sendFile(1, testContent);
            }
        }, 5000);
    }

}
