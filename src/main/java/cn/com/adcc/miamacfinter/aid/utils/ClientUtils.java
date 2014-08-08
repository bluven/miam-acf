package cn.com.adcc.miamacfinter.aid.utils;

import cn.com.adcc.miamacfinter.aid.beans.*;
import cn.com.adcc.miamacfinter.aid.states.IState;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by bluven on 14-8-6.
 */
public class ClientUtils {

    public static void handleInputData(IState state, String msg){

        if(StringUtils.isBlank(msg)){
            return;
        }

        if(msg.startsWith("ok")){

            state.handleOk();

        } else if(msg.startsWith("err")){

            state.handleError();

        } else if(msg.startsWith("data")){

            String[] fields = msg.split(",");
            String label = fields[3];
            String data = fields[4];

            data = Utils.must6chars(data);

            if(data.startsWith(ALRBean.TYPE)){

                state.handleALR(ALRBean.parseRaw(data, label));

            } else if(data.startsWith(ALOBean.TYPE)){

                state.handleALO(ALOBean.parseRaw(data, label));

            } else if(data.startsWith(RTSBean.TYPE)){

                state.handleRTS(RTSBean.parseRaw(data, label));

            } else if(data.startsWith(CTSBean.TYPE)){

                state.handleCTS(CTSBean.parseRaw(data, label));

            } else if(data.startsWith(NCTSBean.TYPE)){

                state.handleNCTS(NCTSBean.parseRaw(data, label));

            } else if(data.startsWith(SOTBean.TYPE)){

                state.handleSOT(SOTBean.parseRaw(data, label));

            } else if(data.startsWith(EOTBean.TYPE_NOT_FINAL)){

                state.handleEOT(EOTBean.newEOT(data.substring(2), label));

            } else if(data.startsWith(EOTBean.TYPE_FINAL)){

                state.handleEOT(EOTBean.newFinalEOT(data.substring(2), label));

            } else if(data.startsWith(ACKBean.TYPE)){

                state.handleACK(ACKBean.parseRaw(data, label));

            } else if(false && data.startsWith(DataBean.TYPE)) {

                state.handleDataBean(new DataBean(data.substring(1), label));

            } else if(false && data.startsWith(PartialDataBean.TYPE)) {

                state.handleDataBean(new PartialDataBean(data.substring(1), label));
            }
        }
    }
}
