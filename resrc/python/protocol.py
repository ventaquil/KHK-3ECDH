from sage.all import *


def get_random_prime(b, attempts=512):
    lower_bound = (2 ** (b - 1)) - 1
    higher_bound = (2 ** b) - 1
    for _ in range(attempts):
        prime = random_prime(lbound=lower_bound, n=higher_bound)
        candidate = (6 * prime) - 1
        if candidate.is_prime() and ((candidate % 3) == 2):
            return candidate
    return random_prime(lbound=lower_bound, n=higher_bound)


def get_random_field(b):
    prime = get_random_prime(b)
    field = GF(prime)
    return field


def get_random_elliptic_curve(field):
    # TODO what if field is prime
    while True:
        order = field.order()
        A = 0 if (((order + 1) % 6) == 0) and ((order + 1) // 6).is_prime() else field.random_element()
        B = field.random_element()
        try:
            E = EllipticCurve(field, [A, B])
            return E
        except ArithmeticError:
            pass


def get_algorithm_parameters(elliptic_curve, p):
    if (((p + 1) % 6) == 0) and ((p + 1) // 6).is_prime():
        return (p + 1) // 6, 2

    while True:
        while True:
            P = elliptic_curve.random_point()

            n = P.order()
            if n > 1:
                break

        divisors = [d for d in n.divisors() if d.is_prime()]
        elements = [GF(n)(p) for n in divisors]
        elements = [element for element in elements if not element.is_zero()]
        if len(elements) > 0:
            k = max([element.multiplicative_order() for element in elements])

            return n, k


def get_point_of_order(elliptic_curve, p, n, k):
    G = GF(p ** k, "a")

    while True:
        Q = elliptic_curve.base_extend(G).random_point()
        try:
            elliptic_curve(Q)
        except ValueError:
            if (((p + 1) % 6) == 0) and ((p + 1) // 6).is_prime():
                if not Q.is_zero() and not (6 * Q).is_zero():
                    return 6 * Q

            if Q.has_finite_order() and not Q.is_zero():
                break

    m = Q.order()
    if m != n:
        Q = Integer(m / n) * Q

    return Q


def get_asymmetric_key(base_field, point1, point2):
    # TODO validate that point is not zero
    while True:
        secret = Integer(base_field.random_element())
        public1 = secret * point1
        public2 = secret * point2
        if not public1.is_zero() and not public2.is_zero():
            return secret, public1, public2


def tate_pairing(P, Q, n, k):
    if P.curve() != Q.curve():
        pass  # TODO throw exception

    a = P.curve().a4()
    p = P.curve().base_field().characteristic()

    def g(P, Q, R):
        x_p, y_p = P.xy()
        x_q, y_q = Q.xy()
        x_r, y_r = R.xy()

        if (x_p == x_q) and ((y_p + y_q) == 0):
            return x_r - x_p

        if P == Q:
            l = (3 * (x_p ** 2) + a) / (2 * y_p)
        else:
            l = (y_p - y_q) / (x_p - x_q)

        return (y_r - y_p - l * (x_r - x_p)) / (x_r + x_p + x_q - (l ** 2))

    b = bin(n)[3:]
    T = P
    f = 1
    for i in b:
        f **= 2
        f *= g(T, T, Q)
        T *= 2
        if i == "1":
            f *= g(T, P, Q)
            T += P

    return f ** (((p ** k) - 1) / n)


def get_3dh_key(secret, public_a, public_b, n, k):
    return tate_pairing(public_a, public_b, n, k) ** secret
