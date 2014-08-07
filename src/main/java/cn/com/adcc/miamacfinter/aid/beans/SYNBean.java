package cn.com.adcc.miamacfinter.aid.beans;

public class SYNBean implements IBean {

    public final static String TYPE = "49";

    private String label;

    private String fsn;

    private String statusCode;

    public static SYNBean parseRaw(String data, String label){

    	SYNBean bean = new SYNBean();

        bean.label = label;

        bean.fsn = data.substring(2, 4);

        bean.statusCode = data.substring(4);

        return bean;
    };

    public String asWord() {

        StringBuilder word = new StringBuilder(label).append(",").append(this.TYPE).append(fsn).append(statusCode);

        return word.toString();
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getFsn() {
        return fsn;
    }

    public void setFsn(String fsn) {
        this.fsn = fsn;
    }
    public int getFsnAsInt(){
        return Integer.parseInt(fsn, 16);
    }

    public void setFsn(int fsn){
        this.fsn = Integer.toHexString(fsn);
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCodeAsInt(){
        return Integer.parseInt(statusCode, 16);
    }

    public void setStatusCode(int statusCode){
        this.statusCode = Integer.toHexString(statusCode);
    }
}