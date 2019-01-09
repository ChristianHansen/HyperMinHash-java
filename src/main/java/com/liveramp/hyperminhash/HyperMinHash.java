package com.liveramp.hyperminhash;

import org.apache.commons.lang.NotImplementedException;

public class HyperMinHash implements IntersectionSketch {

  private static final int HASH_SEED = 1738;

  /* There are 2^p registers. Per the HyperMinHash algorithm, hashes are bucketed based on the value
   * of their bitstring's first p bits. The q most significant bits of the packed register represent
   * the number of leading zeroes in bits 2^p through 2^p + 2^q - 1 bits in the hash bitstring.
   * The remaining r bits are the rightmost r bits in the hash bitstring.
   */
  final long[] packedRegisters;
  //TODO validation for these
  final int p;
  final int q;
  final int r;

  private final HmhCardinalityEstimator cardinalityEstimator;

  @Override
  public long cardinality() {
    return cardinalityEstimator.estimateCardinality(packedRegisters, q, r);
  }

  @Override
  public boolean offer(byte[] bytes) {
    return false;
  }

  @Override
  public int sizeInBytes() {
    return 0;
  }

  @Override
  public byte[] getBytes() {
    return new byte[0];
  }

  @Override
  public IntersectionSketch deepCopy() {
    return null;
  }
}
