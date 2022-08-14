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
import java.util.Base64;

public class Test {
    private BouncyCastleProvider provider;
    private X9ECParameters parameters;
    private ECParameterSpec ecParameterSpec;
    private KeyFactory keyFactory;

    public Test() throws Exception {
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
        Test test = new Test();
        SM2Util sm2 = new SM2Util();
        String prvKey = sm2.generatePrivateKeyHex();
        System.out.println("Private Key: " + prvKey);
    
        String pubKey = sm2.getHexPublicKeyUncompressed(prvKey);
        String pubKeyZip = sm2.getHexPublicKey(prvKey);

        System.out.println("Public Key (Uncompressed): " + pubKey);
        System.out.println("Public Key: " + pubKeyZip);

        ECPointFp point = sm2.getGlobalCurve().decodePointHex(pubKey);
        ECPointFp pointZip = sm2.getGlobalCurve().decodePointHex(pubKey);
        if (!point.getY().equals(pointZip.getY())) {
          System.out.println("Note: Error decoding zipped public key!");
        }

        String str = "How are you?";
        System.out.println("To sign: " + str);
        String signStr = test.sign(str, prvKey);
        System.out.println("Signed: " + signStr);
        test.verify(str, signStr, pubKey);
        test.verify(str, signStr, pubKeyZip);
        System.out.println("Verification OK!");
    }
}
