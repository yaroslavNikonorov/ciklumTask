package server;

/**
 * Created by yar on 11.03.15.
 */
public class BiDirConnection {
    private Port portA;
    private Port portB;

    public BiDirConnection(Port portA, Port portB) {
        this.portA = portA;
        this.portB = portB;
    }

    public Port getPortA() {
        return portA;
    }

    public void setPortA(Port portA) {
        this.portA = portA;
    }

    public Port getPortB() {
        return portB;
    }

    public void setPortB(Port portB) {
        this.portB = portB;
    }
}
