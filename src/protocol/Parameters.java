package protocol;

import subprocess.Sage;

import java.io.IOException;

public class Parameters {
    public static class AsymmetricalKey {
        private final EllipticCurve ellipticCurve;

        private final String point;

        private final String publicKey;

        private final Integer secretKey;

        AsymmetricalKey(EllipticCurve ellipticCurve, String point, Integer secretKey, String publicKey) {
            this.ellipticCurve = ellipticCurve;
            this.point = point;
            this.secretKey = secretKey;
            this.publicKey = publicKey;
        }

        public EllipticCurve getEllipticCurve() {
            return ellipticCurve;
        }

        public String getPoint() {
            return point;
        }

        public String getPublicKey() {
            return publicKey;
        }

        public Integer getSecretKey() {
            return secretKey;
        }
    }

    public static class EllipticCurve {
        private final Integer a;

        private final Integer b;

        private final Field field;

        private final Integer k;

        private final Integer n;

        EllipticCurve(Field field, Integer a, Integer b, Integer n, Integer k) {
            this.field = field;
            this.a = a;
            this.b = b;
            this.n = n;
            this.k = k;
        }

        public Integer getA() {
            return a;
        }

        public Integer getB() {
            return b;
        }

        public Field getField() {
            return field;
        }

        public Integer getK() {
            return k;
        }

        public Integer getN() {
            return n;
        }
    }

    public static class Field {
        private final Integer p;

        Field(Integer p) {
            this.p = p;
        }

        public Integer getP() {
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

    public Parameters(EllipticCurve ellipticCurve, String point) throws IOException, Sage.PythonException {
        this(getRandomAsymmetricalKey(ellipticCurve, point));
    }

    public Parameters(EllipticCurve ellipticCurve) throws IOException, Sage.PythonException {
        this(getRandomAsymmetricalKey(ellipticCurve));
    }

    public Parameters(Field field) throws IOException, Sage.PythonException {
        this(getRandomEllipticCurve(field));
    }

    private static AsymmetricalKey getRandomAsymmetricalKey(EllipticCurve ellipticCurve, String point) throws IOException, Sage.PythonException {
        Field field = ellipticCurve.getField();

        String command = "from base64 import b64decode, b64encode;" +
                "from protocol import get_asymmetric_key;" +
                "from sage.all import GF, loads;" +
                "F = GF(" + field.getP() + ");" +
                "Q = loads(b64decode(\"" + point + "\"));" +
                "secret, public = get_asymmetric_key(F, Q);" +
                "print(\"{} {}\".format(secret, b64encode(public.dumps())))";

        String[] parameters = new Sage().execute(command).trim().split(" ");

        Integer secretKey = Integer.valueOf(parameters[0]);
        String publicKey = parameters[1];

        return new AsymmetricalKey(ellipticCurve, point, secretKey, publicKey);
    }

    private static AsymmetricalKey getRandomAsymmetricalKey(EllipticCurve ellipticCurve) throws IOException, Sage.PythonException {
        Field field = ellipticCurve.getField();

        String command = "from base64 import b64decode, b64encode;" +
                "from protocol import get_point_of_order;" +
                "from sage.all import EllipticCurve, GF;" +
                "p = " + field.getP() + ";" +
                "F = GF(p);" +
                "E = EllipticCurve(F, [" + ellipticCurve.getA() + ", " + ellipticCurve.getB() + "]);" +
                "Q = get_point_of_order(E, p, " + ellipticCurve.getN() + ", " + ellipticCurve.getK() + ");" +
                "print(b64encode(Q.dumps()));";

        String point = new Sage().execute(command).trim();

        return getRandomAsymmetricalKey(ellipticCurve, point);
    }

    public Parameters(Integer securityParameter) throws IOException, Sage.PythonException {
        this(new Field(getRandomP(securityParameter)));
    }

    private static EllipticCurve getRandomEllipticCurve(Field field) throws IOException, Sage.PythonException {
        Integer a;
        Integer b;

        {
            String command = "from protocol import get_random_elliptic_curve;" +
                    "from sage.all import GF;" +
                    "F = GF(" + field.getP() + ");" +
                    "E = get_random_elliptic_curve(F);" +
                    "print(\"{} {}\".format(E.a4(), E.a6()));";

            String parameters[] = new Sage().execute(command).trim().split(" ");

            a = Integer.valueOf(parameters[0]);
            b = Integer.valueOf(parameters[1]);
        }

        Integer n;
        Integer k;

        {
            String command = "from protocol import get_algorithm_parameters;" +
                    "from sage.all import EllipticCurve, GF;" +
                    "p = " + field.getP() + ";" +
                    "F = GF(p);" +
                    "E = EllipticCurve(F, [" + a + ", " + b + "]);" +
                    "n, k = get_algorithm_parameters(E, p);" +
                    "print(\"{} {}\".format(n, k));";

            String parameters[] = new Sage().execute(command).trim().split(" ");

            n = Integer.valueOf(parameters[0]);
            k = Integer.valueOf(parameters[1]);
        }

        return new EllipticCurve(field, a, b, n, k);
    }

    private static Integer getRandomP(Integer securityParameter) throws IOException, Sage.PythonException {
        String command = "from protocol import get_random_prime;" +
                "print(get_random_prime(" + securityParameter + "));";

        String p = new Sage().execute(command).trim();

        return Integer.valueOf(p);
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
