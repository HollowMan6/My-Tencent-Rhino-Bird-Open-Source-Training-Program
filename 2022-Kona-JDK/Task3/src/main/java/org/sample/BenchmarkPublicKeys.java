package org.sample;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.security.*;

@State(Scope.Thread)
public class BenchmarkPublicKeys {
    String prvKey;
    SM2Util sm2;

    @Setup
    public void prepare() throws Exception {
        sm2 = new SM2Util();
        prvKey = sm2.generatePrivateKeyHex();
    }

    @Benchmark
    public void sm2p256v1_compressed() throws Exception {
        sm2.getHexPublicKey(prvKey);
    }

    @Benchmark
    public void sm2p256v1_uncompressed() throws Exception {
        sm2.getHexPublicKeyUncompressed(prvKey);
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
