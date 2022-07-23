package org.sample;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.security.*;
import java.security.spec.*;

public class MyBenchmark {
    void ecc(String curve) throws Exception {
        // Create a public and private key for specified curve name.
        KeyPairGenerator g = KeyPairGenerator.getInstance("EC", "SunEC");
        ECGenParameterSpec ecsp = new ECGenParameterSpec(curve);
        g.initialize(ecsp);

        KeyPair kp = g.genKeyPair();
        PrivateKey privKey = kp.getPrivate();
        PublicKey pubKey = kp.getPublic();

        // Select the signature algorithm.
        Signature s = Signature.getInstance("SHA256withECDSA", "SunEC");
        s.initSign(privKey);

        // Compute the signature.
        String newLine = System.getProperty("line.separator");
        String text = " * Copyright (c) 2014, Oracle America, Inc."
                .concat(newLine)
                .concat(" * All rights reserved.")
                .concat(newLine)
                .concat(" *")
                .concat(newLine)
                .concat(" * Redistribution and use in source and binary forms, with or without")
                .concat(newLine)
                .concat(" * modification, are permitted provided that the following conditions are met:")
                .concat(newLine)
                .concat(" *")
                .concat(newLine)
                .concat(" *  * Redistributions of source code must retain the above copyright notice,")
                .concat(newLine)
                .concat(" *    this list of conditions and the following disclaimer.")
                .concat(newLine)
                .concat(" *")
                .concat(newLine)
                .concat(" *  * Redistributions in binary form must reproduce the above copyright")
                .concat(newLine)
                .concat(" *    notice, this list of conditions and the following disclaimer in the")
                .concat(newLine)
                .concat(" *    documentation and/or other materials provided with the distribution.")
                .concat(newLine)
                .concat(" *")
                .concat(newLine)
                .concat(" *  * Neither the name of Oracle nor the names of its contributors may be used")
                .concat(newLine)
                .concat(" *    to endorse or promote products derived from this software without")
                .concat(newLine)
                .concat(" *    specific prior written permission.")
                .concat(newLine)
                .concat(" *")
                .concat(newLine)
                .concat(" * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS 'AS IS'")
                .concat(newLine)
                .concat(" * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE")
                .concat(newLine)
                .concat(" * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE")
                .concat(newLine)
                .concat(" * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE")
                .concat(newLine)
                .concat(" * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR")
                .concat(newLine)
                .concat(" * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF")
                .concat(newLine)
                .concat(" * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS")
                .concat(newLine)
                .concat(" * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN")
                .concat(newLine)
                .concat(" * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)")
                .concat(newLine)
                .concat(" * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF")
                .concat(newLine)
                .concat(" * THE POSSIBILITY OF SUCH DAMAGE.")
                .concat(newLine);
        byte[] msg = text.getBytes("UTF-8");
        byte[] sig;
        s.update(msg);
        sig = s.sign();

        // Verify the signature.
        Signature sg = Signature.getInstance("SHA256withECDSA", "SunEC");
        sg.initVerify(pubKey);
        sg.update(msg);
        boolean validSignature = sg.verify(sig);

        if (!validSignature) {
            throw new RuntimeException("Invalid signature");
        }
    }

    /*
     * This is our first benchmark method.
     *
     * JMH works as follows: users annotate the methods with @Benchmark, and
     * then JMH produces the generated code to run this particular benchmark as
     * reliably as possible. In general one might think about @Benchmark methods
     * as the benchmark "payload", the things we want to measure. The
     * surrounding infrastructure is provided by the harness itself.
     *
     * Read the Javadoc for @Benchmark annotation for complete semantics and
     * restrictions. At this point we only note that the methods names are
     * non-essential, and it only matters that the methods are marked with
     * @Benchmark. You can have multiple benchmark methods within the same
     * class.
     *
     * Note: if the benchmark method never finishes, then JMH run never finishes
     * as well. If you throw an exception from the method body the JMH run ends
     * abruptly for this benchmark and JMH will run the next benchmark down the
     * list.
     *
     * Although this benchmark measures "nothing" it is a good showcase for the
     * overheads the infrastructure bear on the code you measure in the method.
     * There are no magical infrastructures which incur no overhead, and it is
     * important to know what are the infra overheads you are dealing with. You
     * might find this thought unfolded in future examples by having the
     * "baseline" measurements to compare against.
     */

    @Benchmark
    public void secp256r1() throws Exception {
        ecc("secp256r1");
    }

    @Benchmark
    public void secp256k1() throws Exception {
        ecc("secp256k1");
    }

    /*
     * ============================== HOW TO RUN THIS TEST: ====================================
     *
     * You are expected to see the run with large number of iterations, and
     * very large throughput numbers. You can see that as the estimate of the
     * harness overheads per method call. In most of our measurements, it is
     * down to several cycles per call.
     *
     * a) Via command-line:
     *    $ mvn clean verify
     *    $ java -jar target/benchmarks.jar
     *
     * JMH generates self-contained JARs, bundling JMH together with it.
     * The runtime options for the JMH are available with "-h":
     *    $ java -jar target/benchmarks.jar -h
     *
     * b) Via the Java API:
     *    (see the JMH homepage for possible caveats when running from IDE:
     *      http://openjdk.java.net/projects/code-tools/jmh/)
     */

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(MyBenchmark.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
