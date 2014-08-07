package cn.com.adcc.miamacfinter.aid.states;

import cn.com.adcc.miamacfinter.aid.clients.IContext;

/**
 * Created by bluven on 14-8-3.
 */
public class InitialState extends State{

    public InitialState(IContext ctx){
        this.context = ctx;
    }

    @Override
    public void onConnected() throws Exception{
        context.subscribe();
        context.transferTo(new SubscribeReqState());
    }
}
