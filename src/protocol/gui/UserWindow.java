package protocol.gui;

import protocol.network.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class UserWindow extends Window implements ActionListener {

    private User user;
    private JTextArea textArea;
    private JTextField textField;

    public UserWindow(User user, String title) {
        super(title);
        this.user = user;
    }

    @Override
    protected void initContent() {

        Button button = new Button("Send");
        button.addActionListener(this);

        this.textArea = new JTextArea();
        JScrollPane scroll = new JScrollPane(this.textArea);

        this.textField = new JTextField();
        this.textField.setEnabled(false);

        GroupLayout layout = new GroupLayout(this.getContentPane());
        this.getContentPane().setLayout(layout);

        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(
            layout.createParallelGroup()
                .addComponent(scroll)
                .addGroup(
                    layout.createSequentialGroup()
                    .addComponent(this.textField)
                    .addComponent(button, 50, 50, 50)
                )
        );

        layout.setVerticalGroup(
            layout.createSequentialGroup()
                .addComponent(scroll)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                    .addComponent(this.textField, 25, 25, 25)
                    .addComponent(button, 25, 25, 25)
                )
        );
    }

    public void log(String message){
        super.log(this.textArea, message);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        try {
            if(!this.textField.getText().equals("")) {
                this.log("Sending: " + this.textField.getText());
                this.user.sendData(this.textField.getText());
                this.textField.setText("");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void enableCommunication(){
        this.textField.setEnabled(true);
    }
}
