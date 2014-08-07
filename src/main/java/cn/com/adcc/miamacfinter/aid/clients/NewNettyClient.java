package cn.com.adcc.miamacfinter.aid.clients;

import cn.com.adcc.miamacfinter.aid.exceptions.BaseException;
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

/**
 * Created by bluven on 14-8-7.
 */
public class NewNettyClient extends Client {

    private Channel channel;

    private ChannelHandlerContext ctx;

    private static NewNettyClient singleton;

    private NewNettyClient(){
        this.setState(new InitialState(this));
    }

    public static NewNettyClient newInstance(){

        if(NewNettyClient.singleton == null){
            NewNettyClient.singleton = new NewNettyClient();
        }

        return NewNettyClient.singleton;
    }

    @Override
    public void close() {

        try {
            this.channel.closeFuture().sync();
        } catch (InterruptedException e) {
            throw new BaseException(e);
        }
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
                                      .addLast(new Handler(NewNettyClient.this));
                                      //.addLast(NettyClient.this);
                    }
                 });


        try {

            ChannelFuture future = bootstrap.connect(host, port).sync();
            this.channel = future.channel();

            this.setHost(host);
            this.setPort(port);
            this.setAidLabel(aidLabel);
            this.setCmuLabel(cmuLabel);


        } catch (Exception e) {
            throw new BaseException(e);
        } finally {
            loopGroup.shutdownGracefully();
        }

    }

    class Handler extends ChannelInboundHandlerAdapter {

        private NewNettyClient client;

        public Handler(NewNettyClient client){
            this.client = client;
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            this.client.ctx = ctx;
            this.client.getState().onConnected();
        }

        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ClientUtils.handleInputData(this.client.getState(), (String) msg);
        }
    }

    @Override
    public void sendCommand(String command){

        command += "\n";

        ByteBuf encoded = this.channel.alloc().buffer(command.length()).writeBytes(command.getBytes());

        this.channel.write(encoded);

        this.channel.flush();
    }
}
