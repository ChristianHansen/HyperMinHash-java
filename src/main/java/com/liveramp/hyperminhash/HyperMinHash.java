package com.liveramp.hyperminhash;

import org.apache.commons.lang.NotImplementedException;

public class HyperMinHash implements IntersectionSketch {

  private static final int HASH_SEED = 1738;

  /* There are 2^p registers. Per the HyperMinHash algorithm, hashes are bucketed based on the value
   * of their bitstring's first p bits. The r least significant bits in the bitstring in are stored
   * as the r least significant bits in the register.
   * The number of leading zeroes in positions 2^p through 2^p + 2^q - 1 in the bitstring is stored
   * in the registers bits that are the q next least significant bits after the r least significant
   * bits i.e. number of leading zeroes is stored in bits r through r + q - 1 of the long.
   */
  final long[] packedRegisters;
  //TODO validation for these
  final int p; // must be at least 4
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
