package icp.net.client;

import icp.ICP;
import icp.graphics.screens.MessageScreen;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ales
 */
class ClientReadingRunnable implements Runnable {

    private Socket socket;
    private DataInputStream inputStream;
    private ICP parent;

    public ClientReadingRunnable(ICP parent, Socket socket) throws IOException {
        this.parent = parent;
        this.socket = socket;
        this.inputStream = new DataInputStream(socket.getInputStream());
    }

    @Override
    public void run() {
        while (true) {
            try {
                int i = inputStream.readInt();
                System.out.println(i);
                parent.setMessageScreen(new MessageScreen(parent.getGLUT(), parent, i + ""));

            } catch (IOException ex) {
                Logger.getLogger(ClientReadingRunnable.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
