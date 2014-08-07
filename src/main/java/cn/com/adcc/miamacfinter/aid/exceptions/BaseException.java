package cn.com.adcc.miamacfinter.aid.exceptions;

/**
 * Created by bluven on 14-8-4.
 */
public class BaseException extends RuntimeException {

    public BaseException(){
        super();
    }

    public BaseException(String msg) {
        super(msg);
    }

    public BaseException(Throwable e){
        super(e);
    }
}
