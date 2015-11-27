package icp.net.client;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author Ales
 */
class ClientWritingRunnable implements Runnable {

    private Socket socket;
    private DataOutputStream outputStream;
    
    public ClientWritingRunnable(Socket socket) throws IOException {
        this.socket = socket;
        outputStream = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {

    }    
}
