# Performance test for digital signature comparing between secp256r1 and secp256k1 using SHA256withECDSA
Test the full procedure of the digital signature, including generate key pairs, compute the signature as well as verify the signature.

## Build
```sh
mvn clean verify
```

## Run
```sh
java -jar target/benchmarks.jar
```

## Result
The result clearly shows that `secp256r1` has a better performance than `secp256k1` with regard to SHA256withECDSA for digital signature.

```sh
# JMH version: 1.35
# VM version: JDK 11.0.16, Java HotSpot(TM) 64-Bit Server VM, 11.0.16+11-LTS-199
# VM invoker: C:\Program Files\Java\jdk-11.0.16\bin\java.exe
# VM options: -Dfile.encoding=UTF-8
# Blackhole mode: full + dont-inline hint (auto-detected, use -Djmh.blackhole.autoDetect=false to disable)
# Warmup: 5 iterations, 10 s each
# Measurement: 5 iterations, 10 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Throughput, ops/time
# Benchmark: org.sample.MyBenchmark.secp256k1

# Run progress: 0.00% complete, ETA 00:16:40
# Fork: 1 of 5
Picked up JAVA_TOOL_OPTIONS: -Dfile.encoding=UTF-8
# Warmup Iteration   1: 264.880 ops/s
# Warmup Iteration   2: 285.105 ops/s
# Warmup Iteration   3: 284.701 ops/s
# Warmup Iteration   4: 286.362 ops/s
# Warmup Iteration   5: 285.004 ops/s
Iteration   1: 285.189 ops/s
Iteration   2: 286.686 ops/s
Iteration   3: 286.396 ops/s
Iteration   4: 286.599 ops/s
Iteration   5: 286.589 ops/s

# Run progress: 10.00% complete, ETA 00:15:04
# Fork: 2 of 5
Picked up JAVA_TOOL_OPTIONS: -Dfile.encoding=UTF-8
# Warmup Iteration   1: 264.085 ops/s
# Warmup Iteration   2: 285.140 ops/s
# Warmup Iteration   3: 286.148 ops/s
# Warmup Iteration   4: 286.254 ops/s
# Warmup Iteration   5: 285.612 ops/s
Iteration   1: 285.302 ops/s
Iteration   2: 284.810 ops/s
Iteration   3: 284.958 ops/s
Iteration   4: 284.849 ops/s
Iteration   5: 279.722 ops/s

# Run progress: 20.00% complete, ETA 00:13:23
# Fork: 3 of 5
Picked up JAVA_TOOL_OPTIONS: -Dfile.encoding=UTF-8
# Warmup Iteration   1: 263.110 ops/s
# Warmup Iteration   2: 284.632 ops/s
# Warmup Iteration   3: 285.619 ops/s
# Warmup Iteration   4: 282.706 ops/s
# Warmup Iteration   5: 279.186 ops/s
Iteration   1: 285.300 ops/s
Iteration   2: 282.346 ops/s
Iteration   3: 267.173 ops/s
Iteration   4: 283.085 ops/s
Iteration   5: 283.135 ops/s

# Run progress: 30.00% complete, ETA 00:11:43
# Fork: 4 of 5
Picked up JAVA_TOOL_OPTIONS: -Dfile.encoding=UTF-8
# Warmup Iteration   1: 230.816 ops/s
# Warmup Iteration   2: 271.800 ops/s
# Warmup Iteration   3: 284.963 ops/s
# Warmup Iteration   4: 286.018 ops/s
# Warmup Iteration   5: 285.814 ops/s
Iteration   1: 286.486 ops/s
Iteration   2: 286.293 ops/s
Iteration   3: 286.229 ops/s
Iteration   4: 285.421 ops/s
Iteration   5: 285.857 ops/s

# Run progress: 40.00% complete, ETA 00:10:02
# Fork: 5 of 5
Picked up JAVA_TOOL_OPTIONS: -Dfile.encoding=UTF-8
# Warmup Iteration   1: 265.042 ops/s
# Warmup Iteration   2: 285.256 ops/s
# Warmup Iteration   3: 285.710 ops/s
# Warmup Iteration   4: 285.905 ops/s
# Warmup Iteration   5: 285.127 ops/s
Iteration   1: 285.580 ops/s
Iteration   2: 286.689 ops/s
Iteration   3: 286.430 ops/s
Iteration   4: 286.423 ops/s
Iteration   5: 286.850 ops/s


Result "org.sample.MyBenchmark.secp256k1":
  284.576 ±(99.9%) 2.990 ops/s [Average]
  (min, avg, max) = (267.173, 284.576, 286.850), stdev = 3.992
  CI (99.9%): [281.585, 287.566] (assumes normal distribution)


# JMH version: 1.35
# VM version: JDK 11.0.16, Java HotSpot(TM) 64-Bit Server VM, 11.0.16+11-LTS-199
# VM invoker: C:\Program Files\Java\jdk-11.0.16\bin\java.exe
# VM options: -Dfile.encoding=UTF-8
# Blackhole mode: full + dont-inline hint (auto-detected, use -Djmh.blackhole.autoDetect=false to disable)
# Warmup: 5 iterations, 10 s each
# Measurement: 5 iterations, 10 s each
# Timeout: 10 min per iteration
# Threads: 1 thread, will synchronize iterations
# Benchmark mode: Throughput, ops/time
# Benchmark: org.sample.MyBenchmark.secp256r1

# Run progress: 50.00% complete, ETA 00:08:22
# Fork: 1 of 5
Picked up JAVA_TOOL_OPTIONS: -Dfile.encoding=UTF-8
# Warmup Iteration   1: 254.822 ops/s
# Warmup Iteration   2: 327.003 ops/s
# Warmup Iteration   3: 324.648 ops/s
# Warmup Iteration   4: 321.408 ops/s
# Warmup Iteration   5: 326.883 ops/s
Iteration   1: 329.710 ops/s
Iteration   2: 329.562 ops/s
Iteration   3: 329.357 ops/s
Iteration   4: 329.426 ops/s
Iteration   5: 316.155 ops/s

# Run progress: 60.00% complete, ETA 00:06:41
# Fork: 2 of 5
Picked up JAVA_TOOL_OPTIONS: -Dfile.encoding=UTF-8
# Warmup Iteration   1: 260.575 ops/s
# Warmup Iteration   2: 259.679 ops/s
# Warmup Iteration   3: 310.215 ops/s
# Warmup Iteration   4: 315.397 ops/s
# Warmup Iteration   5: 220.158 ops/s
Iteration   1: 221.907 ops/s
Iteration   2: 223.160 ops/s
Iteration   3: 222.180 ops/s
Iteration   4: 220.527 ops/s
Iteration   5: 218.131 ops/s

# Run progress: 70.00% complete, ETA 00:05:01
# Fork: 3 of 5
Picked up JAVA_TOOL_OPTIONS: -Dfile.encoding=UTF-8
# Warmup Iteration   1: 298.597 ops/s
# Warmup Iteration   2: 326.998 ops/s
# Warmup Iteration   3: 327.791 ops/s
# Warmup Iteration   4: 329.343 ops/s
# Warmup Iteration   5: 328.899 ops/s
Iteration   1: 329.215 ops/s
Iteration   2: 329.676 ops/s
Iteration   3: 329.549 ops/s
Iteration   4: 329.621 ops/s
Iteration   5: 329.215 ops/s

# Run progress: 80.00% complete, ETA 00:03:20
# Fork: 4 of 5
Picked up JAVA_TOOL_OPTIONS: -Dfile.encoding=UTF-8
# Warmup Iteration   1: 254.832 ops/s
# Warmup Iteration   2: 293.049 ops/s
# Warmup Iteration   3: 329.208 ops/s
# Warmup Iteration   4: 329.843 ops/s
# Warmup Iteration   5: 330.163 ops/s
Iteration   1: 330.315 ops/s
Iteration   2: 330.184 ops/s
Iteration   3: 330.213 ops/s
Iteration   4: 330.284 ops/s
Iteration   5: 330.606 ops/s

# Run progress: 90.00% complete, ETA 00:01:40
# Fork: 5 of 5
Picked up JAVA_TOOL_OPTIONS: -Dfile.encoding=UTF-8
# Warmup Iteration   1: 298.795 ops/s
# Warmup Iteration   2: 328.702 ops/s
# Warmup Iteration   3: 329.542 ops/s
# Warmup Iteration   4: 311.867 ops/s
# Warmup Iteration   5: 329.636 ops/s
Iteration   1: 330.380 ops/s
Iteration   2: 330.327 ops/s
Iteration   3: 330.324 ops/s
Iteration   4: 330.098 ops/s
Iteration   5: 330.120 ops/s


Result "org.sample.MyBenchmark.secp256r1":
  307.610 ±(99.9%) 33.109 ops/s [Average]
  (min, avg, max) = (218.131, 307.610, 330.606), stdev = 44.199
  CI (99.9%): [274.501, 340.718] (assumes normal distribution)


# Run complete. Total time: 00:16:44

REMEMBER: The numbers below are just data. To gain reusable insights, you need to follow up on
why the numbers are the way they are. Use profilers (see -prof, -lprof), design factorial
experiments, perform baseline and negative tests that provide experimental control, make sure
the benchmarking environment is safe on JVM/OS/HW level, ask for reviews from the domain experts.
Do not assume the numbers tell you what you want them to tell.

Benchmark               Mode  Cnt    Score    Error  Units
MyBenchmark.secp256k1  thrpt   25  284.576 ±  2.990  ops/s
MyBenchmark.secp256r1  thrpt   25  307.610 ± 33.109  ops/s
```
