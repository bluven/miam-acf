package cn.com.adcc.miamacfinter.aid.states;

import cn.com.adcc.miamacfinter.aid.beans.*;
import cn.com.adcc.miamacfinter.aid.clients.IContext;
import cn.com.adcc.miamacfinter.aid.constants.ProtocolConstants;

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

    public void handleACK(ACKBean bean) {

        IContext context = getContext();

        context.cancelTask("T16");

        CommandLDUBean nextLDU;

        if (this.lduWaitACK.matchACK(bean)) {

            nextLDU = this.fileBean.nextLdu();

            if (nextLDU == null) {
                context.transferTo(new LinkIdleState());
                context.triggerFileSentEvent(fileBean.getFileId(), true);
                return;
            }
        } else {
            // todo: 收到SYN或NAK时都会引起重发，并设有计数器，为什么ldu重发没有
            nextLDU = this.lduWaitACK;
        }

        LinkIdleState.sendLDU(context, fileBean, nextLDU);
    }

    public void handleSYN(SYNBean syn){

        IContext context = getContext();

        context.resetCounter("N4");

        context.incrementCounter("N5");

        if(context.isCounterGreaterThan("N5", ProtocolConstants.N5)){
            context.transferTo(new LinkIdleState());
            context.triggerFileSentEvent(this.fileBean.getFileId(), false);
        } else {
            LinkIdleState.sendFile(context, this.fileBean);
        }
    }

    public void handleNAK(NAKBean nak){

        IContext context = getContext();

        context.cancelTask("T16");

        context.incrementCounter("N4");

        context.resetCounter("N5");

        if(context.isCounterGreaterThan("N4", ProtocolConstants.N4)){
            context.transferTo(new LinkIdleState());
            context.triggerFileSentEvent(this.fileBean.getFileId(), false);
        } else {
            LinkIdleState.sendFile(context, this.fileBean);
        }
    }
}
