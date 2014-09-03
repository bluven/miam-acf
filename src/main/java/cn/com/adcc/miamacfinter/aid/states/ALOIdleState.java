package cn.com.adcc.miamacfinter.aid.states;

import cn.com.adcc.miamacfinter.aid.beans.ALRBean;
import cn.com.adcc.miamacfinter.aid.clients.IContext;

import java.util.TimerTask;

/**
 * Created by bluven on 14-8-3.
 */
public class ALOIdleState extends State {

    // T12计时器间隔, 200ms
    public final static int T12_DELAY = 200;

    // 尝试计时器
    private TimerTask t12;

    public ALOIdleState(final IContext context){

        this.t12 = new TimerTask() {

            @Override
            public void run() {

                if(context.getState() instanceof ALOIdleState){

                    context.sendALO();

                    return;
                }

                if(t12 != null){
                    t12.cancel();
                }
            }
        };

        context.scheduleAtFixedRate(t12, T12_DELAY , T12_DELAY);
    }

    public void handleALR(ALRBean alr){

        if(this.t12 != null){
            this.t12.cancel();
        }

        context.transferTo(new LinkIdleState());
    }

}
