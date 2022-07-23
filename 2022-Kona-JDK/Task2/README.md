# Performance test for digital signature comparing between secp256r1 and secp256k1 using SHA256withECDSA
Use JDK 11.

Test full procedure of the digital signature, including computing the signature as well as verifying the signature.

## Build
```sh
mvn clean verify
```

## Run
```sh
java -jar target/benchmarks.jar
```

## Result
[result logs](result.log)

| Actions | secp256r1 | secp256k1 |
| ------- | --------- | --------- |
| Signing 128B | 1771.610 | 1256.410 |
| Signing 256B | 1773.989 | 1265.490 |
| Signing 1024B | 1765.495 | 1228.140 |
| Signing 1024K | 347.348 | 320.757 |
| Verifying 128B | 478.608 | 498.351 |
| Verifying 256B | 508.961 | 503.602 |
| Verifying 1024B | 505.910 | 502.697 |
| Verifying 1024K | 236.486 | 232.933 |

The result clearly shows that `secp256r1` has a better performance than `secp256k1` with regard to SHA256withECDSA when signing. But `secp256r1` has almost the same performance as `secp256k1` when verifying.
