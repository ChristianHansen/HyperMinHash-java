package com.liveramp.hyperminhash;

import com.google.common.base.Preconditions;

public class HyperMinHash implements IntersectionSketch<HyperMinHash> {

  private static final int HASH_SEED = 1738;

  /* There are 2^p registers. Per the HyperMinHash algorithm, hashes are bucketed based on the value
   * of their bitstring's first p bits. The r least significant bits in the bitstring in are stored
   * as the r least significant bits in the register.
   * The number of leading zeroes in positions 2^p through 2^p + 2^q - 1 in the bitstring is stored
   * in the registers bits that are the q + 1 next least significant bits after the r least
   * significant bits i.e. number of leading zeroes is stored in bits r through r + q - 1 of the long.
   */
  final long[] packedRegisters;
  //TODO validation for these. p + numZeroSearchBits = 64
  final int p; // must be at least 4
  // This is 2^q + 1 in the HMH paper. We use this to represent the space that we're searching for a
  // leading zero.
  final int numZeroSearchBits;
  final int r;

  private final HmhCardinalityEstimator cardinalityEstimator;

  public HyperMinHash(int p, int r) {
    this.p = p;
    this.numZeroSearchBits = Long.SIZE - p;
    // Ensure that we can pack the number of leading zeroes and the least significant r bits from
    // the hash bitstring into a long "register."
    Preconditions.checkArgument(r < 58);
    this.r = r;

  }


  @Override
  public long cardinality() {
    return cardinalityEstimator.estimateCardinality(packedRegisters, numZeroSearchBits, r);
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
  public HyperMinHash deepCopy() {
    return null;
  }
}
