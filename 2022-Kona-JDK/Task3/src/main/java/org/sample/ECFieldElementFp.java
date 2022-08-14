package org.sample;

import java.math.BigInteger;

/**
 * 椭圆曲线域元素
 */
public class ECFieldElementFp {
  private BigInteger q;
  private BigInteger x;

  public ECFieldElementFp(BigInteger q, BigInteger x) {
    this.x = x;
    this.q = q;
  }

  /**
   * 判断相等
   */
  public boolean equals(ECFieldElementFp other) {
    return this.q.equals(other.q) && this.x.equals(other.x);
  }

  /**
   * 返回具体数值
   */
  public BigInteger toBigInteger() {
    return this.x;
  }

  /**
   * 取反
   */
  public ECFieldElementFp negate() {
    return new ECFieldElementFp(this.q, this.x.negate().mod(this.q));
  }

  /**
   * 相加
   */
  public ECFieldElementFp add(ECFieldElementFp b) {
    return new ECFieldElementFp(this.q, this.x.add(b.toBigInteger()).mod(this.q));
  }

  /**
   * 相减
   */
  public ECFieldElementFp subtract(ECFieldElementFp b) {
    return new ECFieldElementFp(this.q, this.x.subtract(b.toBigInteger()).mod(this.q));
  }

  /**
   * 相乘
   */
  public ECFieldElementFp multiply(ECFieldElementFp b) {
    return new ECFieldElementFp(this.q, this.x.multiply(b.toBigInteger()).mod(this.q));
  }

  /**
   * 相除
   */
  public ECFieldElementFp divide(ECFieldElementFp b) {
    return new ECFieldElementFp(this.q, this.x.multiply(b.toBigInteger().modInverse(this.q)).mod(this.q));
  }

  /**
   * 平方
   */
  public ECFieldElementFp square() {
    return new ECFieldElementFp(this.q, this.x.pow(2).mod(this.q));
  }

  /**
   * 开方
   */
  public ECFieldElementFp sqrt() {
    return new ECFieldElementFp(this.q, this.x.sqrt());
  }
}
