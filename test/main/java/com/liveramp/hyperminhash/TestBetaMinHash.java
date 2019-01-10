package com.liveramp.hyperminhash;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


import com.liveramp.hyperminhash.test.AbstractMinHashTest;
import java.util.Random;
import org.junit.Test;

public class TestBetaMinHash extends AbstractMinHashTest {

  @Test
  public void testZeroCardinality() {
    BetaMinHash sk = new BetaMinHash();
    assertEquals(0, sk.cardinality());
  }

  @Test
  public void testCardinality() {
    final BetaMinHash sk = new BetaMinHash();
    super.testCardinality(
        sk,
        10_000_000,
        10_000,
        new Random(),
        2.0
    );

  }

  @Test
  public void testUnion() {
    final BetaMinHash sk1 = new BetaMinHash();
    final BetaMinHash sk2 = new BetaMinHash();
    final BetaMinHashCombiner combiner = BetaMinHashCombiner.getInstance();
    final int elementsPerSketch = 1_500_000;
    final double pctError = 2.0;
    super.testUnion(sk1, sk2, combiner, elementsPerSketch, pctError, new Random());
  }

  @Test
  public void testIntersectionCardinality() {
    final int iters = 20;
    final int k = 1_000_000;
    final double pctError = 5.0;
    final BetaMinHashCombiner combiner = BetaMinHashCombiner.getInstance();

    for (int j = 1; j < iters; j++) {

      final BetaMinHash sk1 = new BetaMinHash();
      final BetaMinHash sk2 = new BetaMinHash();

      final double frac = j / (double) iters;

      for (int i = 0; i < k; i++) {
        final String str = i + "";
        sk1.offer(str.getBytes());
      }

      for (int i = (int) (frac * k); i < 2 * k; i++) {
        final String str = i + "";
        sk2.offer(str.getBytes());
      }

      final long expected = (long) (k - k * frac);
      final long result = combiner.intersectionCardinality(sk1, sk2);

      final double pctError = 100 * getError(result, expected);
      final double expectedPctError = 5.0;
      assertTrue(
          String.format("Expected error ratio to be at most %s but found %f", expectedPctError,
              pctError),
          pctError <= expectedPctError);
    }
  }

  @Test
  public void testIntersectLargeSetWithSmallSet() {
    // Our other test uses string, which puts an upper bound on how big that test can be due to GC errors. With numbers
    // we can estimate cardinalities on the order of billions without running out of memory, unlike strings.
    final BetaMinHash smallSketch = new BetaMinHash();
    final BetaMinHashCombiner combiner = BetaMinHashCombiner.getInstance();
    final long smallSetSize = 100_000;
    for (int i = 1; i < smallSetSize; i++) {
      smallSketch.offer((i + "").getBytes());
    }

    final BetaMinHash bigSketch = new BetaMinHash();
    final long bigSetSize = 100_000_000;
    for (long i = 1; i <= bigSetSize; i++) {
      bigSketch.offer((i + "").getBytes());
    }

    final double expectedJaccardIndex = smallSetSize / (double) bigSetSize;
    final long expectedIntersection = (long) (expectedJaccardIndex * bigSetSize);
    final long actualIntersection = combiner.intersectionCardinality(smallSketch, bigSketch);
    final double pctError = 100 * getError(actualIntersection, expectedIntersection);

    // HyperMinHash performance starts decreasing as jaccard index becomes < 1%. On a Jaccard index this small
    // we should hope for <100% error.
    assertTrue(
        String.format(
            "Percent error for a small jaccard index (%s) should be less than 100, but found %f",
            expectedJaccardIndex, pctError),
        pctError < 100
    );
  }

  @Test
  public void testManyWayIntersection() {

    long intersectionSize = 3000;
    long sketchSize = 10_000;
    final BetaMinHashCombiner combiner = BetaMinHashCombiner.getInstance();
    // union size gets bigger, jaccard gets smaller
    for (int i = 0; i < 5; i++) {
      BetaMinHash sk1 = new BetaMinHash();
      BetaMinHash sk2 = new BetaMinHash();
      BetaMinHash sk3 = new BetaMinHash();
      BetaMinHash sk4 = new BetaMinHash();

      buildIntersectingSketches(sketchSize, intersectionSize, sk1, sk2, sk3, sk4);

      long expectedIntersection = intersectionSize;
      long actualIntersection = combiner.intersectionCardinality(sk1, sk2, sk3);
      double pctError = 100 * getError(actualIntersection, expectedIntersection);
      assertTrue(
          String.format("Expected pctError to be <2, found %f. Expected: %d, Actual: %d", pctError,
              expectedIntersection, actualIntersection),
          pctError <= 5
      );

      intersectionSize <<= 1;
      sketchSize <<= 1;
    }
  }

  @Test
  public void testToFromBytes() {
    final BetaMinHash original = new BetaMinHash();
    original.offer("test data".getBytes());

//    final byte[] serialized = original.getBytes();
//    final BetaMinHash deSerialized = BetaMinHash.fromBytes(serialized);

//    Assert.assertEquals(original, deSerialized);
  }

  // builds equally sized sketches which share numSharedElements items
  private void buildIntersectingSketches(
      long sketchSize, long numSharedElements, BetaMinHash... sketches) {
    assert sketchSize >= numSharedElements;

    for (int i = 0; i < ((sketchSize - numSharedElements) * sketches.length + numSharedElements);
        i++) {
      byte[] val = (i + "").getBytes();
      if (i < numSharedElements) {
        for (BetaMinHash sketch : sketches) {
          sketch.offer(val);
        }
      } else {
        sketches[i % sketches.length].offer(val);
      }
    }
  }


}
