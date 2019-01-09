package com.liveramp.hyperminhash;

class HmhCardinalityEstimator {

  /**
   * @return a estimate of the cardinality of the elements represented by the HyperMinHash packed
   *     registers by determining the number of leading zeroes of hash represented by each packed
   *     register, and using HLL-based estimation from there.
   */
  long estimateCardinality(long[] packedRegisters, int q, int r) {

  }

  private long defaultHllEstimate(long[] packedRegisters, int q, int r) {

  }

  private long linearCountingEstimate(long[] packedRegisters, int q, int r) {

  }

  private long getLeadingZeroes(long packedRegister, int q, int r){
    long numLeadingZeroesQbits = packedRegister >>> r;
    if (numLeadingZeroesQbits == 0) {

    }
  }

  private static double alpha(int p) {
    // From the HLL paper
    final int m = 1 << p;
    if (p > 6) {
      return 0.7213 / (1 + (1.079 / m));
    }

    switch (p) {
      case 4:
        return 0.673;
      case 5:
        return 0.697;
      case 6:
        return 0.709;
      default:
        throw new IllegalArgumentException("Values of p under 4 are not supported");
    }
  }
}
