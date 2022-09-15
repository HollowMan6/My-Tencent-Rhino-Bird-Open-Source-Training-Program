package org.sample;

import java.math.BigInteger;

public class ECPointFp {
  private ECCurveFp curve;
  private ECFieldElementFp x;
  private ECFieldElementFp y;
  private BigInteger z;
  private BigInteger zinv;
  private BigInteger zSquaredInv;
  private BigInteger zCubedInv;
  private boolean jacob;

  public ECPointFp(ECCurveFp curve, ECFieldElementFp x, ECFieldElementFp y, BigInteger z, boolean jacob) {
    this.curve = curve;
    this.x = x;
    this.y = y;
    // zinv == null 或 z * zinv == 1
    this.z = z == null ? BigInteger.ONE : z;
    // 标准射影坐标系：
    // zinv == null 或 z * zinv == 1
    this.zinv = null;
    // Jacobian加重射影坐标系：
    // zSquaredInv == null 或 z^2 * zSquaredInv == 1
    this.zSquaredInv = null;
    // zCubedInv == null 或 z^3 * zCubedInv == 1
    this.zCubedInv = null;
    this.jacob = jacob;
  }

  public ECFieldElementFp getX() {
    if (this.jacob) {
      if (this.zSquaredInv == null) {
        this.zSquaredInv = this.z.pow(2).modInverse(this.curve.q);
      }
      return this.curve.fromBigInteger(this.x.toBigInteger().multiply(this.zSquaredInv).mod(this.curve.q));
    }
    else {
      if (this.zinv == null)
        this.zinv = this.z.modInverse(this.curve.q);
      return this.curve.fromBigInteger(this.x.toBigInteger().multiply(this.zinv).mod(this.curve.q));
    }
  }

  public ECFieldElementFp getY() {
    if (this.jacob) {
      if (this.zCubedInv == null) {
        this.zCubedInv = this.z.pow(3).modInverse(this.curve.q);
      }
      return this.curve.fromBigInteger(this.y.toBigInteger().multiply(this.zCubedInv).mod(this.curve.q));
    } else {
      if (this.zinv == null)
        this.zinv = this.z.modInverse(this.curve.q);
      return this.curve.fromBigInteger(this.y.toBigInteger().multiply(this.zinv).mod(this.curve.q));
    }
  }

  /**
   * 判断相等
   */
  public boolean equals(ECPointFp other) {
    if (this.isInfinity())
      return other.isInfinity();
    if (other.isInfinity())
      return this.isInfinity();

    if (this.jacob) {
      // u = y2 * z1^3 - y1 * z2^3
      BigInteger u = other.y.toBigInteger().multiply(this.z.pow(3)).subtract(this.y.toBigInteger().multiply(other.z.pow(3)))
          .mod(this.curve.q);
      if (!u.equals(BigInteger.ZERO))
        return false;

      // v = x2 * z1^2 - x1 * z2^2
      BigInteger v = other.x.toBigInteger().multiply(this.z.pow(2)).subtract(this.x.toBigInteger().multiply(other.z.pow(2)))
          .mod(this.curve.q);
      return v.equals(BigInteger.ZERO);
    } else {
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
  }

  /**
   * 是否是无穷远点
   */
  public boolean isInfinity() {
    if ((this.x == null) && (this.y == null))
      return true;
    
    if (this.jacob)
      return this.z.equals(BigInteger.ZERO) && !this.y.toBigInteger().equals(BigInteger.ZERO) && !this.x.toBigInteger().equals(BigInteger.ZERO);
    return this.z.equals(BigInteger.ZERO) && !this.y.toBigInteger().equals(BigInteger.ZERO);
  }

  /**
   * 取反，x 轴对称点
   */
  public ECPointFp negate() {
    return new ECPointFp(this.curve, this.x, this.y.negate(), this.z, this.jacob);
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
   * 
   * Jacobian加重射影坐标系(add-2007-bl)：
   *
   * Z1Z1 = Z12
   * Z2Z2 = Z22
   * U1 = X1*Z2Z2
   * U2 = X2*Z1Z1
   * S1 = Y1*Z2*Z2Z2
   * S2 = Y2*Z1*Z1Z1
   * H = U2-U1
   * I = (2*H)2
   * J = H*I
   * r = 2*(S2-S1)
   * V = U1*I
   * X3 = r2-J-2*V
   * Y3 = r*(V-X3)-2*S1*J
   * Z3 = ((Z1+Z2)2-Z1Z1-Z2Z2)*H
   */
  public ECPointFp add(ECPointFp b) {
    if (this.isInfinity())
      return b;
    if (b.isInfinity())
      return this;

    BigInteger q = this.curve.q;

    BigInteger x1 = this.x.toBigInteger();
    BigInteger y1 = this.y.toBigInteger();
    BigInteger z1 = this.z;
    BigInteger x2 = b.x.toBigInteger();
    BigInteger y2 = b.y.toBigInteger();
    BigInteger z2 = b.z;

    if (this.jacob) {
      BigInteger z1z1 = this.z.pow(2).mod(q);
      BigInteger z2z2 = b.z.pow(2).mod(q);
      BigInteger u1 = x1.multiply(z2z2).mod(q);
      BigInteger u2 = x2.multiply(z1z1).mod(q);
      BigInteger s1 = y1.multiply(z2.multiply(z2z2)).mod(q);
      BigInteger s2 = y2.multiply(z1.multiply(z1z1)).mod(q);
      BigInteger h = u2.subtract(u1).mod(q);
      BigInteger r = s2.subtract(s1).shiftLeft(1).mod(q);
  
      if (BigInteger.ZERO.equals(h)) {
        if (BigInteger.ZERO.equals(r)) {
          return this.twice(); // this == b，计算自加
        }
        return this.curve.infinity; // this == -b，则返回无穷远点
      }
  
      BigInteger i = h.pow(2).shiftLeft(2).mod(q);
      BigInteger j = h.multiply(i).mod(q);
      BigInteger v = u1.multiply(i).mod(q);
      BigInteger x3 = r.pow(2).subtract(j).subtract(v.shiftLeft(1)).mod(q);
      BigInteger y3 = r.multiply(v.subtract(x3)).subtract(s1.multiply(j).shiftLeft(1)).mod(q);
      BigInteger z3 = h.multiply(z1.add(z2).pow(2).subtract(z1z1).subtract(z2z2)).mod(q);
  
      return new ECPointFp(this.curve, this.curve.fromBigInteger(x3), this.curve.fromBigInteger(y3), z3, this.jacob);
    } else {
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

      return new ECPointFp(this.curve, this.curve.fromBigInteger(x3), this.curve.fromBigInteger(y3), z3, this.jacob);
    }
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
   *
   * Jacobian加重射影坐标系(dbl-2007-bl)：
   *
   * XX = X12
   * YY = Y12
   * YYYY = YY2
   * ZZ = Z12
   * S = 2*((X1+YY)2-XX-YYYY)
   * M = 3*XX+a*ZZ2
   * T = M2-2*S
   * X3 = T
   * Y3 = M*(S-T)-8*YYYY
   * Z3 = (Y1+Z1)2-YY-ZZ
   */
  public ECPointFp twice() {
    if (this.isInfinity())
      return this;
    if (this.y.toBigInteger().signum() == 0)
      return this.curve.infinity;

    BigInteger q = this.curve.q;

    BigInteger x1 = this.x.toBigInteger();
    BigInteger xx = x1.pow(2).mod(q);
    BigInteger y1 = this.y.toBigInteger();
    BigInteger yy = y1.pow(2).mod(q);
    BigInteger z1 = this.z;
    BigInteger zz = z1.pow(2).mod(q);

    BigInteger a = this.curve.a.toBigInteger();

    if (this.jacob) {
      BigInteger yyyy = yy.pow(2).mod(q);
      BigInteger s = x1.add(yy).pow(2).subtract(xx).subtract(yyyy).shiftLeft(1).mod(q);
      BigInteger m = xx.shiftLeft(1).add(xx).add(a.multiply(zz.pow(2))).mod(q);

      BigInteger x3 = m.pow(2).subtract(s.shiftLeft(1)).mod(q);
      BigInteger y3 = m.multiply(s.subtract(x3)).subtract(yyyy.shiftLeft(3)).mod(q);
      BigInteger z3 = y1.add(z1).pow(2).subtract(yy).subtract(zz).mod(q);

      return new ECPointFp(this.curve, this.curve.fromBigInteger(x3), this.curve.fromBigInteger(y3), z3, this.jacob);
    } else {
      BigInteger w1 = xx.multiply(BigInteger.valueOf(3)).add(a.multiply(zz)).mod(q);
      BigInteger w2 = y1.shiftLeft(1).multiply(z1).mod(q);
      BigInteger w3 = yy.mod(q);
      BigInteger w4 = w3.multiply(x1).multiply(z1).mod(q);
      BigInteger w5 = w2.pow(2).mod(q);
      BigInteger w6 = w1.pow(2).subtract(w4.shiftLeft(3)).mod(q);

      BigInteger x3 = w2.multiply(w6).mod(q);
      BigInteger y3 = w1.multiply(w4.shiftLeft(2).subtract(w6)).subtract(w5.shiftLeft(1).multiply(w3)).mod(q);
      BigInteger z3 = w2.multiply(w5).mod(q);

      return new ECPointFp(this.curve, this.curve.fromBigInteger(x3), this.curve.fromBigInteger(y3), z3, this.jacob);
    }
  }

  /**
   * 倍点计算
   */
  public ECPointFp multiply(BigInteger k, boolean useBinaryExpansion) {
    if (this.isInfinity())
      return this;
    if (k.signum() == 0)
      return this.curve.infinity;

    if (useBinaryExpansion) {
      // 二进制展开法
      ECPointFp Q = this.curve.infinity;
      for (int j = k.bitLength() - 1; j >= 0; j--) {
        Q = Q.twice();
        if (k.testBit(j)) {
          Q = Q.add(this);
        }
      }
      return Q;
    } else {
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
}
