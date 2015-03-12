package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by yar on 11.03.15.
 */
public class Server {
    private final static ExecutorService executorService = Executors.newFixedThreadPool(5);
    private static List<Session> sessionList = Collections.synchronizedList(new ArrayList<Session>());
    //    private static List<String> sessionList = Collections.synchronizedList(new ArrayList<String>());
    private static Map<String, String> accounts = new HashMap<String, String>();

    static {
        accounts.put("admin", "password");
    }

//    private static List<BiDirConnection> biDirConnections = Collections.synchronizedList(new ArrayList<BiDirConnection>());
//    private static List<UniDirConnection> uniDirConnections = Collections.synchronizedList(new ArrayList<UniDirConnection>());

    private static Map<Port, BiDirConnection> biDirConnections = Collections.synchronizedMap(new HashMap<Port, BiDirConnection>());
    private static Map<Port, UniDirConnection> uniDirConnections = Collections.synchronizedMap(new HashMap<Port, UniDirConnection>());

    public static void main(String[] args) throws IOException {
        InetAddress inetAddress = InetAddress.getByName("localhost");
        int port = 2323;
        if (args.length == 2) {
            inetAddress = InetAddress.getByName(args[0]);
            port = Integer.parseInt(args[1]);
        }
        int backLog = 10;
        ServerSocket serverSocket = new ServerSocket(port, backLog, inetAddress);
        Socket socket;
        System.out.println("Server Started");
        while (!Thread.interrupted() && (socket = serverSocket.accept()) != null) {
            System.out.println("New client connected: " + socket.getInetAddress().getHostAddress());
            executorService.execute(new RequestHandler(sessionList, accounts, socket, biDirConnections, uniDirConnections));
        }
    }
}
