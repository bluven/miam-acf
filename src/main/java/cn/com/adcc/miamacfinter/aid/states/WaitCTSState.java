package cn.com.adcc.miamacfinter.aid.states;

import cn.com.adcc.miamacfinter.aid.clients.IContext;
import cn.com.adcc.miamacfinter.aid.constants.ProtocolConstants;
import cn.com.adcc.miamacfinter.aid.beans.*;

import java.util.TimerTask;

/**
 * Created by bluven on 14-8-4.
 */
public class WaitCTSState extends State {

    private CommandFileBean fileBean;

    // 待发送的ldu
    private CommandLDUBean lduToSend;

    // n1, n2计时器共用
    private int counter = 0;

    public WaitCTSState(CommandFileBean fileBean, CommandLDUBean lduBean){
        this.fileBean = fileBean;
        this.lduToSend = lduBean;
    }

    public void handleCTS(CTSBean bean){

        IContext context = getContext();

        this.counter = 0;
        // todo: n2计时器重置, BUSY相关
        context.cancelTask("T5"); // T5跟n3计时器关联，取消t5也同时重置了n3

        if(!bean.matchRTS(this.lduToSend.getRtsBean())){
            // todo: 要有状态切换, 还要有RTS重发处理
            return ;
        }

        context.transmit(lduToSend.getSotBean());

        for(DataBean databean: this.lduToSend.getDataBeans()){
            context.transmit(databean);
        }

        context.transmit(this.lduToSend.getEotBean());

        context.transferTo(new WaitACKState(this.fileBean, this.lduToSend));
    }

    @Override
    public void handleNCTS(NCTSBean bean){
        delayAndResendRTS(ProtocolConstants.N1, ProtocolConstants.T2_MAX);
    }

    @Override
    public void handleBUSY(BUSYBean busy){
        delayAndResendRTS(ProtocolConstants.N2, ProtocolConstants.T4_MAX);
    }

    public void handleRTS(RTSBean rts){

        IContext context = getContext();

        context.cancelTask("T5");
        context.cancelTask("T14");

        context.transferTo(new LinkIdleState());
        // 放弃跟CMU抢夺发送权
        // todo: 保存自己的文件，等待下次发送
    }

    private void delayAndResendRTS(int maxTry, int maxWait){

        IContext context = getContext();

        context.cancelTask("T5");

        if(this.counter > maxTry){
            context.transferTo(new LinkIdleState());
            context.triggerFileSentEvent(this.fileBean.getFileId(), false);
            return;
        }

        try {
            Thread.sleep(maxWait);
        } catch (InterruptedException e) {

        }

        context.transmit(lduToSend.getRtsBean());
        this.counter += 1;

        this.startT5();
    }

    public void startT5(){

        final IContext context = getContext();

        TimerTask t5 = new TimerTask() {

            private int n3Counter = 1;

            @Override
            public void run() {

                if(!(context.getState() instanceof WaitCTSState)){
                    this.cancel();
                    return;
                }

                if(n3Counter < ProtocolConstants.N3){

                    this.n3Counter += 1;
                    context.transmit(lduToSend.getRtsBean());
                } else {
                    context.transferTo(new LinkIdleState());
                    context.triggerFileSentEvent(fileBean.getFileId(), false);
                    this.cancel();
                }
            }

        };

        context.scheduleAtFixedRate(t5, ProtocolConstants.T5_MAX, ProtocolConstants.T5_MAX);

        context.saveTask("T5", t5);
    }
}
