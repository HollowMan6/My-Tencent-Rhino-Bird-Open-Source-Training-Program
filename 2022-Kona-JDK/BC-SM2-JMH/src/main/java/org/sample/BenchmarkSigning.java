package org.sample;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import java.security.*;
import java.nio.file.Files;
import java.nio.file.Paths;

@State(Scope.Thread)
public class BenchmarkSigning {
    SM2Util sm2p256v1;
    PublicKey pubKey;
    PrivateKey prvKey;

    byte[] msg_128B;
    byte[] msg_256B;
    byte[] msg_1024B;
    byte[] msg_1024K;

    @Setup
    public void prepare() throws Exception {
        sm2p256v1 = new SM2Util();
        KeyPair keyPair = sm2p256v1.generateSm2KeyPair();
        prvKey = keyPair.getPrivate();
        pubKey = keyPair.getPublic();

        msg_128B = Files.readAllBytes(Paths.get("resources/128B.txt"));
        msg_256B = Files.readAllBytes(Paths.get("resources/256B.txt"));
        msg_1024B = Files.readAllBytes(Paths.get("resources/1024B.txt"));
        msg_1024K = Files.readAllBytes(Paths.get("resources/1024K.txt"));
    }

    @Benchmark
    public void sm2p256v1_128B() throws Exception {
        sm2p256v1.sign(msg_128B, prvKey);
    }

    @Benchmark
    public void sm2p256v1_256B() throws Exception {
        sm2p256v1.sign(msg_256B, prvKey);
    }

    @Benchmark
    public void sm2p256v1_1024B() throws Exception {
        sm2p256v1.sign(msg_1024B, prvKey);
    }

    @Benchmark
    public void sm2p256v1_1024K() throws Exception {
        sm2p256v1.sign(msg_1024K, prvKey);
    }

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(BenchmarkSigning.class.getSimpleName())
                .forks(1)
                .jvmArgs("-ea")
                .build();

        new Runner(opt).run();
    }
}
