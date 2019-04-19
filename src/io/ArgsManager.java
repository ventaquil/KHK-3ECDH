package io;

import protocol.Parameters;

import java.math.BigInteger;

public class ArgsManager implements InputManager {

    private String[] args;
    private InputManager inputManagerUsed;

    public ArgsManager(String[] args){
        this.args = args;
    }

    @Override
    public Parameters read() throws Exception {
        if(args.length == 0){
            try {
                this.inputManagerUsed = new FileManager();
                return this.inputManagerUsed.read();
            } catch (Exception e){
                this.inputManagerUsed = new EnvironmentVariablesManager();
                return this.inputManagerUsed.read();
            }
        } else {
            switch (this.args[0]){
                case "-a":
                    this.inputManagerUsed = this;
                    return readArgs();
                case "-e":
                    this.inputManagerUsed = new EnvironmentVariablesManager();
                    return this.inputManagerUsed.read();
                case "-f":
                    try {
                        this.inputManagerUsed = new FileManager(this.args[1]);
                        return this.inputManagerUsed.read();
                    } catch (Exception e){
                        this.inputManagerUsed = new FileManager();
                        return this.inputManagerUsed.read();
                    }
            }
        }
        return new Parameters(8);
    }

    private Parameters readArgs() throws Exception {
        Parameters.Field field = null;
        String[] ellipticCurveCoefficients = new String[4];
        ellipticCurveCoefficients[3] = null;
        String point1 = null;
        String point2 = null;

        for(int i = 1; i < this.args.length; i++){
            switch (this.args[i]){
                case "-field":
                    field = new Parameters.Field(new BigInteger(this.args[++i]));
                    break;
                case "-curve":
                    ellipticCurveCoefficients[0] = this.args[i + 1];
                    ellipticCurveCoefficients[1] = this.args[i + 2];
                    ellipticCurveCoefficients[2] = this.args[i + 3];
                    ellipticCurveCoefficients[3] = this.args[i + 4];
                    i += 4;
                    break;
                case "-points":
                    point1 = this.args[++i];
                    point2 = this.args[++i];
                    break;
            }
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
        new FileManager().save(path, parameters);
    }
}
