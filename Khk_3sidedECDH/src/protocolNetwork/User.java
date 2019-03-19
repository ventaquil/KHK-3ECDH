package protocolNetwork;

import network.Client;
import protocolGUI.UserWindow;
import java.io.IOException;

public class User extends Client {

    public static final int baseP2PPort = 8000;

    private UserWindow window;

    private UserP2PClient p2pClient;
    private UserP2PServer p2pServer;

    public User(){
        super();
        this.window = new UserWindow(this, this.id);
        new Thread(this.window).start();
    }

    @Override
    protected void setup() throws IOException {
        super.setup();
        this.p2pServer = new UserP2PServer(this.id, baseP2PPort + this.uid, this.window);
        this.p2pServer.start();
        this.p2pClient = new UserP2PClient(this.id, baseP2PPort + (this.uid + 1) % 3, this.window);
        this.p2pClient.start();
    }

    @Override
    protected void runCommunication() throws IOException {}

    public void sendData(String data) throws IOException {
        this.endpoint.sendData(data);
        this.p2pClient.getEndpoint().sendData(data);
        this.p2pServer.getEndpoint().sendData(data);
    }
}
