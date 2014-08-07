package cn.com.adcc.miamacfinter.aid.clients;

import cn.com.adcc.miamacfinter.aid.beans.*;
import cn.com.adcc.miamacfinter.aid.exceptions.BaseException;
import cn.com.adcc.miamacfinter.aid.handlers.IFileReceivedHandler;
import cn.com.adcc.miamacfinter.aid.states.IState;
import cn.com.adcc.miamacfinter.aid.states.InitialState;
import cn.com.adcc.miamacfinter.aid.utils.ClientUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.util.CharsetUtil;

import java.util.logging.Logger;

/**
 * Created by bluven on 14-8-1.
 */
public class NettyClient extends ChannelInboundHandlerAdapter implements IContext {

    private String aidLabel;

    private String cmuLabel;

    private String host;

    private Integer port;

    private Channel channel;

    // 待发送文件
    private CommandFileBean outFileBean;

    // 在发送的ldubean
    private CommandLDUBean outLduBean;

    // 待接收文件
    private CommandFileBean fileBean;

    private CommandLDUBean lduBean;

    private RTSBean rtsBean;

    private IFileReceivedHandler fileReceivedHandler;

    private ChannelHandlerContext ctx;

    private static Logger log = Logger.getLogger(NettyClient.class.getName());

    private static NettyClient singleton;

    public IState state;

    private NettyClient(){

        this.state = new InitialState(this);
    }

    public static NettyClient newInstance(){

        if(NettyClient.singleton == null){
            NettyClient.singleton = new NettyClient();
        }

        return NettyClient.singleton;
    }

    public void connect(String host, int port, String aidLabel, String cmuLabel) {

        if(channel != null){
            throw new BaseException("已经有了链接");
        }

        Bootstrap bootstrap = new Bootstrap();

        EventLoopGroup loopGroup = new NioEventLoopGroup();

        bootstrap.group(loopGroup)
                 .channel(NioSocketChannel.class)
                 .option(ChannelOption.SO_KEEPALIVE, true)
                 .handler(new ChannelInitializer<SocketChannel>() {
                     public void initChannel(SocketChannel ch) throws Exception {
                         ch.pipeline().addLast(new LineBasedFrameDecoder(50))
                                      .addLast(new StringDecoder(CharsetUtil.UTF_8))
                                      .addLast(new Handler(NettyClient.this));
                                      //.addLast(NettyClient.this);
                    }
                 });


        try {

            ChannelFuture future = bootstrap.connect(host, port).sync();

            this.host = host;
            this.port = port;
            this.aidLabel = aidLabel;
            this.cmuLabel = cmuLabel;
            this.channel = future.channel();

            this.channel.closeFuture().sync();

        } catch (Exception e) {
            throw new BaseException(e);
        } finally {
            loopGroup.shutdownGracefully();
        }

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        this.state.onConnected();
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ClientUtils.handleInputData(this.state, (String)msg);
    }

    public void subscribe(){
        String command = "add,0," + aidLabel;
        this.sendCommand(command);
    }

    public void lock() {
        this.sendCommand("lock,0");
    }

    public boolean isProcessingAnyFile() {
        return this.fileBean != null || this.outFileBean != null;
    }

    public void sendCommand(String command){

        command += "\n";

        ByteBuf encoded = channel.alloc().buffer(command.length()).writeBytes(command.getBytes());

        channel.write(encoded);

        channel.flush();
    }

    public void transmit(IBean bean){
        String command = new StringBuilder("transmit,0,").append(bean.asWord().toUpperCase()).toString();
        sendCommand(command);
    }

    public void transferTo(IState state){
        this.setState(state);
        state.setContext(this);
    }


    public void sendALO() {

        ALOBean bean = new ALOBean();

        bean.setLabel(this.cmuLabel);

        bean.setSal(Integer.parseInt(this.aidLabel));

        bean.setVersion(1);

        this.transmit(bean);
    }

    public void onFileReceived(IFileReceivedHandler handler){
        this.fileReceivedHandler = handler;
    }

    public void receiveFile(){
        System.out.println(this.fileBean);
        if(this.fileBean != null){
            this.receiveFile(this.fileBean);
        }
    }

    public void receiveFile(CommandFileBean fileBean){
        if(fileReceivedHandler != null){
            fileReceivedHandler.handle(fileBean);
        }
    }

    class Handler extends ChannelInboundHandlerAdapter {

        private NettyClient client;

        public Handler(NettyClient client){
            this.client = client;
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            this.client.ctx = ctx;
            this.client.state.onConnected();
        }

        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ClientUtils.handleInputData(this.client.state, (String)msg);
        }
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public CommandFileBean getOutFileBean() {
        return outFileBean;
    }

    public void setOutFileBean(CommandFileBean outFileBean) {
        this.outFileBean = outFileBean;
    }

    public CommandLDUBean getOutLduBean() {
        return outLduBean;
    }

    public void setOutLduBean(CommandLDUBean outLduBean) {
        this.outLduBean = outLduBean;
    }

    public CommandFileBean getFileBean() {
        return fileBean;
    }

    public void setFileBean(CommandFileBean fileBean) {
        this.fileBean = fileBean;
    }

    public CommandLDUBean getLduBean() {
        return lduBean;
    }

    public void setLduBean(CommandLDUBean lduBean) {
        this.lduBean = lduBean;
    }

    public RTSBean getRtsBean() {
        return rtsBean;
    }

    public void setRtsBean(RTSBean rtsBean) {
        this.rtsBean = rtsBean;
    }

    public IFileReceivedHandler getFileReceivedHandler() {
        return fileReceivedHandler;
    }

    public void setFileReceivedHandler(IFileReceivedHandler fileReceivedHandler) {
        this.fileReceivedHandler = fileReceivedHandler;
    }

    public String getAidLabel() {
        return aidLabel;
    }

    public void setAidLabel(String aidLabel) {
        this.aidLabel = aidLabel;
    }

    public String getCmuLabel() {
        return cmuLabel;
    }

    public void setCmuLabel(String cmuLabel) {
        this.cmuLabel = cmuLabel;
    }

    public IState getState() {
        return this.state;
    }

    public void setState(IState state) {
        this.state = state;
    }

}


