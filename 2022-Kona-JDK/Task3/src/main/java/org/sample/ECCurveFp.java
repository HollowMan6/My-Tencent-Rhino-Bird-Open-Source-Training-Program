package org.sample;

import java.math.BigInteger;

/**
 * 椭圆曲线 y^2 = x^3 + ax + b
 */
public class ECCurveFp {
  public BigInteger q;
  public ECFieldElementFp a;
  public ECFieldElementFp b;
  public ECPointFp infinity;

  public ECCurveFp(BigInteger q, BigInteger a, BigInteger b) {
    this.q = q;
    this.a = this.fromBigInteger(a);
    this.b = this.fromBigInteger(b);
    this.infinity = new ECPointFp(this, null, null, null); // 无穷远点
  }

  /**
   * 判断两个椭圆曲线是否相等
   */
  public boolean equals(ECCurveFp other) {
    if (other == this)
      return true;
    return (this.q.equals(other.q) && this.a.equals(other.a) && this.b.equals(other.b));
  }

  /**
   * 生成椭圆曲线域元素
   */
  public ECFieldElementFp fromBigInteger(BigInteger x) {
    return new ECFieldElementFp(this.q, x);
  }

  /**
   * 解析 16 进制串为椭圆曲线点
   */
  public ECPointFp decodePointHex(String s) {
    switch (Integer.parseInt(s.substring(0, 2), 16)) {
      // 第一个字节
      case 0:
        return this.infinity;
      case 2:
      case 3:
        ECFieldElementFp x = this.fromBigInteger(new BigInteger(s.substring(2), 16));
        // 对p ≡ 3 (mod4)，即存在正整数u，使得p = 4u+3
        // 计算y = (√ (x^3 + ax + b) % p)^(u+1) modp
        ECFieldElementFp y = this.fromBigInteger(x.multiply(x.square()).add(
            x.multiply(this.a)).add(this.b).toBigInteger().modPow(
                this.q.divide(BigInteger.valueOf(4)).add(BigInteger.ONE), this.q));
        // 算出结果2进制最后1位不等于第1个字节-2则取反
        if (!y.toBigInteger().mod(BigInteger.TWO).equals(new BigInteger(s.substring(0, 2), 16).subtract(BigInteger.TWO))) {
          y = y.negate();
        }
        return new ECPointFp(this, x, y, null);
      case 4:
      case 6:
      case 7:
        int len = (s.length() - 2) / 2;
        String xHex = s.substring(2, len + 2);
        String yHex = s.substring(len + 2);

        return new ECPointFp(this, this.fromBigInteger(new BigInteger(xHex, 16)),
            this.fromBigInteger(new BigInteger(yHex, 16)), null);
      default:
        // 不支持
        return null;
    }
  }
}
