package protocol.network;

import network.Connection;
import protocol.gui.UserWindow;

import javax.crypto.Cipher;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.concurrent.Semaphore;

public class UserP2PClient extends Connection {

    private int port;
    private User user;
    private Socket clientSocket;
    private UserWindow window;
    private Semaphore semaphore, comSem;

    public UserP2PClient(User user, String id, int port, UserWindow window, Semaphore semaphore){
        super(id);
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
        connect();

        this.endpoint.setDataInput(this.clientSocket.getInputStream());
        this.endpoint.setDataOutput(this.clientSocket.getOutputStream());
        this.semaphore.release();
    }

    private void connect() throws IOException {
        try {
            this.clientSocket = new Socket(ip, this.port);
        } catch(ConnectException e){
            connect();
        }
    }

    @Override
    protected void runCommunication() throws IOException {
        try {
            this.comSem.acquire();
        } catch (InterruptedException e) {}
        String data = this.endpoint.receiveData();
        this.window.log("Encrypted = " + data);
        String decrypted = this.user.runAES(data, Cipher.DECRYPT_MODE);
        this.window.log("Decrypted = " + decrypted);
        this.comSem.release();
    }

    public void enableCommunication(){
        this.comSem.release();
    }
}
