package org.sample;

// Part of http://faculty.washington.edu/moishe/tcss581/modulararith/ModularArith.java

import java.math.BigInteger;

public class sqrtModuloPrime {
      /**
   * Calculates the GCD of two integers.
   * 
   * @param a the first integer
   * @param b the second integer
   * @return the greatest common divisor of a & b
   */
  public static BigInteger GCD(BigInteger a, BigInteger b) {
    BigInteger zero = BigInteger.ZERO;
    if (a.multiply(b).compareTo(zero) == 0)
      return a.add(b); // EXIT condition
    else
      return GCD(b, a.mod(b)); // the recursive call
  }

  /**
   * Calculates the inverse modulus: a^(-1) mod b
   * 
   * @param aux the helper array
   * @param a   the base
   * @param b   the the modulus
   * @return a^(-1) mod b, or -1 if the inverse does not exist.
   */
  public static BigInteger inverseMod(BigInteger[] aux, BigInteger a, BigInteger b) {
    BigInteger zero = BigInteger.ZERO;
    BigInteger one = BigInteger.ONE;
    if (GCD(a, b).compareTo(one) == 1) // gcd a,b > 1
      return null;
    else {
      // aux[0] > 0
      return (aux[0].compareTo(zero) == 1 ? aux[0].mod(b) : ((aux[0].mod(b)).add(b)));
    }
  }

  /**
   * Calculates power of a^exp % m.
   * 
   * @param m   the mod
   * @param exp the exponent
   * @param a   the base
   * @return a^exp % m.
   */
  public static BigInteger modPower(BigInteger m, BigInteger exp, BigInteger a) {
    BigInteger zero = BigInteger.ZERO;
    BigInteger one = BigInteger.ONE;
    BigInteger two = BigInteger.TWO;
    if (exp.compareTo(one) == 0)
      return a.mod(m);
    if (exp.compareTo(zero) == 0)
      return one;
    if (exp.mod(two).compareTo(zero) == 0) {
      BigInteger x = modPower(m, exp.divide(two), a);
      return (x.multiply(x)).mod(m);
    } else {
      BigInteger x = modPower(m, exp.subtract(one).divide(two), a);
      return (x.multiply(x).multiply(a)).mod(m);
    }
  }

  /**
   * Calculates square root of res mod p.
   * 
   * @param res the residue
   * @param p   the prime number
   * @return square root of res mod p or null if none can be found
   */
  public static BigInteger sqrtP(BigInteger res, BigInteger p) {
    BigInteger zero = BigInteger.ZERO;
    BigInteger one = BigInteger.ONE;
    BigInteger two = BigInteger.TWO;
    if (p.mod(two).compareTo(zero) == 0)
      return null; // p not prime odd prime
    BigInteger q = (p.subtract(one)).divide(two);
    // make sure res is a residue mod p by checking that res^q mod p == 1
    if (modPower(p, q, res).compareTo(one) != 0)
      return null;

    while (q.mod(two).compareTo(zero) == 0) {
      q = q.divide(two);
      // if res^q mod p != 1 run the complicated root find
      if (modPower(p, q, res).compareTo(one) != 0) {
        return complexSqrtP(res, q, p);
      }

    }
    // Code gets here if res^q mod p were all 1's and now q is odd
    // then root = res^((q+1)/2) mod p
    q = (q.add(one)).divide(two);
    return modPower(p, q, res);
  }

  /**
   * Calculates square root of res mod p using a start exponent q.
   * 
   * @param res the residue
   * @param q   the prime number
   * @param p   the prime number
   * @return square root of res mod p or null if none can be found
   */
  private static BigInteger complexSqrtP(BigInteger res, BigInteger q, BigInteger p) {
    BigInteger a = findNonResidue(p);
    if (a == null)
      return null;
    BigInteger zero = BigInteger.ZERO;
    BigInteger one = BigInteger.ONE;
    BigInteger two = BigInteger.TWO;
    BigInteger t = (p.subtract(one)).divide(two);
    BigInteger negativePower = t; // a^negativePower mod p = -1 mod p this will be used to get the right power
    // res^q mod p = a^((p-1)/2) mod p

    while (q.mod(two).compareTo(zero) == 0) {
      q = q.divide(two);
      t = t.divide(two);
      // check to make sure that the right power was gonnen
      if (modPower(p, q, res).compareTo(modPower(p, t, a)) != 0) {
        // -(a^t mod p) = a^t*a^negativePower mod p = a^t+(negativePower) mod p
        t = t.add(negativePower);
      }
    }
    BigInteger helper[] = { one, one };
    BigInteger inverceRes = inverseMod(helper, res, p);
    // inverceRes^((q-1)/2)
    q = (q.subtract(one)).divide(two);
    // System.out.println("p:"+p+" q:"+q+"invres: "+inverceRes);
    BigInteger partone = modPower(p, q, inverceRes);
    // a^(t/2)
    t = t.divide(two);
    BigInteger parttwo = modPower(p, t, a);
    BigInteger root;
    root = partone.multiply(parttwo);
    root = root.mod(p);
    return root;
  }

  /**
   * Finds the non residue of the prime p
   * 
   * @param q the prime number
   * @return square root of res mod p or null if none can be found
   */
  private static BigInteger findNonResidue(BigInteger p) {
    BigInteger one = BigInteger.ONE;
    BigInteger two = BigInteger.TWO;
    // pick numbers till a^((p-1)/2) = -1;
    int a = 2;
    BigInteger q = (p.subtract(one)).divide(two);
    while (true) {
      if (modPower(p, q, BigInteger.valueOf(a)).compareTo(one) != 0) {
        return BigInteger.valueOf(a);
      }
      // If i tried all the numbers in an int and got nothing somthing is wrong...
      // this is taking too long.
      if (a == 0)
        return null;
      a++;
    }
  }
}
