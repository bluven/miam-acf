package cn.com.adcc.miamacfinter.aid.states;

/**
 * 订阅请求状态
 *
 * Created by bluven on 14-8-3.
 */
public class SubscribeReqState extends State {

    public SubscribeReqState(){
    }

    @Override
    public void handleOk(){
        context.lock();
        context.transferTo(new LockReqState());
    }
}
