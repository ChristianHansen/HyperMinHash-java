package com.liveramp.hyperminhash;

import static org.junit.Assert.assertEquals;


import org.junit.Test;

public class TestBetaMinHash {

  @Test
  public void testZeroCardinality() {
    BetaMinHash sk = new BetaMinHash();
    assertEquals(0, sk.cardinality());
  }

  @Test
  public void testCardinality() {
    final BetaMinHash sk = new BetaMinHash();
    final int maxUniqueElements = 10_000_000;
    final int minTestCardinality = 10_000;
    final double pctErr = 2.0;
    RandomTestRunner.runRandomizedTest(
        3,
        (random) -> MinHashTestSuite.testCardinality(
            sk,
            maxUniqueElements,
            minTestCardinality,
            random,
            pctErr)
    );
  }

  @Test
  public void testUnion() {
    final BetaMinHash sk1 = new BetaMinHash();
    final BetaMinHash sk2 = new BetaMinHash();
    final BetaMinHashCombiner combiner = BetaMinHashCombiner.getInstance();
    final int elementsPerSketch = 1_500_000;
    final double pctErr = 2.0;
    RandomTestRunner.runRandomizedTest(
        3,
        (random) -> MinHashTestSuite
            .testUnion(sk1, sk2, combiner, elementsPerSketch, pctErr, random)
    );
  }

  @Test
  public void testIntersectionCardinality() {
    final int overlapSlices = 20;
    final int numElementsLeftSketch = 1_000_000;
    final double pctError = 5.0;
    MinHashTestSuite.testIntersection(
        new BetaMinHash(),
        BetaMinHashCombiner.getInstance(),
        overlapSlices,
        numElementsLeftSketch,
        pctError
    );

  }

  @Test
  public void testIntersectLargeSetWithSmallSet() {
    final int smallSetSize = 100_000;
    final int bigSetSize = 100_000_000;
    final double maxPctErr = 100;
    MinHashTestSuite.testIntersectLargeSetWithSmall(
        new BetaMinHash(),
        BetaMinHashCombiner.getInstance(),
        smallSetSize,
        bigSetSize,
        maxPctErr
    );
  }

  @Test
  public void testMultiwayIntersection() {

    final int initialIntersectionSize = 3000;
    final int initialSketchSize = 10_000;
    final int numIter = 5;
    MinHashTestSuite.testMultiwayIntersection(
        new BetaMinHash(),
        BetaMinHashCombiner.getInstance(),
        initialIntersectionSize,
        initialSketchSize,
        numIter
    );

  }

  @Test
  public void testToFromBytes() {
    final BetaMinHash original = new BetaMinHash();
    original.offer("test data".getBytes());

//    final byte[] serialized = original.getBytes();
//    final BetaMinHash deSerialized = BetaMinHash.fromBytes(serialized);

//    Assert.assertEquals(original, deSerialized);
  }


}
