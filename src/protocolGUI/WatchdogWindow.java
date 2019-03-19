package protocolGUI;

import protocolNetwork.Watchdog;
import java.awt.*;

public class WatchdogWindow extends Window {

    private Watchdog watchdog;
    private TextArea textArea;

    public WatchdogWindow(Watchdog watchdog, String title) {
        super(title);
        this.watchdog = watchdog;
        locationY += 450;
    }

    @Override
    protected void initContent() {
        this.textArea = new TextArea();
        this.getContentPane().add(this.textArea);
    }

    public void log(String message){
        super.log(this.textArea, message);
    }
}
