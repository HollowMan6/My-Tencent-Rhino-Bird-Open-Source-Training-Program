# Kona JDK - Tencent Rhino-bird Open-source Training Program 2022
[Issue](https://github.com/Tencent/OpenSourceTalent/issues/34)

## Task 1
[Requirement](https://docs.qq.com/doc/DUVpWUUVpVVVySVNw)

https://github.com/openjdk/jdk/pull/9541

## Task 2
[Requirement](https://docs.qq.com/doc/DUXhGSXBHZG11eUJ0)

[Result](Task2/README.md)

Tested computing the signature as well as verifying the signature for comparing between `secp256r1` and `secp256k1` using SHA256withECDSA with the help of the SunEC provider.

The result clearly shows that `secp256r1` has a better performance than `secp256k1` with regard to SHA256withECDSA when signing. But `secp256r1` has almost the same performance as `secp256k1` when verifying.

```log
Benchmark                                 Mode  Cnt     Score    Error  Units
BenchmarkSigning.secp256k1_1024B         thrpt   25  1237.482 ± 30.518  ops/s
BenchmarkSigning.secp256k1_1024K         thrpt   25   318.589 ±  2.656  ops/s
BenchmarkSigning.secp256k1_128B          thrpt   25  1266.561 ± 32.311  ops/s
BenchmarkSigning.secp256k1_256B          thrpt   25  1254.327 ± 36.935  ops/s
BenchmarkSigning.secp256r1_1024B         thrpt   25  1746.453 ± 33.358  ops/s
BenchmarkSigning.secp256r1_1024K         thrpt   25   340.530 ±  3.970  ops/s
BenchmarkSigning.secp256r1_128B          thrpt   25  1763.460 ± 31.179  ops/s
BenchmarkSigning.secp256r1_256B          thrpt   25  1756.899 ± 32.715  ops/s
BenchmarkVerifying.secp256k1_1024B       thrpt   25   486.545 ± 13.410  ops/s
BenchmarkVerifying.secp256k1_1024K       thrpt   25   228.638 ±  2.478  ops/s
BenchmarkVerifying.secp256k1_128B        thrpt   25   491.065 ± 13.948  ops/s
BenchmarkVerifying.secp256k1_256B        thrpt   25   499.466 ±  4.558  ops/s
BenchmarkVerifying.secp256r1_1024B       thrpt   25   402.902 ± 53.489  ops/s
BenchmarkVerifying.secp256r1_1024K       thrpt   25   212.743 ± 23.301  ops/s
BenchmarkVerifying.secp256r1_128B        thrpt   25   401.215 ± 65.401  ops/s
BenchmarkVerifying.secp256r1_256B        thrpt   25   393.021 ± 79.755  ops/s
```

Further investigation shows that before `secp256k1` was removed from JDK, all the curves seem to be realized by C using the OS library instead of Java. https://github.com/openjdk/jdk/blob/jdk-11+28/src/jdk.crypto.ec/share/native/libsunec/impl/oid.c#L95

[JDK-8238911](https://bugs.openjdk.org/browse/JDK-8238911) in 2020 reported the weaknesses in the implementation of the native library EC code make it necessary to remove support for future releases. The most common EC curves (`secp256r1`, `secp384r1`, and `secp521r1`) had been re-implemented in Java in the SunEC JCE provider.

After some communications with my mentor Johns Jiang, he tells me that [JDK-8181594](https://bugs.openjdk.org/browse/JDK-8181594) introduces the optimized finite field implementations in Java. Previously before that implementation was introduced, pure Java realization was really slow, then we use the OS library to realize all the curves so that the performance can be improved. But now, instead, with the help of that [optimized Java library](https://mail.openjdk.org/pipermail/core-libs-dev/2018-February/051729.html), Java realization takes the advantage and becomes the most efficient one, it's now even comparable with the pure C realization.

Our flame graph also some kind confirms this, as you can see [here](Task2/README.md#cpu), the Java Flight Recorder (JFR) can record the `secp256r1` methods calling stacks, but it's not the case for `secp256k1`. So it's likely that `secp256r1` has a better performance than `secp256k1` for signing since it's fully realized in Java and using that optimized library, thus reduces the calling costs for the OS library. If they are both realized in Java using the optimized method, I guess there should be no difference.

As `secp256k1` has already been removed in JDK and now `secp256r1` does have a better performance, so I guess here we will have no obvious further room for improvement.

## Task 3
[Requirement](https://docs.qq.com/doc/DUVhpTE9HcVJmZFNF)

[Result](Task3/README.md)

As for Elliptic-curve based cryptography algorithms, the curve parameters are used to generate the keys.
The official recommended curve parameters for SM2 can be seen here:
https://www.oscca.gov.cn/sca/xxgk/2010-12/17/1002386/files/b965ce832cc34bc191cb1cde446b860d.pdf

That curve parameters is also known as `sm2p256v1`, since it's also a prime curve just like `secp256r1`, we can use the existing implementation of `secp256r1` in the SunEC library to help us realize our implementation.

The OID for `sm2p256v1` is `1.2.156.10197.1.301`: http://gmssl.org/docs/oid.html

https://github.com/HollowMan6/jdk/tree/sm2
https://github.com/HollowMan6/jdk/commit/c3e924641bb3a838f6abc496dd380ceb619df163

We first fill the curve parameters into the [CurveDB](
https://github.com/HollowMan6/jdk/blob/c3e924641bb3a838f6abc496dd380ceb619df163/src/java.base/share/classes/sun/security/util/CurveDB.java#L258-L265)

Then add the OID and names.

The most important part is FieldGen. `FieldGen` is used to automatically generate optimized finite field implementations, which is also the library I mentioned in Task 2 [JDK-8181594](https://bugs.openjdk.org/browse/JDK-8181594) for improving Java version's efficiency. https://github.com/HollowMan6/jdk/blob/c3e924641bb3a838f6abc496dd380ceb619df163/make/jdk/src/classes/build/tools/intpoly/FieldGen.java We need to generate two fields, `Integer Polynomial` (corresponds to parameter `p`) and `Order Field` (corresponds to parameter `n`).

As:

FFFFFFFE FFFFFFFF FFFFFFFF FFFFFFFF FFFFFFFF 00000000 FFFFFFFF FFFFFFFF

=

FFFFFFFF FFFFFFFF FFFFFFFF FFFFFFFF FFFFFFFF FFFFFFFF FFFFFFFF FFFFFFFF $2^{256} - 1$

\-

00000001 00000000 00000000 00000000 00000000 00000000 00000000 
00000000 $2^{224}$

\-

00000000 00000000 00000000 00000000 00000000 FFFFFFFF FFFFFFFF FFFFFFFF 
$2^{96} - 1$

\+

00000000 00000000 00000000 00000000 00000000 00000000 FFFFFFFF FFFFFFFF

$2^{64} - 1$

= $2^{256} - 2^{224} - 2^{96} + 2^{64} - 2^0$

So the `Integer Polynomial` shall fill just like that. We can copy other parameters from `secp256r1` as they are all 256 digit prime curve.

The private keys in Hex can be printed directly with no special format.

Since the public keys in Hex can be compressed, it does have a special format, that if it starts with `04`, then the keys are uncompressed and we just then concat the X and Y coordinate together. The compressed ones always start with `02` or `03`, and then only the X coordinate is needed. The `02` and `03` is determined by that, when Y coordinate is even, we use `02`, use `03` when odd. In addition, we have to also make sure that both X and Y coordinates are 64 in length for Hex.

As the Bouncy Castle library has already fully realized the SM2, to ensure that the generated keys fit the `sm2p256v1`, I also use the generated keys for signing using SM3withSM2. The validity of the keys can be verified during the signature verification processes, during which we recover the `sm2p256v1` elliptic curve point from the Bouncy Castle library based on the Hex format public key. If we use keys generated based on other curves, like `secp256r1`, error will be thrown.

Demo result:
```sh
Public Key (Uncompressed): 040A8FB35CA4761FAAA36B2A24E77EC657D96F74147C50EE2D5B50E3AAFD8304D8CBB65FB2E661D37B7C3B900E1BDBEDE894D9CBB9079E8DD704B9465BFF65EE17
Public Key: 030A8FB35CA4761FAAA36B2A24E77EC657D96F74147C50EE2D5B50E3AAFD8304D8
Private Key: 42756D22960A58D08F9E7E3A0D56D9630D8D051D082F4D2BFCE22FD2653524EB
To sign: How are you?
Signed: MEUCIAgvYgl0ydHwd536MkmwaRuhmkD/klh79VmHEBJI1zCRAiEAo9jNkGM+Tjh/0AmX82nSPOMYgRPaWm6SUXiB63YGAD4=
Verification OK!
```

Error thrown when using `secp256r1` (faulty key pairs):
```sh
Public Key (Uncompressed): 0456A3205C7B47BF303F4B65CDAB5B94C343BE7220E5AAAB001B7263DBCD113B42447A9E41BF1374D4ABC7A2AE31E7441E3EB20D5808CCB7D88BFE4F8F2C9887C3
Public Key: 0356A3205C7B47BF303F4B65CDAB5B94C343BE7220E5AAAB001B7263DBCD113B42
Private Key: A33048CC39925B5D4BA4C34FE846C85D41DA5AABA0CFDE4092A7A4ED716D557
To sign: How are you?
Signed: MEYCIQCktncEmfLbC9rLC1Im9gj/AvZRUIQ5Z1plrq1X0L/5YwIhAOa0JeSoQFnV51kAJsFRY3T4cpCn2O7bKoN+M+nPpv6y
Exception in thread "main" java.lang.IllegalArgumentException: Invalid point coordinates
        at org.bouncycastle.math.ec.ECCurve.validatePoint(Unknown Source)
        at org.bouncycastle.math.ec.ECCurve.decodePoint(Unknown Source)
        at org.sample.SM2Util.verify(SM2Util.java:103)
        at org.sample.SM2Util.main(SM2Util.java:129)
```

JMH Performance test for compressed and uncompressed public key generation result shows that the compressed public keys generation has a better performance than the uncompressed ones. You can find the reason from the flame graph of Java Flight Recorder (JFR), that it's because the uncompressed ones also need to caculate the Y coordinate Hex format, which takes a lot of time.

```log
Benchmark                                         Mode  Cnt       Score       Error  Units
BenchmarkPublicKeys.sm2p256v1_compressed         thrpt   25  1212201.531 ± 248181.084  ops/s
BenchmarkPublicKeys.sm2p256v1_uncompressed       thrpt   25   760033.805 ±  35058.515  ops/s
```

Our code for generating the sm2p256v1 key pairs using SunEC also has a better performance than the Bouncy Castle.

```log
Benchmark                                         Mode  Cnt       Score       Error  Units
BenchmarkKeyGeneration.sm2p256v1_bc              thrpt   25      470.064 ±     39.347  ops/s
BenchmarkKeyGeneration.sm2p256v1_sunec           thrpt   25     2515.464 ±     45.194  ops/s
```
