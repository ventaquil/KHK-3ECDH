package network;

import java.io.*;

public class Endpoint {

    private String id;
    private PrintWriter dataOutput;
    private BufferedReader dataInput;

    public Endpoint(String id){
        this.id = id;
    }

    public void sendData(String data) throws IOException {
        this.dataOutput.println(data);
        this.dataOutput.flush();
    }
    public String receiveData() throws IOException {
        String message = this.dataInput.readLine();
        return message;
    }

    public void setDataInput(InputStream dataInput) {
        this.dataInput = new BufferedReader(new InputStreamReader(dataInput));
    }
    public void setDataOutput(OutputStream dataOutput) {
        this.dataOutput = new PrintWriter(new DataOutputStream(dataOutput));
    }
}
