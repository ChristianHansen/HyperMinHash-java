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

  private long linearCountingEstimate(long[] packedRegisters, int q, int r){

  }

  private static double alpha(int p) {
    final int numRegisters = 1 << p;
    if ( p > 6 ) {
      return 0.7213 / (1 + (1.079 )
    }
  }
}
