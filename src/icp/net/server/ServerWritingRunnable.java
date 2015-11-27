package icp.net.server;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ales
 */
public class ServerWritingRunnable implements Runnable {

    private int val = 0;
    private Socket client;
    private DataOutputStream outputStream;

    public ServerWritingRunnable(Socket client) throws IOException {
        this.client = client;
        outputStream = new DataOutputStream(client.getOutputStream());
    }

    @Override
    public void run() {
        Random r = new Random();
        while (true) {
            try {
                System.out.println("val = " + val);
                outputStream.writeFloat((float)val);
               
                val++;
                //Thread.sleep(100);
            } catch (IOException ex) {
                Logger.getLogger(ServerWritingRunnable.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
