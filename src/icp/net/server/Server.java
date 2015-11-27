package icp.net.server;

import icp.ICP;
import icp.graphics.A_DrawableGrob;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ales
 */
public class Server implements Runnable {

    private static final int PORT = 59876;

    private boolean serverRunning = false;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private Thread serverStartingThread;
    private Thread serverReaderThread;
    private Thread serverWriterThread;
    private ArrayList<A_DrawableGrob> sceneObjectsBuffer;

    private ICP parent;

    public Server(ICP parent) throws IOException {
        this.parent = parent;
        serverSocket = new ServerSocket(PORT);
    }

    public void start() {
        serverStartingThread = new Thread(this, "serverStartingThread");
        serverStartingThread.start();
    }

    public boolean isShooterConnected() {
        return false;
    }

    @Override
    public void run() {
        serverRunning = true;
        try {
            System.out.println("waiting for client");
            clientSocket = serverSocket.accept();
            serverReaderThread = new Thread(new ServerReadingRunnable(clientSocket), "serverReaderThread");
            serverWriterThread = new Thread(new ServerWritingRunnable(clientSocket), "serverWriterThread");
            
            System.out.println("client accepted, starting threads");
            serverReaderThread.start();
            serverWriterThread.start();
            parent.setMessageScreen(null);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
