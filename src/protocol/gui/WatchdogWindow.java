package protocol.gui;

import protocol.network.Watchdog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WatchdogWindow extends Window implements ActionListener {

    private Watchdog watchdog;
    private JTextArea textArea;

    public WatchdogWindow(Watchdog watchdog, String title) {
        super(title);
        this.watchdog = watchdog;
        locationY += 450;
    }

    @Override
    protected void initContent() {

        Button button = new Button("Save Parameters");
        button.addActionListener(this);

        this.textArea = new JTextArea();
        this.textArea.setLineWrap(true);

        GroupLayout layout = new GroupLayout(this.getContentPane());
        this.getContentPane().setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(
                layout.createParallelGroup()
                .addComponent(this.textArea)
                .addComponent(button)
        );

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                .addComponent(this.textArea)
                .addComponent(button, 50, 50, 50)
        );
    }

    public void log(String message){
        super.log(this.textArea, message);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        try {
            this.watchdog.saveParameters();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
