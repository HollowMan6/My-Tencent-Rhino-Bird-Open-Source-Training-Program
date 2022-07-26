package org.sample;

import org.bouncycastle.asn1.gm.GMNamedCurves;
import org.bouncycastle.asn1.gm.GMObjectIdentifiers;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPrivateKeySpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Hex;

import java.math.BigInteger;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.util.Base64;


public class SM2Util {
    public static KeyPair generateSm2KeyPair() throws Exception {
        final ECGenParameterSpec sm2Spec = new ECGenParameterSpec("sm2p256v1");

        final KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC", "SunEC");
        SecureRandom random = new SecureRandom();

        kpg.initialize(sm2Spec, random);

        KeyPair keyPair = kpg.genKeyPair();
        return keyPair;
    }

    public static String getHexPublicKey(PublicKey publicKey) {
        ECPublicKey pk = (ECPublicKey) publicKey;
        
        BigInteger pubKeyY = pk.getW().getAffineY();

        String suffix = "03";
        if (pubKeyY.mod(BigInteger.valueOf(2)).equals(BigInteger.valueOf(0))) {
            suffix = "02";
        }

        String pubKeyXHex = bigIntegerToHex(pk.getW().getAffineX(), 64);

       return (suffix + pubKeyXHex).toUpperCase();

    }

    public static String getHexPublicKeyUncompressed(PublicKey publicKey) {
        ECPublicKey pk = (ECPublicKey) publicKey;
        
        String pubKeyXHex = bigIntegerToHex(pk.getW().getAffineX(), 64);
        String pubKeyYHex = bigIntegerToHex(pk.getW().getAffineY(), 64);
        return ("04" + pubKeyXHex + pubKeyYHex).toUpperCase();
    }

    public static String getHexPrivateKey(PrivateKey privateKey) {
        ECPrivateKey pk = (ECPrivateKey) privateKey;
        return pk.getS().toString(16).toUpperCase();
    }

    public static String bigIntegerToHex(BigInteger num, int width) {
        String str = num.toString(16);
        if (str.length() < width) {
            str = String.format("%0" + (width - str.length()) + "d", 0) + str;
        }
        return str;
    }

    private BouncyCastleProvider provider;
    private X9ECParameters parameters;
    private ECParameterSpec ecParameterSpec;
    private KeyFactory keyFactory;

    private SM2Util() throws Exception {
        provider = new BouncyCastleProvider();
        parameters = GMNamedCurves.getByName("sm2p256v1");
        ecParameterSpec = new ECParameterSpec(parameters.getCurve(),
                parameters.getG(), parameters.getN(), parameters.getH());
        keyFactory = KeyFactory.getInstance("EC", provider);
    }

    public String sign(String plainText, String prvKey) throws Exception {
        Signature signature = Signature.getInstance(GMObjectIdentifiers.sm2sign_with_sm3.toString(), provider);

        BigInteger bigInteger = new BigInteger(prvKey, 16);
        BCECPrivateKey privateKey = (BCECPrivateKey) keyFactory.generatePrivate(new ECPrivateKeySpec(bigInteger,
                ecParameterSpec));

        signature.initSign(privateKey);

        signature.update(plainText.getBytes());

        return Base64.getEncoder().encodeToString(signature.sign());
    }

    public void verify(String plainText, String signatureValue, String pubKey) throws Exception {
        Signature signature = Signature.getInstance(GMObjectIdentifiers.sm2sign_with_sm3.toString(), provider);

        ECPoint ecPoint = parameters.getCurve().decodePoint(Hex.decode(pubKey));
        BCECPublicKey key = (BCECPublicKey) keyFactory.generatePublic(new ECPublicKeySpec(ecPoint, ecParameterSpec));

        signature.initVerify(key);
        signature.update(plainText.getBytes());
        if (!signature.verify(Base64.getDecoder().decode(signatureValue))) {
            throw new Exception("Failed to verify signature");
        };
    }

    public static void main(String[] args) throws Exception {
        SM2Util sm2 = new SM2Util();
        KeyPair keyPair = generateSm2KeyPair();

        String pubKey = getHexPublicKeyUncompressed(keyPair.getPublic());
        String pubKeyZip = getHexPublicKey(keyPair.getPublic());
        String prvKey = getHexPrivateKey(keyPair.getPrivate());

        System.out.println("Public Key (Uncompressed): " + pubKey);
        System.out.println("Public Key: " + pubKeyZip);
        System.out.println("Private Key: " + prvKey);

        String str = "How are you?";
        System.out.println("To sign: " + str);
        String signStr = sm2.sign(str, prvKey);
        System.out.println("Signed: " + signStr);
        sm2.verify(str, signStr, pubKey);
        sm2.verify(str, signStr, pubKeyZip);
        System.out.println("Verification OK!");
    }
}
