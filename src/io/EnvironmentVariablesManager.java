package io;

import protocol.Parameters;

import java.math.BigInteger;

public class EnvironmentVariablesManager implements InputManager {

    public EnvironmentVariablesManager(){}

    @Override
    public Parameters read() throws Exception {
        String p = System.getenv("ECDH_FIELD");
        String ellipticCurveParameters = System.getenv("ECDH_ELLIPTIC_CURVE");
        String protocolPoints = System.getenv("ECDH_POINTS");

        if (p == null) {
            return new Parameters(8);
        } else if (ellipticCurveParameters == null) {
            return new Parameters(new Parameters.Field(new BigInteger(p)));
        } else if (protocolPoints == null) {
            Parameters.Field field = new Parameters.Field(new BigInteger(p));

            String[] ecParameters = ellipticCurveParameters.split(" ");
            return new Parameters(new Parameters.EllipticCurve(field,
                    new BigInteger(ecParameters[0]),  new BigInteger(ecParameters[1]),
                    new BigInteger(ecParameters[2]),  new BigInteger(ecParameters[3])));
        } else {
            Parameters.Field field = new Parameters.Field(new BigInteger(p));

            String[] points = protocolPoints.trim().split(" ");

            String[] ecParameters = ellipticCurveParameters.split(" ");
            return new Parameters(new Parameters.EllipticCurve(field,
                    new BigInteger(ecParameters[0]),  new BigInteger(ecParameters[1]),
                    new BigInteger(ecParameters[2]),  new BigInteger(ecParameters[3])), points[0], points[1]);
        }
    }

    @Override
    public void save(String path, Parameters parameters) throws Exception {
        new FileManager().save(path, parameters);
    }
}
