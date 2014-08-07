package cn.com.adcc.miamacfinter.aid.states;

import cn.com.adcc.miamacfinter.aid.beans.ACKBean;
import cn.com.adcc.miamacfinter.aid.beans.CommandLDUBean;

/**
 * Created by bluven on 14-8-4.
 */
public class WaitACKState extends State {

    public void handleACK(ACKBean bean){

        CommandLDUBean lduBean = context.getOutLduBean();

        if(lduBean.matchACK(bean)){

            CommandLDUBean nextLDU = context.getOutFileBean().nextLDU();

            if(nextLDU == null){
                context.transferTo(new LinkIdleState());
                return;
            }

            context.transmit(nextLDU.getRtsBean());
            context.setOutLduBean(nextLDU);
            context.transferTo(new WaitCTSState());

        } else {
            // todo: 异常处理
        }
    }
}
