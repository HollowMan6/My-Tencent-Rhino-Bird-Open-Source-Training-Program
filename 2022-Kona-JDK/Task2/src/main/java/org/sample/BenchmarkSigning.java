package org.sample;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import java.nio.file.Files;
import java.nio.file.Paths;

@State(Scope.Thread)
public class BenchmarkSigning {
    DigitalSignature secp256r1_128B;
    DigitalSignature secp256k1_128B;
    byte[] msg_128B;

    DigitalSignature secp256r1_256B;
    DigitalSignature secp256k1_256B;
    byte[] msg_256B;

    DigitalSignature secp256r1_1024B;
    DigitalSignature secp256k1_1024B;
    byte[] msg_1024B;

    DigitalSignature secp256r1_1024K;
    DigitalSignature secp256k1_1024K;
    byte[] msg_1024K;

    @Setup
    public void prepare() throws Exception {
        secp256r1_128B = new DigitalSignature("secp256r1");
        secp256k1_128B = new DigitalSignature("secp256k1");
        msg_128B = Files.readAllBytes(Paths.get("resources/128B.txt"));

        secp256r1_256B = new DigitalSignature("secp256r1");
        secp256k1_256B = new DigitalSignature("secp256k1");
        msg_256B = Files.readAllBytes(Paths.get("resources/256B.txt"));

        secp256r1_1024B = new DigitalSignature("secp256r1");
        secp256k1_1024B = new DigitalSignature("secp256k1");
        msg_1024B = Files.readAllBytes(Paths.get("resources/1024B.txt"));

        secp256r1_1024K = new DigitalSignature("secp256r1");
        secp256k1_1024K = new DigitalSignature("secp256k1");
        msg_1024K = Files.readAllBytes(Paths.get("resources/1024K.txt"));
    }

    @Benchmark
    public void secp256r1_128B() throws Exception {
        secp256r1_128B.sign(msg_128B);
    }

    @Benchmark
    public void secp256k1_128B() throws Exception {
        secp256k1_128B.sign(msg_128B);
    }

    @Benchmark
    public void secp256r1_256B() throws Exception {
        secp256r1_256B.sign(msg_256B);
    }

    @Benchmark
    public void secp256k1_256B() throws Exception {
        secp256k1_256B.sign(msg_256B);
    }

    @Benchmark
    public void secp256r1_1024B() throws Exception {
        secp256r1_1024B.sign(msg_1024B);
    }

    @Benchmark
    public void secp256k1_1024B() throws Exception {
        secp256k1_1024B.sign(msg_1024B);
    }

    @Benchmark
    public void secp256r1_1024K() throws Exception {
        secp256r1_1024K.sign(msg_1024K);
    }

    @Benchmark
    public void secp256k1_1024K() throws Exception {
        secp256k1_1024K.sign(msg_1024K);
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
