package com.liveramp.hyperminhash.test;

import com.liveramp.hyperminhash.IntersectionSketch;
import com.liveramp.hyperminhash.SketchCombiner;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.apache.commons.codec.binary.Hex;
import org.junit.Assert;

public abstract class AbstractMinHashTest {

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

  protected <Sketch extends IntersectionSketch<Sketch>> void testCardinality(
      final Sketch sk,
      final int maxUniqueElements,
      final int minTestCardinality,
      final Random random,
      final double maxPctErr) {
    final Map<String, Boolean> unique = new HashMap<>();
    int assertionCheckpoint = minTestCardinality;
    while (unique.size() < maxUniqueElements) {
      String str = randomStringWithLength(random);
      sk.offer(str.getBytes());
      unique.put(str, true);

      if (unique.size() % assertionCheckpoint == 0) {
        long exact = unique.size();
        long res = sk.cardinality();
        assertionCheckpoint *= 10;

        double pctError = 100 * getError(res, exact);
        Assert.assertTrue(pctError <= maxPctErr);
      }
    }

  }

  protected <Sketch extends IntersectionSketch<Sketch>> void testUnion(
      final Sketch sk1,
      final Sketch sk2,
      final SketchCombiner<Sketch> combiner,
      final int elementsPerSketch,
      final double maxPctError,
      final Random random) {
    final Set<String> unique = new HashSet<>(2 * elementsPerSketch);

    for (int i = 1; i <= elementsPerSketch; i++) {
      String str = randomStringWithLength(random);
      sk1.offer(str.getBytes());
      unique.add(str);

      str = randomStringWithLength(random);
      sk2.offer(str.getBytes());
      unique.add(str);
    }

    final Sketch msk = combiner.union(sk1, sk2);
    final long exact = unique.size();
    final long res = msk.cardinality();

    final double pctError = 100 * getError(res, exact);
    Assert.assertTrue(pctError <= maxPctError);
  }

  protected <Sketch extends IntersectionSketch<Sketch>> void testIntersection(){

  }

  private String randomStringWithLength(final Random random) {
    final int n = Math.abs(random.nextInt()) % 32;
    byte[] b = new byte[n];
    for (int i = 0; i < n; i++) {
      b[i] = (byte) LETTER_BYTES.charAt(randPositiveInt(random) % LETTER_BYTES.length());
    }
    return Hex.encodeHexString(b);
  }


  private int randPositiveInt(final Random random) {
    return Math.abs(random.nextInt());
  }
}
