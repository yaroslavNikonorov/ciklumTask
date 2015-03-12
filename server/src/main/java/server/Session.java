package server;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yar on 11.03.15.
 */
public class Session {
    private String ipAddress;
    private Date lastConnected;
    private static final Long SESSION_TIMEOUT = 60000L;
    private Map<String, Map<String, String>> attributes = new HashMap<>();


    public Session(String ipAddress) {
        this.ipAddress = ipAddress;
        lastConnected = new Date();
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Date getLastConnected() {
        return lastConnected;
    }

    public void setLastConnected(Date lastConnected) {
        this.lastConnected = lastConnected;
    }

    public Map<String, Map<String, String>> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Map<String, String>> attributes) {
        this.attributes = attributes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Session session = (Session) o;

        if (!ipAddress.equals(session.ipAddress)) return false;
        if (lastConnected.getTime() - session.lastConnected.getTime() > SESSION_TIMEOUT) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = ipAddress.hashCode();
        return result;
    }
}
