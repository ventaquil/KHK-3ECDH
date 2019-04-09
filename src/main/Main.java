package main;

import protocol.Parameters;
import protocol.network.User;
import protocol.network.Watchdog;

import java.math.BigInteger;

public class Main {
    public static void main(String[] args) {
        try {
            String p = System.getenv("ECDH_FIELD");
            String ellipticCurveParameters = System.getenv("ECDH_ELLIPTIC_CURVE");
            String protocolPoint = System.getenv("ECDH_POINT");

            Parameters parameters;
            if (p == null) {
                parameters = new Parameters(8);
            } else if (ellipticCurveParameters == null) {
                parameters = new Parameters(new Parameters.Field(new BigInteger(p)));
            } else if (protocolPoint == null) {
                Parameters.Field field = new Parameters.Field(new BigInteger(p));

                String[] ecParameters = ellipticCurveParameters.split(" ");
                parameters = new Parameters(new Parameters.EllipticCurve(field,
                        new BigInteger(ecParameters[0]),  new BigInteger(ecParameters[1]),
                        new BigInteger(ecParameters[2]),  new BigInteger(ecParameters[3])));
            } else {
                Parameters.Field field = new Parameters.Field(new BigInteger(p));

                String[] ecParameters = ellipticCurveParameters.split(" ");
                parameters = new Parameters(new Parameters.EllipticCurve(field,
                        new BigInteger(ecParameters[0]),  new BigInteger(ecParameters[1]),
                        new BigInteger(ecParameters[2]),  new BigInteger(ecParameters[3])), protocolPoint);
            }

            //System.out.println("Prime p: " + parameters.getField().getP());
            //System.out.println("Secret key: " + parameters.getAsymmetricalKey().getSecretKey());

            new Watchdog(parameters).start();
            new User(parameters).start();
            new User(parameters).start();
            new User(parameters).start();

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
