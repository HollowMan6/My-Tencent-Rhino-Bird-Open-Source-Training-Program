package org.sample;

import java.math.BigInteger;

public class ECPointFp {
  private ECCurveFp curve;
  private ECFieldElementFp x;
  private ECFieldElementFp y;
  private BigInteger z;
  private BigInteger zinv;

  public ECPointFp(ECCurveFp curve, ECFieldElementFp x, ECFieldElementFp y, BigInteger z) {
    this.curve = curve;
    this.x = x;
    this.y = y;
    // 标准射影坐标系：zinv == null 或 z * zinv == 1
    this.z = z == null ? BigInteger.ONE : z;
    this.zinv = null;
  }

  public ECFieldElementFp getX() {
    if (this.zinv == null)
      this.zinv = this.z.modInverse(this.curve.q);

    return this.curve.fromBigInteger(this.x.toBigInteger().multiply(this.zinv).mod(this.curve.q));
  }

  public ECFieldElementFp getY() {
    if (this.zinv == null)
      this.zinv = this.z.modInverse(this.curve.q);

    return this.curve.fromBigInteger(this.y.toBigInteger().multiply(this.zinv).mod(this.curve.q));
  }

  /**
   * 判断相等
   */
  public boolean equals(ECPointFp other) {
    if (this.isInfinity())
      return other.isInfinity();
    if (other.isInfinity())
      return this.isInfinity();

    // u = y2 * z1 - y1 * z2
    BigInteger u = other.y.toBigInteger().multiply(this.z).subtract(this.y.toBigInteger().multiply(other.z))
        .mod(this.curve.q);
    if (!u.equals(BigInteger.ZERO))
      return false;

    // v = x2 * z1 - x1 * z2
    BigInteger v = other.x.toBigInteger().multiply(this.z).subtract(this.x.toBigInteger().multiply(other.z))
        .mod(this.curve.q);
    return v.equals(BigInteger.ZERO);
  }

  /**
   * 是否是无穷远点
   */
  public boolean isInfinity() {
    if ((this.x == null) && (this.y == null))
      return true;
    return this.z.equals(BigInteger.ZERO) && !this.y.toBigInteger().equals(BigInteger.ZERO);
  }

  /**
   * 取反，x 轴对称点
   */
  public ECPointFp negate() {
    return new ECPointFp(this.curve, this.x, this.y.negate(), this.z);
  }

  /**
   * 相加
   *
   * 标准射影坐标系：
   *
   * λ1 = x1 * z2
   * λ2 = x2 * z1
   * λ3 = λ1 − λ2
   * λ4 = y1 * z2
   * λ5 = y2 * z1
   * λ6 = λ4 − λ5
   * λ7 = λ1 + λ2
   * λ8 = z1 * z2
   * λ9 = λ3^2
   * λ10 = λ3 * λ9
   * λ11 = λ8 * λ6^2 − λ7 * λ9
   * x3 = λ3 * λ11
   * y3 = λ6 * (λ9 * λ1 − λ11) − λ4 * λ10
   * z3 = λ10 * λ8
   */
  public ECPointFp add(ECPointFp b) {
    if (this.isInfinity())
      return b;
    if (b.isInfinity())
      return this;

    BigInteger x1 = this.x.toBigInteger();
    BigInteger y1 = this.y.toBigInteger();
    BigInteger z1 = this.z;
    BigInteger x2 = b.x.toBigInteger();
    BigInteger y2 = b.y.toBigInteger();
    BigInteger z2 = b.z;
    BigInteger q = this.curve.q;

    BigInteger w1 = x1.multiply(z2).mod(q);
    BigInteger w2 = x2.multiply(z1).mod(q);
    BigInteger w3 = w1.subtract(w2);
    BigInteger w4 = y1.multiply(z2).mod(q);
    BigInteger w5 = y2.multiply(z1).mod(q);
    BigInteger w6 = w4.subtract(w5);

    if (BigInteger.ZERO.equals(w3)) {
      if (BigInteger.ZERO.equals(w6)) {
        return this.twice(); // this == b，计算自加
      }
      return this.curve.infinity; // this == -b，则返回无穷远点
    }

    BigInteger w7 = w1.add(w2);
    BigInteger w8 = z1.multiply(z2).mod(q);
    BigInteger w9 = w3.pow(2).mod(q);
    BigInteger w10 = w3.multiply(w9).mod(q);
    BigInteger w11 = w8.multiply(w6.pow(2)).subtract(w7.multiply(w9)).mod(q);

    BigInteger x3 = w3.multiply(w11).mod(q);
    BigInteger y3 = w6.multiply(w9.multiply(w1).subtract(w11)).subtract(w4.multiply(w10)).mod(q);
    BigInteger z3 = w10.multiply(w8).mod(q);

    return new ECPointFp(this.curve, this.curve.fromBigInteger(x3), this.curve.fromBigInteger(y3), z3);
  }

  /**
   * 自加
   *
   * 标准射影坐标系：
   *
   * λ1 = 3 * x1^2 + a * z1^2
   * λ2 = 2 * y1 * z1
   * λ3 = y1^2
   * λ4 = λ3 * x1 * z1
   * λ5 = λ2^2
   * λ6 = λ1^2 − 8 * λ4
   * x3 = λ2 * λ6
   * y3 = λ1 * (4 * λ4 − λ6) − 2 * λ5 * λ3
   * z3 = λ2 * λ5
   */
  public ECPointFp twice() {
    if (this.isInfinity())
      return this;
    if (this.y.toBigInteger().signum() == 0)
      return this.curve.infinity;

    BigInteger x1 = this.x.toBigInteger();
    BigInteger y1 = this.y.toBigInteger();
    BigInteger z1 = this.z;
    BigInteger q = this.curve.q;
    BigInteger a = this.curve.a.toBigInteger();

    BigInteger w1 = x1.pow(2).multiply(BigInteger.valueOf(3)).add(a.multiply(z1.pow(2))).mod(q);
    BigInteger w2 = y1.shiftLeft(1).multiply(z1).mod(q);
    BigInteger w3 = y1.pow(2).mod(q);
    BigInteger w4 = w3.multiply(x1).multiply(z1).mod(q);
    BigInteger w5 = w2.pow(2).mod(q);
    BigInteger w6 = w1.pow(2).subtract(w4.shiftLeft(3)).mod(q);

    BigInteger x3 = w2.multiply(w6).mod(q);
    BigInteger y3 = w1.multiply(w4.shiftLeft(2).subtract(w6)).subtract(w5.shiftLeft(1).multiply(w3)).mod(q);
    BigInteger z3 = w2.multiply(w5).mod(q);

    return new ECPointFp(this.curve, this.curve.fromBigInteger(x3), this.curve.fromBigInteger(y3), z3);
  }

  /**
   * 倍点计算
   */
  public ECPointFp multiply(BigInteger k) {
    if (this.isInfinity())
      return this;
    if (k.signum() == 0)
      return this.curve.infinity;

    // 使用加减法
    BigInteger k3 = k.multiply(BigInteger.valueOf(3));
    ECPointFp neg = this.negate();
    ECPointFp Q = this;

    for (int i = k3.bitLength() - 2; i > 0; i--) {
      Q = Q.twice();

      Boolean k3Bit = k3.testBit(i);
      Boolean kBit = k.testBit(i);

      if (k3Bit != kBit) {
        Q = Q.add(k3Bit ? this : neg);
      }
    }

    return Q;
  }
}
