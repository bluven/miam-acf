package cn.com.adcc.miamacfinter.aid.beans;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 构建传输文件的Command Bean
 *
 * Created by Daniel on 14-8-2.
 */
public class CommandFileBean {

    //定义文件编号
    private int fileNum = -1;

    private List<CommandLDUBean> LDUBeans = new ArrayList<CommandLDUBean>();

    private Iterator<CommandLDUBean> lduIter;

    public void appendLDUBean(CommandLDUBean lduBean){
        if(LDUBeans == null){
            this.LDUBeans = new ArrayList<CommandLDUBean>();
        }

        this.LDUBeans.add(lduBean);
    }

    public CommandLDUBean nextLDU(){

        if(lduIter == null){
            lduIter = LDUBeans.iterator();
        }

        if(lduIter.hasNext()){
            return lduIter.next();
        } else {
            return null;
        }
    }

    public void inspect(){
        // debug用
        for(CommandLDUBean lduBean: LDUBeans){
           lduBean.inspect();
        }
    }

    public boolean isFileSeqNumMatch(int fileNum_){
        return this.fileNum == fileNum_;
    }

    public List<CommandLDUBean> getLDUBeans() {
        return LDUBeans;
    }

    public void setLDUBeans(List<CommandLDUBean> LDUBeans) {
        this.LDUBeans = LDUBeans;
    }

    public int getFileNum() {
        return fileNum;
    }

    public void setFileNum(int fileNum) {
        this.fileNum = fileNum;
    }

}
