package ru.javaops.masterjava.matrix;

import com.google.common.io.Resources;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import ru.javaops.masterjava.MainXml;
import ru.javaops.masterjava.xml.schema.User;
import ru.javaops.masterjava.xml.util.JaxbParser;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Warmup(iterations = 10)
@Measurement(iterations = 10)
@BenchmarkMode({Mode.SingleShotTime})
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Threads(1)
@Fork(10)
@Timeout(time = 5, timeUnit = TimeUnit.MINUTES)
public class MatrixBenchmark {

    // Matrix size
    private static final int MATRIX_SIZE = 1000;

    @Param({"3", "4", "10"})
    private int threadNumber;

    private static int[][] matrixA;
    private static int[][] matrixB;

    @Setup
    public void setUp() {
        matrixA = MatrixUtil.create(MATRIX_SIZE);
        matrixB = MatrixUtil.create(MATRIX_SIZE);
    }

    private ExecutorService executor;
    private JaxbParser jaxbParser;
    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(MatrixBenchmark.class.getSimpleName())
                .threads(1)
                .forks(10)
                .timeout(TimeValue.minutes(5))
                .build();
        new Runner(options).run();
    }
    @Benchmark
    public List<User> jaxbRealization () throws Exception{
        InputStream is = Resources.getResource("payload.xml").openStream();
        return MainXml.jaxbRealization("masterJava", is);
    }
    //    @Benchmark
    public int[][] singleThreadMultiplyOpt() throws Exception {
        return MatrixUtil.singleThreadMultiplyOpt(matrixA, matrixB);
    }

    //    @Benchmark
    public int[][] singleThreadMultiplyOpt2() throws Exception {
        return MatrixUtil.singleThreadMultiplyOpt(matrixA, matrixB);
    }

   //@Benchmark
    public int[][] concurrentMultiplyStreams() throws Exception {
        return MatrixUtil.concurrentMultiplyStreams(matrixA, matrixB, threadNumber);
    }

    //@Benchmark
    public int[][] concurrentMultiply() throws Exception {
        return MatrixUtil.concurrentMultiply(matrixA, matrixB, executor);
    }

    //@Benchmark
    public int[][] concurrentMultiply2() throws Exception {
        return MatrixUtil.concurrentMultiply2(matrixA, matrixB, executor);
    }

    @Setup
    public void setup() {
        executor = Executors.newFixedThreadPool(threadNumber);
    }

    @TearDown
    public void tearDown() {
        executor.shutdown();
    }
}