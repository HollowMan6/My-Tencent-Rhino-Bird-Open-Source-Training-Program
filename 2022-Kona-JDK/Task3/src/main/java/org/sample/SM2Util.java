package org.sample;

import java.math.BigInteger;
import java.security.SecureRandom;

public class SM2Util {
  private SecureRandom rng;
  private ECCurveFp curve;
  private ECPointFp G;
  private BigInteger n;

  /**
   * 生成ecparam
   */
  public SM2Util() {
    this.rng = new SecureRandom();
    // 椭圆曲线
    BigInteger p = new BigInteger("FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF00000000FFFFFFFFFFFFFFFF", 16);
    BigInteger a = new BigInteger("FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF00000000FFFFFFFFFFFFFFFC", 16);
    BigInteger b = new BigInteger("28E9FA9E9D9F5E344D5A9E4BCF6509A7F39789F515AB8F92DDBCBD414D940E93", 16);
    this.curve = new ECCurveFp(p, a, b);

    // 基点
    String gxHex = "32C4AE2C1F1981195F9904466A39C9948FE30BBFF2660BE1715A4589334C74C7";
    String gyHex = "BC3736A2F4F6779C59BDCEE36B692153D0A9877CC62A474002DF32E52139F0A0";
    this.G = curve.decodePointHex("04" + gxHex + gyHex);

    this.n = new BigInteger("FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFF7203DF6B21C6052B53BBF40939D54123", 16);
  }

  /**
   * 获取公共椭圆曲线
   */
  public ECCurveFp getGlobalCurve() {
    return curve;
  }

  public String generatePrivateKeyHex() {
    BigInteger random = new BigInteger(n.bitLength(), rng);
    BigInteger d = random.mod(n.subtract(BigInteger.ONE)).add(BigInteger.ONE); // 随机数
    String privateKey = leftPad(d.toString(16), 64);
    return privateKey.toUpperCase();
  }

  /**
   * 生成密钥对：publicKey = privateKey * G
   */
  public String getHexPublicKey(String privateKey, boolean useBinaryExpansion) {
    BigInteger d = new BigInteger(privateKey, 16);
    ECPointFp P = G.multiply(d, useBinaryExpansion); // P = dG，p 为公钥，d 为私钥

    String Px = leftPad(P.getX().toBigInteger().toString(16), 64);
    BigInteger PyI = P.getY().toBigInteger();

    String suffix = "03";
    if (PyI.mod(BigInteger.valueOf(2)).equals(BigInteger.ZERO)) {
      suffix = "02";
    }

    return (suffix + Px).toUpperCase();
  }

  public String getHexPublicKeyUncompressed(String privateKey, boolean useBinaryExpansion) {
    BigInteger d = new BigInteger(privateKey, 16);
    ECPointFp P = G.multiply(d, useBinaryExpansion); // P = dG，p 为公钥，d 为私钥
    String Px = leftPad(P.getX().toBigInteger().toString(16), 64);
    String Py = leftPad(P.getY().toBigInteger().toString(16), 64);
    return ("04" + Px + Py).toUpperCase();
  }

  /**
   * 补全16进制字符串
   */
  public String leftPad(String input, int num) {
    if (input.length() >= num)
      return input;

    return String.format("%0" + (num - input.length()) + "d", 0) + input;
  }

  /**
   * 验证公钥是否为椭圆曲线上的点
   */
  public boolean verifyPublicKey(String publicKey) {
    ECPointFp point = curve.decodePointHex(publicKey);
    if (point == null)
      return false;

    ECFieldElementFp x = point.getX();
    ECFieldElementFp y = point.getY();

    // 验证 y^2 是否等于 x^3 + ax + b
    return y.square().equals(x.multiply(x.square()).add(x.multiply(curve.a)).add(curve.b));
  }

  /**
   * 验证公钥是否等价，等价返回true
   */
  public boolean comparePublicKeyHex(String publicKey1, String publicKey2) {
    ECPointFp point1 = curve.decodePointHex(publicKey1);
    if (point1 == null) return false;

    ECPointFp point2 = curve.decodePointHex(publicKey2);
    if (point2 == null) return false;

    return point1.equals(point2);
  }

  public static void main(String[] args) throws Exception {
    SM2Util sm2 = new SM2Util();

    String prvKey = sm2.generatePrivateKeyHex();
    System.out.println("Private Key: " + prvKey);

    String pubKey = sm2.getHexPublicKeyUncompressed(prvKey, false);
    String pubKeyZip = sm2.getHexPublicKey(prvKey, false);

    System.out.println("Public Key (Uncompressed): " + pubKey);
    System.out.println("Public Key: " + pubKeyZip);

    if (sm2.verifyPublicKey(pubKey)) {
      System.out.println("Public Key (Uncompressed) is valid");
    } else {
      throw new Error("Public Key (Uncompressed) is invalid");
    }

    if (sm2.verifyPublicKey(pubKeyZip)) {
      System.out.println("Public Key is valid");
    } else {
      throw new Error("Public Key is invalid");
    }

    if (sm2.comparePublicKeyHex(pubKey, pubKeyZip)) {
      System.out.println("Public Keys are equivalent");
    } else {
      throw new Error("Public Keys are not equivalent");
    }
  }
}
