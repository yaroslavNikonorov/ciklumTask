package server;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yar on 11.03.15.
 */
public class RequestHandler implements Runnable {

    private List<Session> sessionList;
    private Map<String, String> accounts;
    private Socket socket;
    final private DataInputStream inputStream;
    final private PrintWriter out;
    private String request;
    private Command command;
    private String response;

    private static Map<Port, BiDirConnection> biDirConnections;
    private static Map<Port, UniDirConnection> uniDirConnections;


    public RequestHandler(List<Session> sessionList, Map<String, String> accounts, Socket socket, Map<Port, BiDirConnection> biDirConnections, Map<Port, UniDirConnection> uniDirConnections) throws IOException {
        this.sessionList = sessionList;
        this.accounts = accounts;
        this.socket = socket;
        this.biDirConnections = biDirConnections;
        this.uniDirConnections = uniDirConnections;
        inputStream = new DataInputStream(socket.getInputStream());
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    @Override
    public void run() {
        try {
            readRequest();
            command = Utils.parseCommand(request);
            if (command != null) {
                System.out.println(command.getName() + " request");
                switch (command.getName()) {
                    case "Login":
                        loginRequest();
                        break;
                    case "Logout":
                        logOutRequest();
                        break;
                    case "SetAttributeValue":
                        if (checkLogedIn()) {
                            setAttributeValue();
                        }
                        break;
                    case "GetAttributeValue":
                        if (checkLogedIn()) {
                            getAttributeValue();
                        }
                        break;
                    case "Bidir":
                        if (checkLogedIn()) {
                            biDirCommand();
                        }
                        break;
                    case "Unidir":
                        if (checkLogedIn()) {
                            uniDirCommand();
                        }
                        break;
                    case "Discovery":
                        if (checkLogedIn()) {
                            discoveryCommand();
                        }
                        break;
                    default:
                        response = "<CommandResponse CommandName=\"" + command.getName() + "\" Success=\"0\"><ErrorCode/><Log/><ResponseInfo>Incorrect Command</ResponseInfo></CommandResponse>";
                }
            } else {
                response = "<CommandResponse CommandName=\"\" Success=\"0\"><ErrorCode/><Log/><ResponseInfo/></CommandResponse>";
            }
//            out.println(response);
            out.flush();
            socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    private void readRequest() throws IOException {
        StringBuilder sb = new StringBuilder();
        int dataLength = inputStream.readInt();
//        System.out.println(dataLength);
        int count = 0;
        int bytesRead;
        while (count < dataLength && (bytesRead = inputStream.read()) != -1) {
            sb.append((char) bytesRead);
            count++;
        }
        request = sb.toString();
//        System.out.println(request);
    }


    private void loginRequest() throws IOException, InterruptedException, TransformerException, ParserConfigurationException {
//        System.out.println(sessionList);
        if (sessionList.contains(new Session(socket.getInetAddress().getHostAddress()))) {
            Utils.createResponse(command.getName(), "1", "", "", out);
            return;
        } else {
            String username = command.getParameters().get("User");
            String password = command.getParameters().get("Password");
            String host = command.getParameters().get("Address");
            if (accounts.get(username) != null && accounts.get(username).equals(password)) {
                if (Utils.hostReachable(host)) {
                    sessionList.add(new Session(socket.getInetAddress().getHostAddress()));
                    Utils.createResponse(command.getName(), "1", "", "", out);
                    return;
                }
            }
            Utils.createResponse(command.getName(), "0", "", "", out);
        }
    }

    private void logOutRequest() throws TransformerException, ParserConfigurationException {
        Session session = new Session(socket.getInetAddress().getHostAddress());
        if (sessionList.contains(session)) {
            sessionList.remove(session);
            Utils.createResponse(command.getName(), "1", "", "", out);
        } else {
            Utils.createResponse(command.getName(), "0", "", "", out);
        }
    }


    private void setAttributeValue() throws TransformerException, ParserConfigurationException {
        int sessionIndex = sessionList.indexOf(new Session(socket.getInetAddress().getHostAddress()));
        Session session = sessionList.get(sessionIndex);
        String port = command.getParameters().get("Port");
        Map<String, String> attributes = null;
        if (session.getAttributes().containsKey(port)) {
            attributes = session.getAttributes().get(port);
        } else {
            attributes = new HashMap<>();
            session.getAttributes().put(port, attributes);
        }
        attributes.put(command.getParameters().get("Attribute"), command.getParameters().get("Value"));
        Utils.createResponse(command.getName(), "1", "", "", out);
    }

    private void getAttributeValue() throws TransformerException, ParserConfigurationException {
        int sessionIndex = sessionList.indexOf(new Session(socket.getInetAddress().getHostAddress()));
        Session session = sessionList.get(sessionIndex);
        String port = command.getParameters().get("Port");
        String key = command.getParameters().get("Attribute");
        String value = "";
        Map<String, String> attributes = null;
        if (session.getAttributes().containsKey(port)) {
            attributes = session.getAttributes().get(port);
            if (attributes != null) {
                if (attributes.containsKey(key)) {
                    Utils.createResponse(command.getName(), "1", "", attributes.get(key), out);
                    return;
                }
            }
        }
        Utils.createResponse(command.getName(), "0", "", "", out);
    }

    private void biDirCommand() throws TransformerException, ParserConfigurationException {
        Port portA = new Port(command.getParameters().get("Port_A"));
        Port portB = new Port(command.getParameters().get("Port_B"));
        if (biDirConnections.containsKey(portA)) {
            BiDirConnection biDirConnection = biDirConnections.get(portA);
            if (biDirConnection.getPortA().equals(portB) || biDirConnection.getPortB().equals(portB)) {
                Utils.createResponse(command.getName(), "1", "CONNECTION EXISTS", "", out);
            } else {
                Utils.createResponse(command.getName(), "1", "CONNECTION USED", "", out);
            }
        } else if (biDirConnections.containsKey(portB)) {
            Utils.createResponse(command.getName(), "1", "CONNECTION USED", "", out);
        } else {
            BiDirConnection biDirConnection = new BiDirConnection(portA, portB);
            biDirConnections.put(portA, biDirConnection);
            biDirConnections.put(portB, biDirConnection);
            Utils.createResponse(command.getName(), "1", "CONNECTION CREATED", "", out);
        }
    }

    private void uniDirCommand() throws TransformerException, ParserConfigurationException {
        Port srcPort = new Port(command.getParameters().get("SrcPort"));
        Port dstPort = new Port(command.getParameters().get("DstPort"));
        if (uniDirConnections.containsKey(dstPort) && uniDirConnections.get(dstPort).getDstPort().equals(dstPort)) {
            if (uniDirConnections.get(dstPort).getSrcPort().equals(srcPort)) {
                Utils.createResponse(command.getName(), "1", "CONNECTION EXISTS", "", out);
            } else {
                Utils.createResponse(command.getName(), "1", "DstPORT is USED - Not creating an additional connection", "", out);
            }
        } else if (uniDirConnections.containsKey(srcPort) && uniDirConnections.get(srcPort).getSrcPort().equals(srcPort)) {
            UniDirConnection uniDirConnection = new UniDirConnection(srcPort, dstPort);
            uniDirConnections.put(srcPort, uniDirConnection);
            uniDirConnections.put(dstPort, uniDirConnection);
            Utils.createResponse(command.getName(), "1", "SrcPORT is USED - Creating additional connection", "", out);
        } else {
            UniDirConnection uniDirConnection = new UniDirConnection(srcPort, dstPort);
            uniDirConnections.put(srcPort, uniDirConnection);
            uniDirConnections.put(dstPort, uniDirConnection);
            Utils.createResponse(command.getName(), "1", "CONNECTION CREATED", "", out);
        }
    }

    private void discoveryCommand() throws TransformerException, ParserConfigurationException {
        Port port = new Port(command.getParameters().get("Address"));
        if (uniDirConnections.containsKey(port)) {
            Utils.createResponse(command.getName(), "1", "", "", out);
        } else if (biDirConnections.containsKey(port)) {
            Utils.createResponse(command.getName(), "1", "", "", out);
        } else {
            Utils.createResponse(command.getName(), "0", "", "", out);
        }
    }

    private boolean checkLogedIn() throws IOException, TransformerException, ParserConfigurationException {
        if (sessionList.contains(new Session(socket.getInetAddress().getHostAddress()))) {
            sessionList.get(sessionList.indexOf(new Session(socket.getInetAddress().getHostAddress()))).setLastConnected(new Date());
            return true;
        }
        Utils.createResponse(command.getName(), "0", "", "", out);
        return false;
    }
}
