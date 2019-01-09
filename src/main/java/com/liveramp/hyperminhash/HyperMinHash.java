package com.liveramp.hyperminhash;

import com.google.common.base.Preconditions;
import util.hash.MetroHash;
import util.hash.MetroHash128;

import java.util.Arrays;

public class HyperMinHash implements IntersectionSketch<HyperMinHash> {

  private static final int HASH_SEED = 1738;

  /* There are 2^p registers. Per the HyperMinHash algorithm, hashes are bucketed based on the value
   * of their bitstring's first p bits. The r least significant bits in the bitstring in are stored
   * as the r least significant bits in the register.
   * The number of leading zeroes in positions 2^p through 2^p + 2^q - 1 in the bitstring is stored
   * in the registers bits that are the q + 1 next least significant bits after the r least
   * significant bits i.e. number of leading zeroes is stored in bits r through r + q - 1 of the long.
   */
  final long[] registers;
  //TODO validation for these. p + numZeroSearchBits = 64
  final int p; // must be at least 4
  // This is 2^q + 1 in the HMH paper. We use this to represent the space that we're searching for a
  // leading zero.
  final int numZeroSearchBits;
  final int r;

  private final HmhCardinalityEstimator cardinalityEstimator;

  /**
   * @param p HLL precision parameter
   * @param r Number of MinHash bits to keep
   */
  public HyperMinHash(int p, int r) {
    this(p, r, null);
  }

  HyperMinHash(int p, int r, long[] registers) {
    // Ensure that the number of registers isn't larger than the largest array java can hold in memory
    // biggest java array can be of size Integer.MAX_VALUE
    Preconditions.checkArgument(p < 31);

    // Ensure that we can pack the number of leading zeroes and the least significant r bits from
    // the hash bitstring into a long "register."
    Preconditions.checkArgument(r < 58);
    this.p = p;
    this.numZeroSearchBits = Long.SIZE - p;
    this.r = r;
    this.cardinalityEstimator = new HmhCardinalityEstimator();
    if (registers == null) {
      this.registers = new long[(int) Math.pow(2, p)];
    } else {
      this.registers = registers;
    }
  }


  @Override
  public long cardinality() {
    return cardinalityEstimator.estimateCardinality(registers, numZeroSearchBits, r);
  }

  @Override
  public boolean offer(byte[] bytes) {
    MetroHash128 hash = MetroHash.hash128(HASH_SEED, bytes);

    // left half of the hash is used for HLL, right half for min hash
    long leftHalf = hash.getHigh();
    long rightHalf = hash.getLow();

    // Unsafely cast to int because we assume p < 32
    int registerIndex = (int) leftHalf >>> numZeroSearchBits;
    // zero out leftmost p bits and find position of leftmost one
    // We add a one to the right of the zero search space just in case the entire space is zeros
    long zeroSearchSpace = (leftHalf << p) | (1 << p - 1);
    int leftmostOnePosition = Long.numberOfLeadingZeros(zeroSearchSpace) + 1;
    // We take the leftmost R bits as the minHash bits
    long minHashBits = rightHalf >>> (Long.SIZE - r);

    long incomingRegister = LongPacker.pack(leftmostOnePosition, minHashBits, r);
    if (shouldReplace(registers[registerIndex], incomingRegister, numZeroSearchBits, r)) {
      registers[registerIndex] = incomingRegister;
      return true;
    }

    return false;
  }

  @Override
  public HyperMinHash deepCopy() {
    return new HyperMinHash(p, r, Arrays.copyOf(registers, registers.length));
  }

  // we could replace this with a single comparison of the registers but it'd be less clear
  // it could be swapped if it meaningfully affects performance
  static boolean shouldReplace(long currentRegister, long incomingRegister, int q, int r) {
    int currentLeadingOnePosition = LongPacker.unpackPositionOfFirstOne(currentRegister, q, r);
    int incomingLeadingOnePosition = LongPacker.unpackPositionOfFirstOne(incomingRegister, q, r);

    if (currentLeadingOnePosition < incomingLeadingOnePosition) {
      return true;
    } else if (currentLeadingOnePosition == incomingLeadingOnePosition) {
      long currentMantissa = LongPacker.unpackMantissa(currentRegister, r);
      long incomingMantissa = LongPacker.unpackMantissa(incomingRegister, r);

      if (currentMantissa > incomingMantissa) {
        return true;
      }
    }

    return false;
  }
}
