package icp.net.server;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author Ales
 */
public class ServerReadingRunnable implements Runnable{
    
    private Socket client;
    private DataInputStream inputStream;

    public ServerReadingRunnable(Socket client) throws IOException {
        this.client = client;
        inputStream = new DataInputStream(client.getInputStream());
    }
    
    @Override
    public void run() {
        
    }
    
}
