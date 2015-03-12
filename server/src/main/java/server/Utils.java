package server;

import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.net.InetAddress;

/**
 * Created by yar on 12.03.15.
 */
public class Utils {
    public static Command parseCommand(String request) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(request));
        Document doc = db.parse(is);

        Command command = new Command();

        Element root = doc.getDocumentElement();
        command.setName(root.getAttribute("CommandName"));
        NodeList parameters = root.getFirstChild().getChildNodes();
        for (int i = 0; i < parameters.getLength(); i++) {
            NamedNodeMap nodeMap = parameters.item(i).getAttributes();
            String name = null;
            String value = null;
            for (int j = 0; j < nodeMap.getLength(); j++) {
                Node node = nodeMap.item(j);
                if (node.getNodeName().equals("Name")) name = node.getNodeValue();
                if (node.getNodeName().equals("Value")) value = node.getNodeValue();
            }
            if (name != null && value != null) {
                command.getParameters().put(name, value);
            }
        }
        return command;
    }

    public static boolean hostReachable(String host) throws IOException, InterruptedException {
        InetAddress inetAddress = InetAddress.getByName(host);
        if (inetAddress.isReachable(1000)) {
            return true;
        } else {
            Thread.sleep(5000);
            return inetAddress.isReachable(1000);
        }
    }

}
