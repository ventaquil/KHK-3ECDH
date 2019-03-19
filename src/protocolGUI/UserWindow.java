package protocolGUI;

import protocolNetwork.User;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class UserWindow extends Window implements ActionListener {

    private User user;
    private TextArea textArea;
    private TextField textField;

    public UserWindow(User user, String title) {
        super(title);
        this.user = user;
    }

    @Override
    protected void initContent() {

        GridBagLayout layout = new GridBagLayout();
        this.getContentPane().setLayout(layout);

        GridBagConstraints gbc = new GridBagConstraints();

        this.textArea = new TextArea();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        this.getContentPane().add(this.textArea, gbc);

        this.textField = new TextField();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 300;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        this.getContentPane().add(this.textField, gbc);

        Button button = new Button("Send");
        gbc.ipadx = 50;
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        this.getContentPane().add(button, gbc);
        this.pack();
        button.addActionListener(this);
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
}
