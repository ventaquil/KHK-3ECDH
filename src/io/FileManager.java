package io;

import protocol.Parameters;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.math.BigInteger;

public class FileManager implements InputManager {

    private static final String DEFAULT_PATH = "resrc/params.txt";
    private String path;

    public FileManager(){
        this.path = DEFAULT_PATH;
    }
    public FileManager(String path){
        this.path = path;
    }

    @Override
    public Parameters read() throws Exception {
        Parameters.Field field = null;
        String[] ellipticCurveCoefficients = new String[4];
        ellipticCurveCoefficients[3] = null;
        String point1 = null;
        String point2 = null;

        File file = new File(this.path);
        BufferedReader reader = new BufferedReader((new FileReader(file)));
        String line = reader.readLine();

        while(line != null){
            String[] split = line.split(" ");
            switch(split[0].toLowerCase()){
                case "field:":
                    field = new Parameters.Field(new BigInteger(split[1]));
                    break;
                case "curve:":
                    ellipticCurveCoefficients[0] = split[1];
                    ellipticCurveCoefficients[1] = split[2];
                    ellipticCurveCoefficients[2] = split[3];
                    ellipticCurveCoefficients[3] = split[4];
                    break;
                case "points:":
                    point1 = split[1];
                    point2 = split[2];
                    break;
            }
            line = reader.readLine();
        }

        if(field == null)
            return new Parameters(8);

        if(ellipticCurveCoefficients[3] == null)
            return new Parameters(field);

        if((point1 == null) || (point2 == null))
            return new Parameters(new Parameters.EllipticCurve(field,
                    new BigInteger(ellipticCurveCoefficients[0]),
                    new BigInteger(ellipticCurveCoefficients[1]),
                    new BigInteger(ellipticCurveCoefficients[2]),
                    new BigInteger(ellipticCurveCoefficients[3])));

        return new Parameters(new Parameters.EllipticCurve(field,
                new BigInteger(ellipticCurveCoefficients[0]),
                new BigInteger(ellipticCurveCoefficients[1]),
                new BigInteger(ellipticCurveCoefficients[2]),
                new BigInteger(ellipticCurveCoefficients[3])), point1, point2);
    }

    @Override
    public void save(String path, Parameters parameters) throws Exception {
        File file = new File(path);
        PrintWriter writer = new PrintWriter(file);
        writer.println("Field: " + parameters.getField().getP().toString());
        writer.println("Curve: " + parameters.getEllipticCurve().getA() + " "
                + parameters.getEllipticCurve().getB() + " "
                + parameters.getEllipticCurve().getN() + " "
                + parameters.getEllipticCurve().getK());
        writer.println("Point: " + parameters.getAsymmetricalKey().getPoint1() + ", " + parameters.getAsymmetricalKey().getPoint2());
    }
}
