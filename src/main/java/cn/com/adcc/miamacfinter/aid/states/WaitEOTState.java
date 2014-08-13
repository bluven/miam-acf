package cn.com.adcc.miamacfinter.aid.states;

import cn.com.adcc.miamacfinter.ProtocolConstants;
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

        if(lduBean == null){
            // todo: 没有LDU时处理
            return;
        }

        lduBean.appendData(data);

    }

    public void handleEOT(EOTBean eot){

        if(lduBean == null){
            // todo: 没有LDU时处理
            return;
        }

        lduBean.setEotBean(eot);

        if(lduBean.isAllDataReceived()){

            // todo: CRC validate

            if(eot.isFinalEOT()){
                context.triggerFileReceived();
            } else {
                // todo: 可能需要进一步处理
            }
        } else {

            // todo: 异常处理，需要返回NAK
        }

        super.context.transferTo(new LinkIdleState());

        SOTBean sotBean = this.lduBean.getSotBean();

        ACKBean ack = new ACKBean();
        ack.setLabel(context.getCmuLabel());
        ack.setFileSeqNum(sotBean.getFileSeqNum());
        ack.setLduSeqNum(sotBean.getLduNum());
        context.transmit(ack);

        if(eot.isFinalEOT()){
            startT14(sotBean);
        }

        super.context.cancelTask("T9");
    }

    public void startT14(final SOTBean sot){

        final IContext context = super.getContext();

        TimerTask t14 =  new TimerTask() {
            @Override
            public void run() {

                this.cancel();

                context.setInputFileBean(null);

                // 发送SYN告知source 文件超时
                context.transmit(new SYNBean(context.getCmuLabel(), sot.getFileSeqNum(), "8E"));
            }
        };

        context.schedule(t14, ProtocolConstants.T14_MAX);
        context.saveTask("T14", t14);
    }
}
