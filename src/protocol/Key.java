package protocol;

import subprocess.Sage;

import java.io.IOException;
import java.math.BigInteger;

public abstract class Key {
    public static String get(BigInteger secret, String publicA, String publicB, BigInteger n, BigInteger k, BigInteger p) throws IOException, Sage.PythonException {
        String command = "from base64 import b64decode;" +
                "from protocol import get_3dh_key;" +
                "from sage.all import Integer, loads, PolynomialRing, GF;" +
                "public_a = loads(b64decode(\"" + publicA + "\"));" +
                "public_b = loads(b64decode(\"" + publicB + "\"));" +
                "n = Integer(" + n + ");" +
                "k = Integer(" + k + ");" +
                "polynomial = get_3dh_key(" + secret + ", public_a, public_b, n, k);" +
                "R = PolynomialRing(GF(" + p + "), \"a\");" +
                "print(R(polynomial)(n));";

        return new Sage().execute(command).trim();
    }
}
