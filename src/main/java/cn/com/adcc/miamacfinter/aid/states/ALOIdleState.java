package cn.com.adcc.miamacfinter.aid.states;

import cn.com.adcc.miamacfinter.aid.beans.ALOBean;
import cn.com.adcc.miamacfinter.aid.beans.ALRBean;

import java.io.IOException;

/**
 * Created by bluven on 14-8-3.
 */
public class ALOIdleState extends State {

    public void handleALR(ALRBean alr){
        context.transferTo(new LinkIdleState());
    }
}
