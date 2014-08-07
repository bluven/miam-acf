package cn.com.adcc.miamacfinter.aid.handlers;

import cn.com.adcc.miamacfinter.aid.beans.CommandFileBean;

/**
 * Created by bluven on 14-8-4.
 */
public interface IFileReceivedHandler {

    void handle(CommandFileBean fileBean);
}
