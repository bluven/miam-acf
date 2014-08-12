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

    // 文件ID, 只有发送的文件才有
    private Integer fileId;

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

	public String getFileContent() {
        //定义临时变量
        StringBuilder fileContent=new StringBuilder();

        //循环ldu数据,进行拼接
        for(CommandLDUBean lduBean : this.LDUBeans) {
            fileContent.insert(0,lduBean.getLduContent());
        }
        return fileContent.reverse().toString();
    }

    public Integer getFileId() {
        return fileId;
    }

    public void setFileId(Integer fileId) {
        this.fileId = fileId;
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
