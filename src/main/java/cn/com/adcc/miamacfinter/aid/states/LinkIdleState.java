package cn.com.adcc.miamacfinter.aid.states;

import java.util.TimerTask;

import cn.com.adcc.miamacfinter.ProtocolConstants;
import cn.com.adcc.miamacfinter.aid.clients.IContext;
import cn.com.adcc.miamacfinter.aid.beans.CTSBean;
import cn.com.adcc.miamacfinter.aid.beans.CommandFileBean;
import cn.com.adcc.miamacfinter.aid.beans.CommandLDUBean;
import cn.com.adcc.miamacfinter.aid.beans.RTSBean;

/**
 * Created by bluven on 14-8-3.
 */
public class LinkIdleState extends State {

    public void handleRTS(RTSBean rts){

        final IContext context = this.getContext();

        CTSBean cts = new CTSBean();

        cts.setLabel(super.context.getCmuLabel());
        cts.setDst(rts.getDst());
        cts.setWordCount(rts.getWordCount());

        context.transmit(cts);

        // 进行T9倒计时，到时结束会将状态切换成LinkIdleState
        // todo: 需要在数据接收完成后关闭该任务

        TimerTask t9 = new TimerTask() {

            public void run() {
                context.transferTo(new LinkIdleState());
            }
        };

        // 收到RTS，取消T14
        context.cancelTask("T14");

        context.schedule(t9, ProtocolConstants.T9_MAX);

        context.saveTask("T9", t9);

        context.transferTo(new WaitSOTState(rts));
    }

    @Override
    public void sendFile(CommandFileBean fileBean){
        sendFile(super.context, fileBean);
    }

    public static void sendFile(IContext context, CommandFileBean fileBean){

        CommandLDUBean firstLDU = fileBean.nextLDU();

        context.transmit(firstLDU.getRtsBean());

        WaitCTSState waitCTS = new WaitCTSState(fileBean, firstLDU);

        context.transferTo(waitCTS);

        waitCTS.startT5();
    }

}
