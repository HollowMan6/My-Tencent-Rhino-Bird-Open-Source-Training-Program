package org.sample;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import java.security.*;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.bouncycastle.util.encoders.Hex;
import java.nio.file.Files;
import java.nio.file.Paths;

@State(Scope.Thread)
public class BenchmarkVerifying {
    SM2Util sm2p256v1;
    String pubKey;
    String prvKey;

    String msg_128B;
    String msg_256B;
    String msg_1024B;
    String msg_1024K;

    String msg_signed_128B;
    String msg_signed_256B;
    String msg_signed_1024B;
    String msg_signed_1024K;

    @Setup
    public void prepare() throws Exception {
        sm2p256v1 = new SM2Util();
        KeyPair keyPair = sm2p256v1.generateSm2KeyPair();
        BCECPrivateKey privateKey = (BCECPrivateKey) keyPair.getPrivate();
        BCECPublicKey publicKey = (BCECPublicKey) keyPair.getPublic();
        pubKey = new String(Hex.encode(publicKey.getQ().getEncoded(true)));
        prvKey = privateKey.getD().toString(16);

        msg_128B = Files.readString(Paths.get("resources/128B.txt"));
        msg_256B = Files.readString(Paths.get("resources/256B.txt"));
        msg_1024B = Files.readString(Paths.get("resources/1024B.txt"));
        msg_1024K = Files.readString(Paths.get("resources/1024K.txt"));

        msg_signed_128B = sm2p256v1.sign(msg_128B, prvKey);
        msg_signed_256B = sm2p256v1.sign(msg_256B, prvKey);
        msg_signed_1024B = sm2p256v1.sign(msg_1024B, prvKey);
        msg_signed_1024K = sm2p256v1.sign(msg_1024K, prvKey);
    }

    @Benchmark
    public void sm2p256v1_128B() throws Exception {
        if (!sm2p256v1.verify(msg_128B, msg_signed_128B, pubKey)) {
            throw new RunnerException("verify failed");
        };
    }

    @Benchmark
    public void sm2p256v1_256B() throws Exception {
        if (!sm2p256v1.verify(msg_256B, msg_signed_256B, pubKey)) {
            throw new RunnerException("verify failed");
        };
    }

    @Benchmark
    public void sm2p256v1_1024B() throws Exception {
        if (!sm2p256v1.verify(msg_1024B, msg_signed_1024B, pubKey)) {
            throw new RunnerException("verify failed");
        };
    }

    @Benchmark
    public void sm2p256v1_1024K() throws Exception {
        if (!sm2p256v1.verify(msg_1024K, msg_signed_1024K, pubKey)) {
            throw new RunnerException("verify failed");
        };
    }

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(BenchmarkVerifying.class.getSimpleName())
                .forks(1)
                .jvmArgs("-ea")
                .build();

        new Runner(opt).run();
    }
}
