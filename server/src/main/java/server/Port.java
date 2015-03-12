package server;

/**
 * Created by yar on 11.03.15.
 */
public class Port {
    private String ip;
    private String bladeId;
    private String portId;

    public Port(String port) {
        String[] data = port.split("/");
        this.ip = data[0];
        this.bladeId = data[1];
        this.portId = data[2];
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getBladeId() {
        return bladeId;
    }

    public void setBladeId(String bladeId) {
        this.bladeId = bladeId;
    }

    public String getPortId() {
        return portId;
    }

    public void setPortId(String portId) {
        this.portId = portId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Port port = (Port) o;

        if (!bladeId.equals(port.bladeId)) return false;
        if (!ip.equals(port.ip)) return false;
        if (!portId.equals(port.portId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = ip.hashCode();
        result = 31 * result + bladeId.hashCode();
        result = 31 * result + portId.hashCode();
        return result;
    }
}
