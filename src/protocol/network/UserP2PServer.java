package protocol.network;

import network.ServerConnection;
import protocol.gui.UserWindow;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.Semaphore;

public class UserP2PServer extends ServerConnection {

    private int port;
    private User user;
    private ServerSocket serverSocket;
    private UserWindow window;
    private Semaphore semaphore, comSem;

    public UserP2PServer(User user, String id, int port, UserWindow window, Semaphore semaphore){
        super(id, null);
        this.user = user;
        this.port = port;
        this.window = window;
        this.semaphore = semaphore;
        this.comSem = new Semaphore(1);
    }

    @Override
    protected void setup() throws IOException {
        try {
            this.comSem.acquire();
        } catch (InterruptedException e) {}
        this.serverSocket = new ServerSocket(this.port);
        this.clientSocket = this.serverSocket.accept();
        super.setup();
        this.semaphore.release();
    }

    @Override
    protected void runCommunication() throws IOException {
        try {
            this.comSem.acquire();
        } catch (InterruptedException e) {}
        this.window.log(this.endpoint.receiveData());
        this.comSem.release();
    }

    public void enableCommunication(){
        this.comSem.release();
    }
}
