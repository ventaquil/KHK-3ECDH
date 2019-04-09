package protocol.gui;

import protocol.network.Watchdog;

import javax.swing.*;

public class WatchdogWindow extends Window {

    private Watchdog watchdog;
    private JTextArea textArea;

    public WatchdogWindow(Watchdog watchdog, String title) {
        super(title);
        this.watchdog = watchdog;
        locationY += 450;
    }

    @Override
    protected void initContent() {
        this.textArea = new JTextArea();
        this.textArea.setLineWrap(true);
        this.getContentPane().add(new JScrollPane(this.textArea));
    }

    public void log(String message){
        super.log(this.textArea, message);
    }
}
