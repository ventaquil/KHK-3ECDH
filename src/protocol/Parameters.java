package protocol;

import subprocess.Sage;

import java.io.IOException;

public class Parameters {
    private final Integer bytesLength;

    private Integer ellipticCurveA;
    private Integer ellipticCurveB;

    private Integer p;

    private Integer n;
    private Integer k;

    private String publicKey;
    private Integer secretKey;

    private String Q;

    private final Sage sage;

    public Parameters(Integer bytesLength) throws IOException, Sage.PythonException {
        this.bytesLength = bytesLength;

        this.sage = new Sage();

        initialize();
    }

    private void initialize() throws IOException, Sage.PythonException {
        p = Integer.valueOf(sage.execute("from protocol import get_random_prime;" +
                "print(get_random_prime(" + bytesLength + "));").trim());

        String[] E = sage.execute("from protocol import get_random_elliptic_curve;" +
                "from sage.all import GF;" +
                "F = GF(" + p + ");" +
                "E = get_random_elliptic_curve(F);" +
                "print(\"{} {}\".format(E.a4(), E.a6()));").trim().split(" ");

        ellipticCurveA = Integer.valueOf(E[0]);
        ellipticCurveB = Integer.valueOf(E[1]);

        String[] P = sage.execute("from protocol import get_algorithm_parameters;" +
                "from sage.all import EllipticCurve, GF;" +
                "p = " + p + ";" +
                "F = GF(p);" +
                "E = EllipticCurve(F, [" + ellipticCurveA + ", " + ellipticCurveB + "]);" +
                "n, k = get_algorithm_parameters(E, p);" +
                "print(\"{} {}\".format(n, k));").trim().split(" ");

        n = Integer.valueOf(P[0]);
        k = Integer.valueOf(P[1]);

        Q = sage.execute("from base64 import b64decode, b64encode;" +
                "from protocol import get_point_of_order;" +
                "from sage.all import EllipticCurve, GF;" +
                "p = " + p + ";" +
                "F = GF(p);" +
                "E = EllipticCurve(F, [" + ellipticCurveA + ", " + ellipticCurveB + "]);" +
                "Q = get_point_of_order(E, p, " + n + ", " + k + ");" +
                "print(b64encode(Q.dumps()));").trim();

        String[] K = sage.execute("from base64 import b64decode, b64encode;" +
                "from protocol import get_asymmetric_key;" +
                "from sage.all import GF, loads;" +
                "F = GF(" + p + ");" +
                "Q = loads(b64decode(\"" + Q + "\"));" +
                "secret, public = get_asymmetric_key(F, Q);" +
                "print(\"{} {}\".format(secret, b64encode(public.dumps())))").trim().split(" ");

        secretKey = Integer.valueOf(K[0]);
        publicKey = K[1];
    }

    public Integer getBytesLength() {
        return bytesLength;
    }

    public Integer getEllipticCurveA() {
        return ellipticCurveA;
    }

    public Integer getEllipticCurveB() {
        return ellipticCurveB;
    }

    public Integer getK() {
        return k;
    }

    public Integer getN() {
        return n;
    }

    public Integer getP() {
        return p;
    }

    public String getQ() {
        return Q;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public Integer getSecretKey() {
        return secretKey;
    }
}
