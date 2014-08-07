package cn.com.adcc.miamacfinter.aid.states;

import cn.com.adcc.miamacfinter.aid.beans.CTSBean;
import cn.com.adcc.miamacfinter.aid.beans.CommandLDUBean;
import cn.com.adcc.miamacfinter.aid.beans.DataBean;
import cn.com.adcc.miamacfinter.aid.beans.NCTSBean;

/**
 * Created by bluven on 14-8-4.
 */
public class WaitCTSState extends State {

    public void handleCTS(CTSBean bean){

        CommandLDUBean lduBean = context.getLduBean();

        if(!bean.matchRTS(lduBean.getRtsBean())){
            // todo: 要有状态切换, 还要有RTS重发处理
            return ;
        }

        for(DataBean databean: lduBean.getDataBeans()){
            context.transmit(databean);
        }

        context.transmit(lduBean.getEotBean());
        context.transferTo(new WaitACKState());
    }

    public void handleNCTS(NCTSBean bean){

    }
}
