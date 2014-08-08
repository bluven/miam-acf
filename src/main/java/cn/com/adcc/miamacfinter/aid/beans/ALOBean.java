package cn.com.adcc.miamacfinter.aid.beans;

import cn.com.adcc.miamacfinter.aid.utils.Utils;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by bluven on 14-7-30.
 */
public class ALOBean implements IBean{

    public final static String TYPE = "47";

    private String label = "";

    // System Address Label, 8进制数
    private int sal = 0;

    // 协议版本号 16进制
    private int version = 0;

    /**
     * 解析原始数据，分别是data跟label，例如
     *
     * 472301,374
     *
     * @param data
     * @param label
     * @return
     */
    public static ALOBean parseRaw(String data, String label){

        ALOBean bean = new ALOBean();

        bean.label = label;

        bean.version = Integer.parseInt(data.substring(4), 16);

        String sal = data.substring(2, 4);

        sal = Utils.hex2bit(sal);

        sal = Utils.mustXchars(sal, 8);

        sal = StringUtils.reverse(sal);

        bean.sal = Utils.bit2oct(sal);

        return bean;
    }

    public String asWord(){

        String salBits = Utils.oct2bit("" + this.sal);

        salBits = StringUtils.reverse(salBits);

        String versionBits = Utils.hex2bit(this.version);

        if(versionBits.length() < 8){
            versionBits = Utils.leftPadding(versionBits, 8);
        }

        return this.label + "," + this.TYPE + Utils.bit2hex(salBits + versionBits).toUpperCase();
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getSal() {
        return sal;
    }

    public void setSal(int sal) {
        this.sal = sal;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String toString(){
        return StringUtils.join(label, ":", sal, ":", version);
    }

}
