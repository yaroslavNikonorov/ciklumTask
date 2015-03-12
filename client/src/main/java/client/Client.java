package client;

import java.io.*;
import java.net.Socket;

/**
 * Created by yar on 11.03.15.
 */
public class Client {
    public static void main(String[] args) throws IOException {

        String filePath = null;
        String host = "localhost";
        int port = 2323;
        if (args.length == 1) {
            filePath = args[0];
        } else if (args.length == 3) {
            host = args[0];
            port = Integer.parseInt(args[1]);
            filePath = args[2];
        }

        Socket csocket = new Socket(host, port);
        if (csocket.isConnected()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(csocket.getInputStream()));
            DataOutputStream out = new DataOutputStream(csocket.getOutputStream());
            if (filePath == null) return;
            File myFile = new File(filePath);
//            System.out.println(myFile.length());
            DataInputStream fileInput = new DataInputStream(new FileInputStream(myFile));
            out.writeInt((int) myFile.length());
            out.flush();
            int data;
            while ((data = fileInput.read()) != -1) {
                out.write(data);
                out.flush();
            }
            String str;
            System.out.println("Sending data");
            while (!Thread.interrupted() && (str = br.readLine()) != null) {
                System.out.println(str);
            }
        }
    }
}
