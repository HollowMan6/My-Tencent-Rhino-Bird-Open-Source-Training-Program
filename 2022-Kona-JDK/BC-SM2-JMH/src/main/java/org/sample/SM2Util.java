package org.sample;

import org.bouncycastle.asn1.gm.GMObjectIdentifiers;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.*;
import java.security.spec.ECGenParameterSpec;

public class SM2Util {
    private BouncyCastleProvider provider;

    public SM2Util() throws Exception {
        provider = new BouncyCastleProvider();
    }

    /**
     * SM2算法生成密钥对
     */
    public KeyPair generateSm2KeyPair() throws Exception {
        final ECGenParameterSpec sm2Spec = new ECGenParameterSpec("sm2p256v1");
        // 获取一个椭圆曲线类型的密钥对生成器
        final KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC", provider);
        SecureRandom random = new SecureRandom();
        // 使用SM2的算法区域初始化密钥生成器
        kpg.initialize(sm2Spec, random);
        // 获取密钥对
        KeyPair keyPair = kpg.generateKeyPair();
        return keyPair;
    }

    /**
     * 签名
     */
    public byte[] sign(byte[] plainText, PrivateKey privateKey) throws Exception {
        // 创建签名对象
        Signature signature = Signature.getInstance(GMObjectIdentifiers.sm2sign_with_sha256.toString(), provider);
        // 初始化为签名状态
        signature.initSign(privateKey);
        // 传入签名字节
        signature.update(plainText);
        // 签名
        return signature.sign();
    }

    public void verify(byte[] text, byte[] signatureValue, PublicKey pubKey) throws Exception {
        // 创建签名对象
        Signature signature = Signature.getInstance(GMObjectIdentifiers.sm2sign_with_sha256.toString(), provider);
        // 初始化为验签状态
        signature.initVerify(pubKey);
        signature.update(text);
        if (!signature.verify(signatureValue)) {
            throw new Exception("Failed to verify signature!");
        }
    }

    public static void main(String[] args) throws Exception {
        byte[] str = "How are you?".getBytes();
        SM2Util sm2 = new SM2Util();
        KeyPair keyPair = sm2.generateSm2KeyPair();
        PrivateKey privateKey = keyPair.getPrivate();
        PublicKey publicKey = keyPair.getPublic();

        System.out.println("Private Key: " + privateKey);
        System.out.println("Public Key: " + publicKey);

        byte[] signStr = sm2.sign(str, privateKey);
        sm2.verify(str, signStr, publicKey);
        System.out.println("Test OK!");
    }
}
