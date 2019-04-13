package protocol.network;

import network.Server;
import network.ServerConnection;
import protocol.Parameters;
import protocol.gui.WatchdogWindow;
import subprocess.Sage;

import java.io.IOException;
import java.net.Socket;

public class Watchdog extends Server {

    private Parameters parameters;
    private WatchdogWindow window;

    public Watchdog(Parameters parameters){
        super("Watchdog");
        try {
            this.parameters = new Parameters(parameters.getEllipticCurve(), parameters.getAsymmetricalKey().getPoint1(), parameters.getAsymmetricalKey().getPoint2());
        } catch (IOException e) {
            System.err.println("Watchdog params failed");
        } catch (Sage.PythonException e) {
            System.err.println("Watchdog params failed");
        }
        this.window = new WatchdogWindow(this, this.id);
        new Thread(this.window).start();
    }

    @Override
    protected void setup() throws IOException {
        super.setup();
        this.window.log("Prime p: " + parameters.getField().getP());
    }

    @Override
    protected void runCommunication() throws IOException {
        super.runCommunication();
        this.window.log("Client connected.");
    }

    @Override
    protected ServerConnection getConnection(Socket clientSocket) {
        return new WatchdogConnection("Watchdog", clientSocket, this.window);
    }
}
