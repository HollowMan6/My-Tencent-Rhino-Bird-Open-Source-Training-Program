package org.sample;

import org.bouncycastle.asn1.gm.GMObjectIdentifiers;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.util.encoders.Hex;

import java.security.*;
import java.security.spec.ECGenParameterSpec;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.security.*;

@State(Scope.Thread)
public class BenchmarkKeyGeneration {
    ECGenParameterSpec sm2Spec;
    KeyPairGenerator kpg;

    @Setup
    public void prepare() throws Exception {
        sm2Spec = new ECGenParameterSpec("sm2p256v1");
        kpg = KeyPairGenerator.getInstance("EC", new BouncyCastleProvider());
    }

    @Benchmark
    public void sm2p256v1_homemade_compressed_BinaryExpansion() throws Exception {
        SM2Util sm2 = new SM2Util(true);

        String prvKey = sm2.generatePrivateKeyHex();
        String pubKeyZip = sm2.getHexPublicKey(prvKey, true);
    }

    @Benchmark
    public void sm2p256v1_homemade_compressed_AddMinus() throws Exception {
        SM2Util sm2 = new SM2Util(true);

        String prvKey = sm2.generatePrivateKeyHex();
        String pubKeyZip = sm2.getHexPublicKey(prvKey, false);
    }

    @Benchmark
    public void sm2p256v1_bc() throws Exception {
        SecureRandom random = new SecureRandom();
        kpg.initialize(sm2Spec, random);

        KeyPair keyPair = kpg.generateKeyPair();

        BCECPrivateKey privateKey = (BCECPrivateKey) keyPair.getPrivate();
        BCECPublicKey publicKey = (BCECPublicKey) keyPair.getPublic();

        String pubKey = new String(Hex.encode(publicKey.getQ().getEncoded(true)));
        String prvKey = privateKey.getD().toString(16);
    }

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(BenchmarkKeyGeneration.class.getSimpleName())
                .forks(1)
                .jvmArgs("-ea")
                .build();

        new Runner(opt).run();
    }
}
