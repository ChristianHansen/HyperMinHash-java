package com.liveramp.hyperminhash.bithelper;

public class BitHelper {

  /**
   * @return the leftmost (most significant) {@code numBits} bits in {@code value} in int as the
   *     {@code numBits} least significant bits in that int.
   */
  public static long getLeftmostBits(long value, int numBits) {
    if (numBits >= Long.SIZE) {
      throw new IllegalArgumentException(String.format("numBits must be < %d", Integer.SIZE));
    }

    return (value >>> (Long.SIZE - numBits));
  }

  /**
   * @return the {@code rightmost} (least significant) bits of {@code value} as the {@code numBits}
   *     least significant bits in a long.
   */
  public static long getRightmostBits(long value, int numBits) {
    if (numBits >= Long.SIZE) {
      throw new IllegalArgumentException(String.format("numBits must be < %d", Short.SIZE));
    }
    return (value << (Long.SIZE - numBits)) >>> (Long.SIZE - numBits);
  }

  /**
   * @return the position of the leftmost one-bit in the first (2^q)-1 bits.
   */
  public static byte getLeftmostOneBitPosition(long hash, int q) {
    // To find the position of the leftmost 1-bit in the first (2^Q)-1 bits
    // We zero out all bits to the right of the first (2^Q)-1 bits then add a
    // 1-bit in the 2^Qth position of the bits to search. This way if the bits we're
    // searching are all 0, we take the position of the leftmost 1-bit to be 2^Q
    int _2q = (1 << q) - 1;
    int shiftAmount = (Long.SIZE - _2q);

    // zero all bits to the right of the first (2^Q)-1 bits
    long _2qSearchBits = ((hash >>> shiftAmount) << shiftAmount);

    // add a 1-bit in the 2^Qth position
    _2qSearchBits += (1 << (shiftAmount - 1));

    return (byte) (Long.numberOfLeadingZeros(_2qSearchBits) + 1);
  }
}
