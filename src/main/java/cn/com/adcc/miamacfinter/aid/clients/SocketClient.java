package cn.com.adcc.miamacfinter.aid.clients;

import cn.com.adcc.miamacfinter.aid.exceptions.BaseException;
import cn.com.adcc.miamacfinter.aid.states.InitialState;
import cn.com.adcc.miamacfinter.aid.utils.ClientUtils;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Timer;

/**
 * Created by bluven on 14-8-7.
 */
public class SocketClient extends Client {

    private transient boolean running;

    private Socket socket;

    private Thread readThread;

    private static SocketClient singleton;

    protected SocketClient(){

        this.setTimer(new Timer());
        this.setState(new InitialState(this));
    }

    public static Client newInstance(){

        if(SocketClient.singleton == null){
            SocketClient.singleton = new SocketClient();
        }

        return SocketClient.singleton;
    }


    @Override
    public void close() {
        try {
            this.socket.close();
            this.running = false;
            this.readThread.join(1000);

        } catch (Exception e) {
            throw new BaseException(e);
        }
    }

    public void connect(String host, int port, String aidLabel, String cmuLabel) {

        this.doConnect(host, port, aidLabel, cmuLabel);

        this.startRead();
    }

    public void doConnect(String host, int port, String aidLabel, String cmuLabel){

        if(this.socket != null){
            throw new BaseException("已经有连接!!!");
        }

        Socket socket = new Socket();

        try {

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
    }

    public void startRead(){

        this.readThread = new Thread(new Runnable(){

            public void run() {

                try {
                    BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    while(true){
                        ClientUtils.handleInputData(SocketClient.this.getState(), input.readLine());
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        this.readThread.start();
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

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }
}
