package network;

import java.io.IOException;

public abstract class Connection extends Thread {

    protected static final String ip = "localhost";
    protected static final int port = 9999;

    protected String id;
    protected Endpoint endpoint;

    public Connection(String id){
        this.id = id;
        this.endpoint = new Endpoint(id);
    }

    @Override
    public void run(){
        try {
            setup();
            runECDH();
            while(true)
                runCommunication();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    protected abstract void setup() throws IOException;
    protected void runECDH() throws IOException {}
    protected abstract void runCommunication() throws IOException;

    public Endpoint getEndpoint() {
        return endpoint;
    }
}
