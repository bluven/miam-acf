package cn.com.adcc.miamacfinter.aid.states;

import cn.com.adcc.miamacfinter.aid.beans.ACKBean;
import cn.com.adcc.miamacfinter.aid.beans.CommandFileBean;
import cn.com.adcc.miamacfinter.aid.beans.CommandLDUBean;

/**
 * Created by bluven on 14-8-4.
 */
public class WaitACKState extends State {

    private CommandFileBean fileBean;

    private CommandLDUBean lduWaitACK;

    public WaitACKState(CommandFileBean fileBean, CommandLDUBean lduBean){
        this.fileBean = fileBean;
        this.lduWaitACK = lduBean;
    }

    public void handleACK(ACKBean bean){

        if(this.lduWaitACK.matchACK(bean)){

            CommandLDUBean nextLDU = this.fileBean.nextLDU();

            if(nextLDU == null){

                context.transferTo(new LinkIdleState());
                context.triggerFileSentEvent(fileBean.getFileId(), true);

                return;
            }

            context.transmit(nextLDU.getRtsBean());

            context.transferTo(new WaitCTSState(this.fileBean, nextLDU));

        } else {
            // todo: 异常处理
        }
    }
}
