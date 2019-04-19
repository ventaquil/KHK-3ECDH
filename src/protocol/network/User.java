package protocol.network;

import network.Client;
import protocol.Key;
import protocol.Parameters;
import protocol.gui.UserWindow;
import subprocess.Sage;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.concurrent.Semaphore;

public class User extends Client {

    public static final int baseP2PPort = 8000;
    private static final String salt = "Come on, salt is bad for your health...";
    private static final byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

    private UserWindow window;
    private Parameters parameters;

    private UserP2PClient p2pClient;
    private UserP2PServer p2pServer;

    private BigInteger privateComponent;
    private String publicComponent1;
    private String publicComponent2;

    private ArrayList<String> participantsPublicComponents;
    private String sessionKey;

    private IvParameterSpec ivspec;
    private SecretKeySpec key;

    public User(Parameters parameters){
        super();
        this.window = new UserWindow(this, this.id);
        new Thread(this.window).start();
        try {
            this.parameters = new Parameters(parameters.getEllipticCurve(), parameters.getAsymmetricalKey().getPoint1(), parameters.getAsymmetricalKey().getPoint2());
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

        String P = this.parameters.getAsymmetricalKey().getPoint1();
        String Q = this.parameters.getAsymmetricalKey().getPoint2();
        this.window.log("sk = " + this.privateComponent.toString());
        this.window.log("P = " + P.substring(P.length() - 16, P.length() - 1) + "...");
        this.window.log("Q = " + Q.substring(Q.length() - 16, Q.length() - 1) + "...");

        this.publicComponent1 = this.parameters.getAsymmetricalKey().getPublicKey1();
        this.publicComponent2 = this.parameters.getAsymmetricalKey().getPublicKey2();
        this.window.log("pk = sk * P = " + this.publicComponent1.substring(this.publicComponent1.length() - 16, this.publicComponent1.length() - 1) + "...");
        this.window.log("pk = sk * Q = " + this.publicComponent2.substring(this.publicComponent2.length() - 16, this.publicComponent2.length() - 1) + "...");

        broadcastPublicComponent(this.publicComponent1, this.publicComponent2);
        computeSessionKey();
        enableCommunication();
    }

    private void broadcastPublicComponent(String publicComponent1, String publicComponent2) throws IOException {
        log("Distributing public EC points.");
        this.p2pClient.getEndpoint().sendData(publicComponent1);
        this.p2pServer.getEndpoint().sendData(publicComponent1);
        this.p2pClient.getEndpoint().sendData(publicComponent2);
        this.p2pServer.getEndpoint().sendData(publicComponent2);
        this.participantsPublicComponents.add(
                this.p2pClient.getEndpoint().receiveData());
        this.participantsPublicComponents.add(
                this.p2pClient.getEndpoint().receiveData());
        this.participantsPublicComponents.add(
                this.p2pServer.getEndpoint().receiveData());
        this.participantsPublicComponents.add(
                this.p2pServer.getEndpoint().receiveData());
    }
    private void computeSessionKey() throws IOException {
        log("Computing Session Key.");
        try {
            this.sessionKey = Key.get(
                    this.privateComponent, // a
                    this.participantsPublicComponents.get(0), // bP
                    this.participantsPublicComponents.get(3), // cQ
                    this.parameters.getEllipticCurve().getN(),
                    this.parameters.getEllipticCurve().getK(),
                    this.parameters.getField().getP());
        } catch (Sage.PythonException e) {
            e.printStackTrace();
        }
        this.window.log(this.sessionKey);

        //this.window.log("Session Key set.");
        log("Session Key set. K = " + this.sessionKey);
        initEncryptionParams();
    }

    private void initEncryptionParams() {
        this.ivspec = new IvParameterSpec(iv);
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec(this.sessionKey.toCharArray(), salt.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            this.key = new SecretKeySpec(tmp.getEncoded(), "AES");

        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
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
        String encrypted = runAES(this.id + ": " + data, Cipher.ENCRYPT_MODE);
        this.endpoint.sendData(encrypted);
        this.p2pClient.getEndpoint().sendData(encrypted);
        this.p2pServer.getEndpoint().sendData(encrypted);
    }

    public String runAES(String data, int mode){
        try {
            Cipher aes = Cipher.getInstance("AES/CBC/PKCS5Padding");
            aes.init(mode, this.key, this.ivspec);
            String output;
            if(mode == Cipher.ENCRYPT_MODE)
                output = Base64.getEncoder().encodeToString(aes.doFinal(data.getBytes("UTF-8")));
            else
                output = new String(aes.doFinal(Base64.getDecoder().decode(data)));
            return output;

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return "Falied.";
    }
}
