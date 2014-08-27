package cn.com.adcc.miamacfinter.aid.clients;

import cn.com.adcc.miamacfinter.aid.handlers.IFileHandler;

/**
 * Created by bluven on 14-8-9.
 */
public interface IClient {

    void connect(String aidLabel);

    void connect(String host, int port, String aidLabel, String cmuLabel);

    void registerFileHandler(IFileHandler handler);

    void sendFile(int fileId, String fileContent);

    boolean isReceivingFile();

    String getAidLabel();

    void setAidLabel(String label);

    String getCmuLabel();

    void setCmuLabel(String label);

    String getHost();

    void setHost(String host);

    Integer getPort();

    void setPort(Integer port);

}
