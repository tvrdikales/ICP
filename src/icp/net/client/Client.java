package icp.net.client;

import icp.ICP;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ales
 */
public class Client implements Runnable {

    private static final int PORT = 59875;

    private ICP parent;
    private Socket clientSocket;
    private Thread clientStartingThread;
    private Thread clientReaderThread;
    private Thread clientWriterThread;

    public Client(ICP parent) {
        this.parent = parent;
        clientSocket = new Socket();
    }

    public void start() {
        clientStartingThread = new Thread(this, "serverStartingThread");
        clientStartingThread.start();
    }

    @Override
    public void run() {
        try {
            System.out.println("connecting");
            clientSocket.connect(new InetSocketAddress(InetAddress.getByAddress(new byte[]{(byte) 127, (byte) 0, (byte) 0, (byte) 1}), 59876));
            System.out.println("connected, starting threads");
            clientReaderThread = new Thread(new ClientReadingRunnable(parent, clientSocket));
            clientWriterThread = new Thread(new ClientWritingRunnable(clientSocket));
            clientReaderThread.start();
            clientWriterThread.start();
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
