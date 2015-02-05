package cn.com.adcc.miamacfinter.aid.states;

import cn.com.adcc.miamacfinter.aid.constants.ProtocolConstants;
import cn.com.adcc.miamacfinter.aid.beans.*;
import cn.com.adcc.miamacfinter.aid.clients.IContext;

import java.util.TimerTask;

/**
 * 接受数据状态
 *
 * Created by bluven on 14-8-4.
 */

public class WaitEOTState extends State {

    private CommandLDUBean lduBean;

    public WaitEOTState(CommandLDUBean lduBean){
        this.lduBean = lduBean;
    }

    public void handleDataBean(DataBean data){
        lduBean.appendData(data);
    }

    public void handleRTS(RTSBean rts){
        super.context.transferTo(new LinkIdleState());
        super.context.getState().handleRTS(rts);
    }

    public void handleEOT(EOTBean eot){

        IContext context = super.getContext();

        // 在处理EOT时，无论是返回NAK，ACK 或SYN 都有2个分支，一个分支会要求reset T14, 一个分支会要求 启动 T14,
        // 为了安全，启动T14前也应该reset T14，所以把T14 reset放在handleEOT开头处, 如果要移动这行代码请三思
        context.cancelTask("T14");

        if(lduBean == null){
            // todo: 没有LDU时处理, 不太可能发生这种事，除非开发者故意传错数据
            // 什么都不做，会导致对方尝试重发该LDU几次, WaitEOT状态实现handleRTS应该可以
            // 解决lduBean是null的问题, 或者应该直接切换到LinkIdleState

            context.transferTo(new LinkIdleState());
            return;
        }

        SOTBean sotBean = this.lduBean.getSotBean();

        lduBean.setEotBean(eot);

        // todo: CRC validate

        // todo: LDU Num validate, 这个功能在WaitSOT里实现

        if(!lduBean.isAllDataReceived()){
            NAKBean nak = new NAKBean();
            nak.setLabel(context.getCmuLabel());
            nak.setFileSeqNum(sotBean.getFileSeqNum());
            nak.setStatusCode(ProtocolConstants.LDU_WORD_COUNT_ERR);

            context.transmit(nak);

            if(!eot.isFinalEOT()){
                startT14(sotBean);
            }

            context.transferTo(new LinkIdleState());

            return;
        }

        // todo: 判断当前LDU is first of new file
        // 疑问： 这个判断为什么不放在sot的时候做, EOT并没有文件相关信息

        ACKBean ack = new ACKBean();
        ack.setLabel(context.getCmuLabel());
        ack.setFileSeqNum(sotBean.getFileSeqNum());
        ack.setLduSeqNum(sotBean.getLduNum());
        context.transmit(ack);

        context.transferTo(new LinkIdleState());

        context.getInputFileBean().appendLDUBean(lduBean);

        if(eot.isFinalEOT()){
            context.triggerFileReceived();
        } else {
            startT14(sotBean);
        }

        context.cancelTask("T9");
    }

    @Override
    public void handleSYN(SYNBean syn){
        super.context.discardInputFile();
        super.context.cancelTask("T14");
        super.context.transferTo(new LinkIdleState());
    }

    /**
     * 启动NAK，ACK计时器
     * @param sot
     */
    public void startT14(final SOTBean sot){

        final IContext context = super.getContext();


        TimerTask t14 =  new TimerTask() {
            @Override
            public void run() {

                this.cancel();

                context.discardInputFile();

                // 发送SYN告知source 文件超时
                context.transmit(
                        new SYNBean(context.getCmuLabel(),
                                    sot.getFileSeqNum(),
                                    ProtocolConstants.FILE_TIME_OUT_ERR));
            }
        };

        context.schedule(t14, ProtocolConstants.T14_MAX);
        context.saveTask("T14", t14);
    }

}
