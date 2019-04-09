package protocol.network;

import network.Client;
import protocol.Parameters;
import protocol.gui.UserWindow;
import subprocess.Sage;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class User extends Client {

    public static final int baseP2PPort = 8000;

    private UserWindow window;
    private Parameters parameters;

    private UserP2PClient p2pClient;
    private UserP2PServer p2pServer;

    private BigInteger privateComponent;
    private String publicComponent;

    private ArrayList<String> participantsPublicComponents;
    private String sessionKey;

    public User(Parameters parameters){
        super();
        this.window = new UserWindow(this, this.id);
        new Thread(this.window).start();
        try {
            this.parameters = new Parameters(parameters.getEllipticCurve(), parameters.getAsymmetricalKey().getPoint());
        } catch (IOException e) {
            System.err.println(this.id + " params failed");
        } catch (Sage.PythonException e) {
            System.err.println(this.id + " params failed");
        }
        this.participantsPublicComponents = new ArrayList<>();
    }

    @Override
    protected void setup() throws IOException {
        super.setup();
        Semaphore semaphore = new Semaphore(2);
        try {
            semaphore.acquire(2);
        } catch (InterruptedException e) {}
        this.p2pServer = new UserP2PServer(this, this.id, baseP2PPort + this.uid, this.window, semaphore);
        this.p2pServer.start();
        this.p2pClient = new UserP2PClient(this, this.id, baseP2PPort + (this.uid + 1) % 3, this.window, semaphore);
        this.p2pClient.start();
        while(semaphore.availablePermits() != 2);
    }

    @Override
    protected void runECDH() throws IOException {
        this.privateComponent = this.parameters.getAsymmetricalKey().getSecretKey();

        this.window.log("sk = " + this.privateComponent.toString());
        this.window.log("P = " + this.parameters.getAsymmetricalKey().getPoint());

        this.publicComponent = this.parameters.getAsymmetricalKey().getPublicKey();
        this.window.log("pk = sk * P = " + this.publicComponent);

        broadcastPublicComponent(this.publicComponent);
        computeSessionKey();
    }

    private String computePublicComponent(String privateComponent) throws IOException {
        log("Computing public EC point.");
        return privateComponent + " Public";
    }
    private void broadcastPublicComponent(String publicComponent) throws IOException {
        log("Distributing public EC point.");
        this.p2pClient.getEndpoint().sendData(publicComponent);
        this.p2pServer.getEndpoint().sendData(publicComponent);
        this.participantsPublicComponents.add(
                this.p2pClient.getEndpoint().receiveData());
        this.participantsPublicComponents.add(
                this.p2pServer.getEndpoint().receiveData());
    }
    private void computeSessionKey() throws IOException {
        log("Computing Session Key.");
        StringBuilder sb = new StringBuilder();
        sb.append(this.id);
        sb.append(": ");
        for(String c : this.participantsPublicComponents){
            sb.append(c);
            sb.append("; ");
        }
        this.sessionKey = sb.toString();
        this.window.log(this.sessionKey);

        //this.window.log("Session Key set.");
        log("Session Key set. K = " + this.sessionKey);
        enableCommunication();
    }

    public void enableCommunication() throws IOException {
        this.window.enableCommunication();
        this.p2pClient.enableCommunication();
        this.p2pServer.enableCommunication();
    }

    @Override
    protected void runCommunication() throws IOException {}

    private void log(String data) throws IOException {
        this.window.log(data);
        this.endpoint.sendData(this.id + ": " + data);
    }
    public void sendData(String data) throws IOException {
        String encrypted = encrypt(this.id + ": " + data);
        this.endpoint.sendData(encrypted);
        this.p2pClient.getEndpoint().sendData(encrypted);
        this.p2pServer.getEndpoint().sendData(encrypted);
    }

    private String encrypt(String s) {
        return s + "... " + this.sessionKey;
    }
}
