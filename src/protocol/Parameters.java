package protocol;

import subprocess.Sage;

import java.io.IOException;
import java.math.BigInteger;

public class Parameters {
    public static class AsymmetricalKey {
        private final EllipticCurve ellipticCurve;

        private final String point1;

        private final String point2;

        private final String publicKey1;

        private final String publicKey2;

        private final BigInteger secretKey;

        public AsymmetricalKey(EllipticCurve ellipticCurve, String point1, String point2, BigInteger secretKey, String publicKey1, String publicKey2) {
            this.ellipticCurve = ellipticCurve;
            this.point1 = point1;
            this.point2 = point2;
            this.secretKey = secretKey;
            this.publicKey1 = publicKey1;
            this.publicKey2 = publicKey2;
        }

        public EllipticCurve getEllipticCurve() {
            return ellipticCurve;
        }

        public String getPoint1() {
            return point1;
        }

        public String getPoint2() {
            return point2;
        }

        public String getPublicKey1() {
            return publicKey1;
        }

        public String getPublicKey2() {
            return publicKey2;
        }

        public BigInteger getSecretKey() {
            return secretKey;
        }
    }

    public static class EllipticCurve {
        private final BigInteger a;

        private final BigInteger b;

        private final Field field;

        private final BigInteger k;

        private final BigInteger n;

        public EllipticCurve(Field field, BigInteger a, BigInteger b, BigInteger n, BigInteger k) {
            this.field = field;
            this.a = a;
            this.b = b;
            this.n = n;
            this.k = k;
        }

        public BigInteger getA() {
            return a;
        }

        public BigInteger getB() {
            return b;
        }

        public Field getField() {
            return field;
        }

        public BigInteger getK() {
            return k;
        }

        public BigInteger getN() {
            return n;
        }
    }

    public static class Field {
        private final BigInteger p;

        public Field(BigInteger p) {
            this.p = p;
        }

        public BigInteger getP() {
            return p;
        }
    }

    private final AsymmetricalKey asymmetricalKey;

    private final EllipticCurve ellipticCurve;

    private final Field field;

    private Parameters(Field field, EllipticCurve ellipticCurve, AsymmetricalKey asymmetricalKey) {
        this.field = field;
        this.ellipticCurve = ellipticCurve;
        this.asymmetricalKey = asymmetricalKey;
    }

    public Parameters(AsymmetricalKey asymmetricalKey) {
        this(asymmetricalKey.getEllipticCurve().getField(), asymmetricalKey.getEllipticCurve(), asymmetricalKey);
    }

    public Parameters(EllipticCurve ellipticCurve, String point1, String point2) throws IOException, Sage.PythonException {
        this(getRandomAsymmetricalKey(ellipticCurve, point1, point2));
    }

    public Parameters(EllipticCurve ellipticCurve) throws IOException, Sage.PythonException {
        this(getRandomAsymmetricalKey(ellipticCurve));
    }

    public Parameters(Field field) throws IOException, Sage.PythonException {
        this(getRandomEllipticCurve(field));
    }

    private static AsymmetricalKey getRandomAsymmetricalKey(EllipticCurve ellipticCurve, String point1, String point2) throws IOException, Sage.PythonException {
        Field field = ellipticCurve.getField();

        String command = "from base64 import b64decode, b64encode;" +
                "from protocol import get_asymmetric_key;" +
                "from sage.all import GF, loads;" +
                "F = GF(" + field.getP() + ");" +
                "P = loads(b64decode(\"" + point1 + "\"));" +
                "Q = loads(b64decode(\"" + point2 + "\"));" +
                "secret, public1, public2 = get_asymmetric_key(F, P, Q);" +
                "print(\"{} {} {}\".format(secret, b64encode(public1.dumps()), b64encode(public2.dumps())))";

        String[] parameters = new Sage().execute(command).trim().split(" ");

        BigInteger secretKey = new BigInteger(parameters[0]);
        String publicKey1 = parameters[1];
        String publicKey2 = parameters[2];

        return new AsymmetricalKey(ellipticCurve, point1, point2, secretKey, publicKey1, publicKey2);
    }

    private static AsymmetricalKey getRandomAsymmetricalKey(EllipticCurve ellipticCurve) throws IOException, Sage.PythonException {
        Field field = ellipticCurve.getField();

        String command = "from base64 import b64decode, b64encode;" +
                "from protocol import get_point_of_order;" +
                "from sage.all import EllipticCurve, GF, Integer;" +
                "p = Integer(" + field.getP() + ");" +
                "F = GF(p);" +
                "E = EllipticCurve(F, [" + ellipticCurve.getA() + ", " + ellipticCurve.getB() + "]);" +
                "P = get_point_of_order(E, p, " + ellipticCurve.getN() + ", " + ellipticCurve.getK() + ");" +
                "Q = get_point_of_order(E, p, " + ellipticCurve.getN() + ", " + ellipticCurve.getK() + ");" +
                "print(\"{} {}\".format(b64encode(P.dumps()), b64encode(Q.dumps())));";

        String[] parameters = new Sage().execute(command).trim().split(" ");

        String point1 = parameters[0];
        String point2 = parameters[1];

        return getRandomAsymmetricalKey(ellipticCurve, point1, point2);
    }

    public Parameters(Integer securityParameter) throws IOException, Sage.PythonException {
        this(new Field(getRandomP(securityParameter)));
    }

    private static EllipticCurve getRandomEllipticCurve(Field field) throws IOException, Sage.PythonException {
        BigInteger a;
        BigInteger b;

        {
            String command = "from protocol import get_random_elliptic_curve;" +
                    "from sage.all import GF;" +
                    "F = GF(" + field.getP() + ");" +
                    "E = get_random_elliptic_curve(F);" +
                    "print(\"{} {}\".format(E.a4(), E.a6()));";

            String parameters[] = new Sage().execute(command).trim().split(" ");

            a = new BigInteger(parameters[0]);
            b = new BigInteger(parameters[1]);
        }

        BigInteger n;
        BigInteger k;

        {
            String command = "from protocol import get_algorithm_parameters;" +
                    "from sage.all import EllipticCurve, GF, Integer;" +
                    "p = Integer(" + field.getP() + ");" +
                    "F = GF(p);" +
                    "E = EllipticCurve(F, [" + a + ", " + b + "]);" +
                    "n, k = get_algorithm_parameters(E, p);" +
                    "print(\"{} {}\".format(n, k));";

            String parameters[] = new Sage().execute(command).trim().split(" ");

            n = new BigInteger(parameters[0]);
            k = new BigInteger(parameters[1]);
        }

        return new EllipticCurve(field, a, b, n, k);
    }

    private static BigInteger getRandomP(Integer securityParameter) throws IOException, Sage.PythonException {
        String command = "from protocol import get_random_prime;" +
                "print(get_random_prime(" + securityParameter + "));";

        String p = new Sage().execute(command).trim();

        return new BigInteger(p);
    }

    public AsymmetricalKey getAsymmetricalKey() {
        return asymmetricalKey;
    }

    public EllipticCurve getEllipticCurve() {
        return ellipticCurve;
    }

    public Field getField() {
        return field;
    }
}
