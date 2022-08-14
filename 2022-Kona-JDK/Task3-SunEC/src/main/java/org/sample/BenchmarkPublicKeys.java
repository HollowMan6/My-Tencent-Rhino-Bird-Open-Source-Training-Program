package org.sample;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.security.*;

@State(Scope.Thread)
public class BenchmarkPublicKeys {
    KeyPair keyPair;

    @Setup
    public void prepare() throws Exception {
        keyPair = SM2Util.generateSm2KeyPair();
    }

    @Benchmark
    public void sm2p256v1_compressed() throws Exception {
        SM2Util.getHexPublicKey(keyPair.getPublic());
    }

    @Benchmark
    public void sm2p256v1_uncompressed() throws Exception {
        SM2Util.getHexPublicKeyUncompressed(keyPair.getPublic());
    }

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(BenchmarkPublicKeys.class.getSimpleName())
                .forks(1)
                .jvmArgs("-ea")
                .build();

        new Runner(opt).run();
    }
}
