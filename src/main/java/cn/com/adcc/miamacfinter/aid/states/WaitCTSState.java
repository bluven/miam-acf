package cn.com.adcc.miamacfinter.aid.states;

import cn.com.adcc.miamacfinter.ProtocolConstants;
import cn.com.adcc.miamacfinter.aid.beans.*;

import java.util.TimerTask;

/**
 * Created by bluven on 14-8-4.
 */
public class WaitCTSState extends State {

    private CommandFileBean fileBean;

    // 待发送的ldu
    private CommandLDUBean lduToSend;

    private TimerTask t5;

    private int n1Counter = 0;

    public WaitCTSState(CommandFileBean fileBean, CommandLDUBean lduBean){
        this.fileBean = fileBean;
        this.lduToSend = lduBean;
    }

    public void startT5(){

        this.t5 = new TimerTask() {

            private int n3Counter = 1;

            @Override
            public void run() {

                if(!(context.getState() instanceof WaitCTSState)){
                    this.cancel();
                    return;
                }

                if(n3Counter < ProtocolConstants.N3){

                    this.n3Counter -= 1;
                    context.transmit(lduToSend.getRtsBean());
                } else {
                    context.transferTo(new LinkIdleState());
                    context.triggerFileSentEvent(fileBean.getFileId(), false);
                    this.cancel();
                }
            }

        };

        super.context.scheduleAtFixedRate(t5, ProtocolConstants.T5_MAX, ProtocolConstants.T5_MAX);
    }

    public void handleCTS(CTSBean bean){

        if(!bean.matchRTS(this.lduToSend.getRtsBean())){
            // todo: 要有状态切换, 还要有RTS重发处理
            return ;
        }

        this.cancelT5();

        context.transmit(lduToSend.getSotBean());

        for(DataBean databean: this.lduToSend.getDataBeans()){
            context.transmit(databean);
        }

        context.transmit(this.lduToSend.getEotBean());

        context.transferTo(new WaitACKState(this.fileBean, this.lduToSend));
    }

    public void handleNCTS(NCTSBean bean){

        this.cancelT5();

        if(this.n1Counter > ProtocolConstants.N1){
            context.transferTo(new LinkIdleState());
            context.triggerFileSentEvent(this.fileBean.getFileId(), false);
            System.out.println("n1 countdown");
            return;
        }

        try {
            Thread.sleep(ProtocolConstants.T2_MAX);
        } catch (InterruptedException e) {

        }

        context.transmit(lduToSend.getRtsBean());
        this.n1Counter += 1;

        this.startT5();
    }

    public void cancelT5(){

        if(this.t5 != null){
            this.t5.cancel();
            this.t5 = null;
        }
    }
}
