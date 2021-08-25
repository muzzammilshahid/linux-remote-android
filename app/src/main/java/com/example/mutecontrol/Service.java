package com.example.mutecontrol;

import java.io.Serializable;
import java.util.Map;

public class Service implements Serializable {
    private String mHostname;
    private String mHostIP;
    private int mPort;
    private Map<String, String> mProperties;

    public Service(String hostName, String hostIP, int port, Map<String, String> properties) {
        mHostname = hostName;
        mHostIP = hostIP;
        mPort = port;
        mProperties = properties;
    }

    public String getHostName() {
        return mHostname;
    }

    public String getHostIP() {
        return mHostIP;
    }

    public int getPort() {
        return mPort;
    }

}
