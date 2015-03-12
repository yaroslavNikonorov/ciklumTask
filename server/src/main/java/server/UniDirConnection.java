package server;

/**
 * Created by yar on 11.03.15.
 */
public class UniDirConnection {
    private Port srcPort;
    private Port dstPort;

    public UniDirConnection(Port srcPort, Port dstPort) {
        this.srcPort = srcPort;
        this.dstPort = dstPort;
    }

    public Port getSrcPort() {
        return srcPort;
    }

    public void setSrcPort(Port srcPort) {
        this.srcPort = srcPort;
    }

    public Port getDstPort() {
        return dstPort;
    }

    public void setDstPort(Port dstPort) {
        this.dstPort = dstPort;
    }
}
