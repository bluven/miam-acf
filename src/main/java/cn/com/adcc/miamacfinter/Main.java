package cn.com.adcc.miamacfinter;

import cn.com.adcc.miamacfinter.aid.beans.ALOBean;
import cn.com.adcc.miamacfinter.aid.beans.ALRBean;
import cn.com.adcc.miamacfinter.aid.clients.*;
import cn.com.adcc.miamacfinter.aid.handlers.IFileReceivedHandler;
import cn.com.adcc.miamacfinter.aid.beans.CommandFileBean;
import cn.com.adcc.miamacfinter.aid.utils.Utils;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by bluven on 14-8-5.
 */
public class Main {

    public static void main(String[] args) throws Exception {
        ALOBean alo = ALOBean.parseRaw("473F01", "374");

        System.out.println(alo.asWord());

        System.out.println(Utils.reverseBitsByte(403));
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
