package cn.com.adcc.miamacfinter;

import cn.com.adcc.miamacfinter.aid.clients.*;
import cn.com.adcc.miamacfinter.aid.handlers.IFileReceivedHandler;
import cn.com.adcc.miamacfinter.aid.beans.CommandFileBean;

/**
 * Created by bluven on 14-8-5.
 */
public class Main {

    public static void main(String[] args) throws Exception {
//        testSocket();
//        testNetty();
          testClient();
    }

    public static void testSocket() throws Exception {

        SocketClient client = SocketClient.newInstance();

        client.onFileReceived(new IFileReceivedHandler() {

            public void handle(CommandFileBean fileBean) {
                fileBean.inspect();
            }
        });

        // 374: cabin
        // 304: cmu
        client.connect("192.168.4.253", 8766, "374", "304");
//        client.connect("127.0.0.1", 1234, "374", "304");
    }

    public static void testNetty(){

        NettyClient client = NettyClient.newInstance();

        client.onFileReceived(new IFileReceivedHandler() {

            public void handle(CommandFileBean fileBean) {
                fileBean.inspect();
            }
        });

        // 374: cabin
        // 304: cmu
        client.connect("192.168.4.253", 8766, "374", "304");
        //client.connect("127.0.0.1", 1234, "374", "304");

    }

    public static void testClient(){

        Client client = ConcurrentClient.newInstance();

        client.onFileReceived(new IFileReceivedHandler() {

            public void handle(CommandFileBean fileBean) {
                fileBean.inspect();
            }
        });

        // 374: cabin
        // 304: cmu
        client.connect("192.168.4.253", 8766, "374", "304");
        //client.connect("127.0.0.1", 1234, "374", "304");
    }
}
