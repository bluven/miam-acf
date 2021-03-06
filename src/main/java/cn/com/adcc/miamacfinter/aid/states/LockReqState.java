package cn.com.adcc.miamacfinter.aid.states;

import java.util.TimerTask;

/**
 * 加锁请求
 *
 * Created by bluven on 14-8-3.
 */
public class LockReqState extends State {

    public void handleOk(){

        //super.context.sendCommand("add,0,270");
        super.context.sendALO();
        super.context.transferTo(new ALOIdleState(super.context));

        this.startPingPong();
    }

    private void startPingPong(){

        super.context.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                LockReqState.this.getContext().sendCommand("transmit,0,172,00009F");
            }
        }, 1000, 1000);
    }


}
