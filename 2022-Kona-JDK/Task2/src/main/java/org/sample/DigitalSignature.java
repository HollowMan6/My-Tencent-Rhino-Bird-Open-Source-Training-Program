package org.sample;

import java.security.*;
import java.security.spec.*;

public class DigitalSignature {
    PrivateKey privKey;
    PublicKey pubKey;
    byte[] sig;

    public DigitalSignature(String curve) throws Exception {
        // Create a public and private key for specified curve name.
        KeyPairGenerator g = KeyPairGenerator.getInstance("EC", "SunEC");
        ECGenParameterSpec ecsp = new ECGenParameterSpec(curve);
        g.initialize(ecsp);
        KeyPair kp = g.genKeyPair();
        privKey = kp.getPrivate();
        pubKey = kp.getPublic();
    }

    public void sign(byte[] msg) throws Exception {
        // Select the signature algorithm.
        Signature s = Signature.getInstance("SHA256withECDSA", "SunEC");
        s.initSign(privKey);
        // Compute the signature.   
        s.update(msg);
        sig = s.sign();
    }

    public void verify(byte[] msg) throws Exception {
        // Verify the signature.
        Signature s = Signature.getInstance("SHA256withECDSA", "SunEC");
        s.initVerify(pubKey);
        s.update(msg);

        if (!s.verify(sig)) {
            throw new RuntimeException("Invalid signature");
        }
    }
}
