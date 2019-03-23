from sage.all import *

def get_random_prime(b):
    lower_bound = (2 ** (b - 1)) - 1
    higher_bound = (2 ** b) - 1
    prime = random_prime(higher_bound, lbound=lower_bound)
    return prime

def get_random_field(b):
    prime = get_random_prime(b)
    field = GF(prime)
    return field

def get_random_elliptic_curve(field):
    # TODO what if field is prime
    while True:
        A = field.random_element()
        B = field.random_element()
        try:
            E = EllipticCurve(field, [A, B])
            return E
        except ArithmeticError:
            pass

def get_algorithm_parameters(elliptic_curve, p):
    while True:
        while True:
            P = elliptic_curve.random_point()

            n = P.order()
            if n > 1:
                break

        if not n.is_prime():
            divisors = [d for d in n.divisors() if d.is_prime()]
            n = divisors.pop()

        k = GF(n)(p).multiplicative_order()
        if k != 1:
            return (n, k)

def get_point_of_order(elliptic_curve, p, n, k):
    G = GF(p ** k, "a")

    while True:
        Q = elliptic_curve.base_extend(G).random_point()
        try:
            elliptic_curve(Q)
        except ValueError:
            if Q.has_finite_order() and not Q.is_zero():
                break

    m = Q.order()
    if m != n:
        Q = Integer(m / n) * Q
    
    return Q

def get_asymmetric_key(base_field, point):
    # TODO validate that point is not zero
    while True:
        secret = Integer(base_field.random_element())
        public = secret * point
        if not public.is_zero():
            return (secret, public)

def get_3dh_key(secret, public_a, public_b, n, k):
    return public_a.tate_pairing(public_b, n, k) ** secret
