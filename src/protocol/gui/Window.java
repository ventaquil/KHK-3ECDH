package protocol.gui;

import javax.swing.*;
import java.awt.*;

public abstract class Window extends JFrame implements Runnable {

    private static final int
            width = 400,
            height = 400,
            maxWindowsHorizontally = 3;
    protected static int
            id = 0,
            locationX,
            locationY = 100;
    private String title;

    public Window(String title){
        this.title = title;
        initWindow();
        initContent();
    }

    @Override
    public void run(){
        this.setVisible(true);
    }

    private void initWindow() {
        this.setTitle(this.title);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize(width, height);
        locationX = (screenSize.width - width) / 2;
        if(id != 0)
            locationX += (id - (maxWindowsHorizontally + maxWindowsHorizontally % 2) / 2) * (width + 50);
        this.setLocation(locationX, locationY);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        id++;
    }
    protected abstract void initContent();

    protected void log(TextComponent textArea, String message){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(textArea.getText());
        stringBuilder.append('\n');
        stringBuilder.append(" > ");
        stringBuilder.append(message);
        textArea.setText(stringBuilder.toString());
    }
}
