# JMH Performance test for digital signature using sm2p256v1 SHA256withSM2 with Bouncy Castle
Use JDK 11.

Test full procedure of the digital signature, including computing the signature as well as verifying the signature.

Using Java Flight Recorder(JFR) as the profiler.

## Build
```sh
mvn clean verify
```

## Run
```sh
java -jar target/benchmarks.jar -prof jfr
```

## Result
[result logs](result.log)

### allocation-tlab
#### 128B Signing
![](jfr/org.sample.BenchmarkSigning.sm2p256v1_128B-Throughput/allocation-tlab.svg)

#### 256B Signing
![](jfr/org.sample.BenchmarkSigning.sm2p256v1_256B-Throughput/allocation-tlab.svg)

#### 1024B Signing
![](jfr/org.sample.BenchmarkSigning.sm2p256v1_1024B-Throughput/allocation-tlab.svg)

#### 1024K Signing
![](jfr/org.sample.BenchmarkSigning.sm2p256v1_1024K-Throughput/allocation-tlab.svg)

#### 128B Verifying
![](jfr/org.sample.BenchmarkVerifying.sm2p256v1_128B-Throughput/allocation-tlab.svg)

#### 256B Verifying
![](jfr/org.sample.BenchmarkVerifying.sm2p256v1_256B-Throughput/allocation-tlab.svg)

#### 1024B Verifying
![](jfr/org.sample.BenchmarkVerifying.sm2p256v1_1024B-Throughput/allocation-tlab.svg)

#### 1024K Verifying
![](jfr/org.sample.BenchmarkVerifying.sm2p256v1_1024K-Throughput/allocation-tlab.svg)

### cpu
#### 128B Signing
![](jfr/org.sample.BenchmarkSigning.sm2p256v1_128B-Throughput/cpu.svg)

#### 256B Signing
![](jfr/org.sample.BenchmarkSigning.sm2p256v1_256B-Throughput/cpu.svg)

#### 1024B Signing
![](jfr/org.sample.BenchmarkSigning.sm2p256v1_1024B-Throughput/cpu.svg)

#### 1024K Signing
![](jfr/org.sample.BenchmarkSigning.sm2p256v1_1024K-Throughput/cpu.svg)

#### 128B Verifying
![](jfr/org.sample.BenchmarkVerifying.sm2p256v1_128B-Throughput/cpu.svg)

#### 256B Verifying
![](jfr/org.sample.BenchmarkVerifying.sm2p256v1_256B-Throughput/cpu.svg)

#### 1024B Verifying
![](jfr/org.sample.BenchmarkVerifying.sm2p256v1_1024B-Throughput/cpu.svg)

#### 1024K Verifying
![](jfr/org.sample.BenchmarkVerifying.sm2p256v1_1024K-Throughput/cpu.svg)

### alloc
#### 128B Signing
![](jfr/org.sample.BenchmarkSigning.sm2p256v1_128B-Throughput/alloc.svg)

#### 256B Signing
![](jfr/org.sample.BenchmarkSigning.sm2p256v1_256B-Throughput/alloc.svg)

#### 1024B Signing
![](jfr/org.sample.BenchmarkSigning.sm2p256v1_1024B-Throughput/alloc.svg)

#### 1024K Signing
![](jfr/org.sample.BenchmarkSigning.sm2p256v1_1024K-Throughput/alloc.svg)

#### 128B Verifying
![](jfr/org.sample.BenchmarkVerifying.sm2p256v1_128B-Throughput/alloc.svg)

#### 256B Verifying
![](jfr/org.sample.BenchmarkVerifying.sm2p256v1_256B-Throughput/alloc.svg)

#### 1024B Verifying
![](jfr/org.sample.BenchmarkVerifying.sm2p256v1_1024B-Throughput/alloc.svg)

#### 1024K Verifying
![](jfr/org.sample.BenchmarkVerifying.sm2p256v1_1024K-Throughput/alloc.svg)

### io-socket
#### 128B Signing
![](jfr/org.sample.BenchmarkSigning.sm2p256v1_128B-Throughput/io-socket.svg)

#### 256B Signing
![](jfr/org.sample.BenchmarkSigning.sm2p256v1_256B-Throughput/io-socket.svg)

#### 1024B Signing
![](jfr/org.sample.BenchmarkSigning.sm2p256v1_1024B-Throughput/io-socket.svg)

#### 1024K Signing
![](jfr/org.sample.BenchmarkSigning.sm2p256v1_1024K-Throughput/io-socket.svg)

#### 128B Verifying
![](jfr/org.sample.BenchmarkVerifying.sm2p256v1_128B-Throughput/io-socket.svg)

#### 256B Verifying
![](jfr/org.sample.BenchmarkVerifying.sm2p256v1_256B-Throughput/io-socket.svg)

#### 1024B Verifying
![](jfr/org.sample.BenchmarkVerifying.sm2p256v1_1024B-Throughput/io-socket.svg)

#### 1024K Verifying
![](jfr/org.sample.BenchmarkVerifying.sm2p256v1_1024K-Throughput/io-socket.svg)

### monitor-blocked
#### 128B Signing
![](jfr/org.sample.BenchmarkSigning.sm2p256v1_128B-Throughput/monitor-blocked.svg)

#### 256B Signing
![](jfr/org.sample.BenchmarkSigning.sm2p256v1_256B-Throughput/monitor-blocked.svg)

#### 1024B Signing
![](jfr/org.sample.BenchmarkSigning.sm2p256v1_1024B-Throughput/monitor-blocked.svg)

#### 1024K Signing
![](jfr/org.sample.BenchmarkSigning.sm2p256v1_1024K-Throughput/monitor-blocked.svg)

#### 128B Verifying
![](jfr/org.sample.BenchmarkVerifying.sm2p256v1_128B-Throughput/monitor-blocked.svg)

#### 256B Verifying
![](jfr/org.sample.BenchmarkVerifying.sm2p256v1_256B-Throughput/monitor-blocked.svg)

#### 1024B Verifying
![](jfr/org.sample.BenchmarkVerifying.sm2p256v1_1024B-Throughput/monitor-blocked.svg)

#### 1024K Verifying
![](jfr/org.sample.BenchmarkVerifying.sm2p256v1_1024K-Throughput/monitor-blocked.svg)
