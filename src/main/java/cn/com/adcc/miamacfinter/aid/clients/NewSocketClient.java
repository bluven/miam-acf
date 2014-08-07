package cn.com.adcc.miamacfinter.aid.clients;

import cn.com.adcc.miamacfinter.aid.exceptions.BaseException;
import cn.com.adcc.miamacfinter.aid.states.InitialState;
import cn.com.adcc.miamacfinter.aid.utils.ClientUtils;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by bluven on 14-8-7.
 */
public class NewSocketClient extends Client {

    private Socket socket;

    private static NewSocketClient singleton;

    private NewSocketClient(){
        this.setState(new InitialState(this));
    }

    public static Client newInstance(){

        if(NewSocketClient.singleton == null){
            NewSocketClient.singleton = new NewSocketClient();
        }

        return NewSocketClient.singleton;
    }


    public void connect(String host, int port, String aidLabel, String cmuLabel) {

        if(this.socket != null){
            throw new BaseException("已经有连接!!!");
        }

        final Socket socket = new Socket();

        try{
            socket.setKeepAlive(true);
            socket.setTcpNoDelay(true);
            socket.connect(new InetSocketAddress(host, port), 5000);

            this.setHost(host);
            this.setPort(port);
            this.setAidLabel(aidLabel);
            this.setCmuLabel(cmuLabel);

            this.socket = socket;
            this.getState().onConnected();

        } catch (Exception e){
            throw new BaseException(e);
        }

        Thread reader = new Thread(new Runnable(){

            public void run() {

                try {
                    BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    while(true){
                        ClientUtils.handleInputData(NewSocketClient.this.getState(), input.readLine());
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        reader.start();
    }

    public void sendCommand(String command) {

        PrintWriter out = null;

        try {
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(this.socket.getOutputStream())), true);
        } catch (IOException e) {
            throw new BaseException(e);
        }

        command += "\r\n";

        out.write(command);
        out.flush();
    }
}
