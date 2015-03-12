package server;

import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
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

    public static void createResponse(String commandName, String success, String response, String value, PrintWriter out) throws ParserConfigurationException, TransformerException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();

        Element rootElement = document.createElement("CommandResponse");
        rootElement.setAttribute("CommandName", commandName);
        rootElement.setAttribute("Success", success);
        document.appendChild(rootElement);

        Element errorCode = document.createElement("ErrorCode");
        rootElement.appendChild(errorCode);

        Element log = document.createElement("Log");
        rootElement.appendChild(log);

        Element responseInfo = document.createElement("ResponseInfo");
        rootElement.appendChild(responseInfo);
        responseInfo.setTextContent(response);

        if(!value.isEmpty()){
            Element valueElement = document.createElement("Value");
            rootElement.appendChild(valueElement);
            valueElement.setTextContent(value);
        }

        TransformerFactory tFactory =
                TransformerFactory.newInstance();
        Transformer transformer = tFactory.newTransformer();

        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.transform(new DOMSource(document), new StreamResult(out));
    }
}
