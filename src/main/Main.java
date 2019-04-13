package main;

import protocol.Key;
import protocol.Parameters;

import java.math.BigInteger;

public class Main {
    private static final Integer SECURE_PARAMETER = 16;

    public static void main(String[] args) {
        try {
            Integer secureParameter = (System.getenv("SECURE_PARAMETER") == null) ? SECURE_PARAMETER : Integer.valueOf(System.getenv("SECURE_PARAMETER"));

            String p = System.getenv("ECDH_FIELD");
            String ellipticCurveParameters = System.getenv("ECDH_ELLIPTIC_CURVE");
            String protocolPoints = System.getenv("ECDH_POINTS");

            Parameters parameters;
            if (p == null) {
                parameters = new Parameters(secureParameter);
            } else if (ellipticCurveParameters == null) {
                parameters = new Parameters(new Parameters.Field(new BigInteger(p)));
            } else if (protocolPoints == null) {
                Parameters.Field field = new Parameters.Field(new BigInteger(p));

                String[] ecParameters = ellipticCurveParameters.split(" ");
                parameters = new Parameters(new Parameters.EllipticCurve(field,
                        new BigInteger(ecParameters[0]), new BigInteger(ecParameters[1]),
                        new BigInteger(ecParameters[2]), new BigInteger(ecParameters[3])));
            } else {
                Parameters.Field field = new Parameters.Field(new BigInteger(p));

                String[] points = protocolPoints.trim().split(" ");

                String[] ecParameters = ellipticCurveParameters.split(" ");
                parameters = new Parameters(new Parameters.EllipticCurve(field,
                        new BigInteger(ecParameters[0]), new BigInteger(ecParameters[1]),
                        new BigInteger(ecParameters[2]), new BigInteger(ecParameters[3])), points[0], points[1]);
            }

            Parameters clientA = new Parameters(parameters.getEllipticCurve(), parameters.getAsymmetricalKey().getPoint1(), parameters.getAsymmetricalKey().getPoint2());
            Parameters clientB = new Parameters(parameters.getEllipticCurve(), parameters.getAsymmetricalKey().getPoint1(), parameters.getAsymmetricalKey().getPoint2());
            Parameters clientC = new Parameters(parameters.getEllipticCurve(), parameters.getAsymmetricalKey().getPoint1(), parameters.getAsymmetricalKey().getPoint2());

            System.out.println(Key.get(clientA.getAsymmetricalKey().getSecretKey(), clientB.getAsymmetricalKey().getPublicKey1(), clientC.getAsymmetricalKey().getPublicKey2(), clientA.getEllipticCurve().getN(), clientA.getEllipticCurve().getK(), clientA.getField().getP()));
            //System.out.println("Prime p: " + parameters.getField().getP());
            //System.out.println("Secret key: " + parameters.getAsymmetricalKey().getSecretKey());

//            new Watchdog(parameters).start();
//            new User(parameters).start();
//            new User(parameters).start();
//            new User(parameters).start();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
