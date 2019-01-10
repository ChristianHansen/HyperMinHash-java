package com.liveramp.hyperminhash;

import java.util.Collection;

public class HyperMinHashCombiner implements SketchCombiner<HyperMinHash> {

  @Override
  public HyperMinHash union(Collection<HyperMinHash> sketches) {
    assertInputNotEmpty(sketches);
    assertParamsAreEqual(sketches);
    final HyperMinHash firstSketch = sketches.stream()
        .findFirst()
        .get();
    if (sketches.size() == 1) {
      return firstSketch.deepCopy();
    }

    final int numRegisters = firstSketch.registers.length;
    final HyperMinHash mergedSketch = firstSketch.deepCopy();
    int r = mergedSketch.r;

    for (int i = 0; i < numRegisters; i++) {
      for (HyperMinHash sketch : sketches) {

        if (HyperMinHash.shouldReplace(mergedSketch.registers[i], sketch.registers[i], r)) {
          mergedSketch.registers[i] = sketch.registers[i];
        }
      }
    }

    return mergedSketch;
  }

  @Override
  public long intersectionCardinality(Collection<HyperMinHash> sketches) {
    return (long) (union(sketches).cardinality() * similarity(sketches));
  }

  @Override
  public double similarity(Collection<HyperMinHash> sketches) {
    // TODO we can maybe make an abstract class that exposes a similarityInternal(registers, p,q,r)
    // TODO which is shared between combiners
    // Algorithm 2.1.4 in HyperMinHash paper
    assertInputNotEmpty(sketches);
    assertParamsAreEqual(sketches);

    if (sketches.size() == 1) {
      return 1.0;
    }

    long c = 0;
    long n = 0;
    final HyperMinHash firstSketch = sketches.stream()
        .findFirst()
        .get();
    long numRegisters = firstSketch.registers.length;

    for (int i = 0; i < numRegisters; i++) {
      if (firstSketch.registers[i] != 0) {
        boolean itemInIntersection = true;
        for (HyperMinHash sketch : sketches) {
          itemInIntersection =
              itemInIntersection && firstSketch.registers[i] == sketch.registers[i];
        }

        if (itemInIntersection) {
          c++;
        }
      }

      for (HyperMinHash sketch : sketches) {
        if (sketch.registers[i] != 0) {
          n++;
          break;
        }
      }
    }

    if (c == 0) {
      return 0;
    }

    double[] cardinalities = new double[sketches.size()];
    int i = 0;
    for (HyperMinHash sk : sketches) {
      cardinalities[i++] = sk.cardinality();
    }

    int p = firstSketch.p;
    int numZeroSearchBits = firstSketch.numZeroSearchBits;
    int r = firstSketch.r;
    double numExpectedCollisions = expectedCollision(p, numZeroSearchBits, r, cardinalities);

    if (c < numExpectedCollisions) {
      return 0;
    }

    return (c - numExpectedCollisions) / (double) n;
  }

  // algorithm 2.1.5 in the HyperMinHash paper
  private static double expectedCollision(int p, int q, int r, double... cardinalities) {
    final int _2q = 1 << q;
    final int _2r = 1 << r;

    double x = 0;
    double b1 = 0;
    double b2 = 0;

    for (int i = 1; i <= _2q; i++) {
      for (int j = 1; j <= _2r; j++) {
        if (i != _2q) {
          double den = Math.pow(2, p + r + i);
          b1 = (_2r + j) / den;
          b2 = (_2r + j + 1) / den;
        } else {
          double den = Math.pow(2, p + r + i - 1);
          b1 = j / den;
          b2 = (j + 1) / den;
        }

        double product = 1;
        for (double cardinality : cardinalities) {
          product *= Math.pow(1 - b2, cardinality) - Math.pow(1 - b1, cardinality);
        }

        x += product;
      }
    }
    return x * Math.pow(2, p);
  }

  /**
   * Assumes that the array contains at least one sketch.
   *
   * @param sketches input sketches
   */
  private void assertParamsAreEqual(Collection<HyperMinHash> sketches) {
    final HyperMinHash firstSketch = sketches.stream()
        .findFirst()
        .get();

    int p = firstSketch.p;
    int numZeroSearchBits = firstSketch.numZeroSearchBits;
    int r = firstSketch.r;

    for (HyperMinHash sketch : sketches) {
      if (p != sketch.p || numZeroSearchBits != sketch.numZeroSearchBits || r != sketch.r) {
        throw new IllegalArgumentException("Input sketches have different parameters.");
      }
    }
  }

  private void assertInputNotEmpty(Collection<HyperMinHash> sketches) {
    if (sketches.size() == 0) {
      throw new IllegalArgumentException("Input sketches cannot be empty.");
    }
  }
}
