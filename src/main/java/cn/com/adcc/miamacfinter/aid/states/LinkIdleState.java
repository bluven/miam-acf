package cn.com.adcc.miamacfinter.aid.states;

import java.util.TimerTask;

import cn.com.adcc.miamacfinter.aid.constants.ProtocolConstants;
import cn.com.adcc.miamacfinter.aid.beans.*;
import cn.com.adcc.miamacfinter.aid.clients.IContext;

/**
 * Created by bluven on 14-8-3.
 */
public class LinkIdleState extends State {

    public void handleRTS(RTSBean rts){

        final IContext context = this.getContext();

        // 收到RTS，取消T14
        context.cancelTask("T14");
        context.cancelTask("T9");

        CTSBean cts = new CTSBean();

        cts.setLabel(super.context.getCmuLabel());
        cts.setDst(rts.getDst());
        cts.setWordCount(rts.getWordCount());

        context.transmit(cts);

        // 进行T9倒计时，到时结束会将状态切换成LinkIdleState
        // todo: 需要在数据接收完成后关闭该任务

        TimerTask t9 = new TimerTask() {

            public void run() {

                // todo: 需要返回NAK
                // 问题： T9是在CTS后SOT前生成，如果T9超时，是不知道File Seq Num的
                // 解决： 假设SOT收到，需要为T9设置File Seq Num, 当T9超时，需要判断File Seq Num是否具备
                // 具备则返回NAK， 不具备则只改变状态

                CommandFileBean fileBean = context.getInputFileBean();

                if(fileBean != null){

                    int fileSeqNum = fileBean.getFileNum();

                    if(CommandFileBean.isValidFileSeqNum(fileSeqNum)){
                        NAKBean nak = new NAKBean();
                        nak.setLabel(context.getCmuLabel());
                        nak.setFileSeqNum(fileSeqNum);
                        // LDU time out
                        nak.setStatusCode(ProtocolConstants.LDU_TIME_OUT_ERR);
                        context.transmit(nak);
                    }
                }
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
        fileBean.resetLduIter();
        sendLDU(context, fileBean, fileBean.nextLdu());
    }

    public static void sendLDU(IContext context, CommandFileBean fileBean, CommandLDUBean ldu){

        context.transmit(ldu.getRtsBean());

        WaitCTSState waitCTS = new WaitCTSState(fileBean, ldu);

        context.transferTo(waitCTS);

        waitCTS.startT5();
    }

}
