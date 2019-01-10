package com.liveramp.hyperminhash;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.apache.commons.codec.binary.Hex;
import org.junit.Assert;

class MinHashTestSuite {

  private static final String LETTER_BYTES = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

  private static double getError(long result, long expected) {
    if (result == expected) {
      return 0;
    }

    if (expected == 0) {
      return result;
    }

    long delta = Math.abs(result - expected);
    return delta / (double) expected;
  }

  static <Sketch extends IntersectionSketch<Sketch>> void testCardinality(
      final Sketch sk,
      final int maxUniqueElements,
      final int minTestCardinality,
      final Random random,
      final double maxPctErr) {
    final Map<String, Boolean> unique = new HashMap<>();
    int assertionCheckpoint = minTestCardinality;
    while (unique.size() < maxUniqueElements) {
      String str = randomString(random);
      sk.offer(str.getBytes());
      unique.put(str, true);

      if (unique.size() % assertionCheckpoint == 0) {
        long exact = unique.size();
        long res = sk.cardinality();
        assertionCheckpoint *= 10;

        double pctError = 100 * getError(res, exact);
        Assert.assertTrue(
            String.format("Got %f%% error, but expected at most %f%%", pctError, maxPctErr),
            pctError <= maxPctErr);
      }
    }

  }

  static <Sketch extends IntersectionSketch<Sketch>> void testUnion(
      final Sketch sk1,
      final Sketch sk2,
      final SketchCombiner<Sketch> combiner,
      final int elementsPerSketch,
      final double maxPctError,
      final Random random) {
    final Set<String> unique = new HashSet<>(2 * elementsPerSketch);

    for (int i = 1; i <= elementsPerSketch; i++) {
      String str = randomString(random);
      sk1.offer(str.getBytes());
      unique.add(str);

      str = randomString(random);
      sk2.offer(str.getBytes());
      unique.add(str);
    }

    final Sketch msk = combiner.union(Arrays.asList(sk1, sk2));
    final long exact = unique.size();
    final long res = msk.cardinality();

    final double pctError = 100 * getError(res, exact);
    Assert.assertTrue(
        String.format("Got %f%% error, but expected at most %f%%", pctError, maxPctError),
        pctError <= maxPctError
    );
  }

  static <Sketch extends IntersectionSketch<Sketch>> void testIntersection(
      final Sketch emptySketch,
      final SketchCombiner<Sketch> combiner,
      final int overlapSlices,
      final int elementsInLeftSketches,
      final double maxPctError) {
    for (int j = 1; j < overlapSlices; j++) {

      final Sketch sk1 = emptySketch.deepCopy();
      final Sketch sk2 = emptySketch.deepCopy();

      final double frac = j / (double) overlapSlices;

      for (int i = 0; i < elementsInLeftSketches; i++) {
        final String str = i + "";
        sk1.offer(str.getBytes());
      }

      for (int i = (int) (frac * elementsInLeftSketches); i < 2 * elementsInLeftSketches; i++) {
        final String str = i + "";
        sk2.offer(str.getBytes());
      }

      final long expected = (long) (elementsInLeftSketches - elementsInLeftSketches * frac);
      final long result = combiner.intersectionCardinality(Lists.newArrayList(sk1, sk2));

      final double pctError = 100 * getError(result, expected);
      Assert.assertTrue(
          String.format(
              "Expected error ratio to be at most %s but found %f",
              maxPctError,
              pctError),
          pctError <= maxPctError);
    }
  }

  static <Sketch extends IntersectionSketch<Sketch>> void testIntersectLargeSetWithSmall(
      final Sketch emptySketch,
      final SketchCombiner<Sketch> combiner,
      final int smallSetSize,
      final int bigSetSize,
      final double maxPctErr) {

    final Sketch smallSketch = emptySketch.deepCopy();
    for (int i = 1; i < smallSetSize; i++) {
      smallSketch.offer((i + "").getBytes());
    }

    final Sketch bigSketch = emptySketch.deepCopy();
    for (long i = 1; i <= bigSetSize; i++) {
      bigSketch.offer((i + "").getBytes());
    }

    final double expectedJaccardIndex = smallSetSize / (double) bigSetSize;
    final long expectedIntersection = (long) (expectedJaccardIndex * bigSetSize);
    final long actualIntersection = combiner.intersectionCardinality(
        Arrays.asList(smallSketch, bigSketch)
    );
    final double pctError = 100 * getError(actualIntersection, expectedIntersection);

    // HyperMinHash performance starts decreasing as jaccard index becomes < 1%. On a Jaccard index this small
    // we should hope for <100% error.
    Assert.assertTrue(
        String.format(
            "Percent error for a small jaccard index (%s) should be less than 100, but found %f",
            expectedJaccardIndex, pctError),
        pctError < maxPctErr
    );
  }

  static <Sketch extends IntersectionSketch<Sketch>> void testMultiwayIntersection(
      final Sketch emptySketch,
      final SketchCombiner<Sketch> combiner,
      final int initialSketchSize,
      final int initialIntersectionSize,
      final int numIter) {

    long sketchSize = initialSketchSize;
    long intersectionSize = initialIntersectionSize;
    // union size gets bigger, jaccard gets smaller
    for (int i = 0; i < numIter; i++) {
      Sketch sk1 = emptySketch.deepCopy();
      Sketch sk2 = emptySketch.deepCopy();
      Sketch sk3 = emptySketch.deepCopy();
      Sketch sk4 = emptySketch.deepCopy();

      buildIntersectingSketches(sketchSize, intersectionSize, sk1, sk2, sk3, sk4);

      long expectedIntersection = initialIntersectionSize;
      long actualIntersection = combiner.intersectionCardinality(Arrays.asList(sk1, sk2, sk3));
      double pctError = 100 * getError(actualIntersection, expectedIntersection);
      Assert.assertTrue(
          String.format("Expected pctError to be <2, found %f. Expected: %d, Actual: %d", pctError,
              expectedIntersection, actualIntersection),
          pctError <= 5
      );

      intersectionSize <<= 1;
      sketchSize <<= 1;
    }
  }

  // builds equally sized sketches which share numSharedElements items
  private static <Sketch extends IntersectionSketch<Sketch>> void buildIntersectingSketches(
      long sketchSize,
      long numSharedElements,
      Sketch... sketches) {
    assert sketchSize >= numSharedElements;

    final long numIter = ((sketchSize - numSharedElements) * sketches.length + numSharedElements);
    for (int i = 0; i < numIter; i++) {
      byte[] val = (i + "").getBytes();
      if (i < numSharedElements) {
        for (Sketch sketch : sketches) {
          sketch.offer(val);
        }
      } else {
        sketches[i % sketches.length].offer(val);
      }
    }
  }

  private static String randomString(final Random random) {
    final int n = Math.abs(random.nextInt()) % 32;
    byte[] b = new byte[n];
    for (int i = 0; i < n; i++) {
      b[i] = (byte) LETTER_BYTES.charAt(randPositiveInt(random) % LETTER_BYTES.length());
    }
    return Hex.encodeHexString(b);
  }

  private static int randPositiveInt(final Random random) {
    return Math.abs(random.nextInt());
  }
}
