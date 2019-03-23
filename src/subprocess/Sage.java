package subprocess;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class Sage {
    public String execute(String command) throws IOException {
        Process process = Runtime.getRuntime().exec(new String[]{"sage", "-python", "-c", command}, null, new File("resrc/python"));

        InputStream errorStream = process.getErrorStream();
        byte[] error = errorStream.readAllBytes();

        if (error.length > 0) {
            throw new RuntimeException(new String(error, "UTF-8"));
        }

        byte[] output = process.getInputStream().readAllBytes();

        return new String(output, "UTF-8");
    }
}
