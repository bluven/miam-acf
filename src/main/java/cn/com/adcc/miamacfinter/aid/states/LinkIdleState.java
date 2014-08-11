package cn.com.adcc.miamacfinter.aid.states;

import cn.com.adcc.miamacfinter.ProtocolConstants;
import cn.com.adcc.miamacfinter.aid.clients.IContext;
import cn.com.adcc.miamacfinter.aid.beans.CTSBean;
import cn.com.adcc.miamacfinter.aid.beans.CommandFileBean;
import cn.com.adcc.miamacfinter.aid.beans.CommandLDUBean;
import cn.com.adcc.miamacfinter.aid.beans.RTSBean;

import java.util.TimerTask;

/**
 * Created by bluven on 14-8-3.
 */
public class LinkIdleState extends State {

    public void handleRTS(RTSBean rts){

        CTSBean cts = new CTSBean();

        cts.setLabel(context.getCmuLabel());
        cts.setDst(rts.getDst());
        cts.setWordCount(rts.getWordCount());

        context.transmit(cts);

        // 进行T9倒计时，到时结束会将状态切换成LinkIdleState
        // todo: 需要在数据接收完成后关闭该任务

        TimerTask t9 = new TimerTask() {

            @Override
            public void run() {
                context.transferTo(new LinkIdleState());
            }
        };

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

    /*
    public static void sendFile(IContext context, CommandFileBean fileBean){

        if(context.isProcessingAnyFile()){
            throw new BaseException("One file is being processed.");
        }

        context.setOutputFileBean(fileBean);

        CommandLDUBean firstLDU = fileBean.nextLDU();

        context.setOutLduBean(firstLDU);

        context.transmit(firstLDU.getRtsBean());

        context.transferTo(new WaitCTSState(fileBean));
    }
    */
}
