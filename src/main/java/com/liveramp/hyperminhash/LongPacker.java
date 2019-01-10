package com.liveramp.hyperminhash;

/**
 * Class used to pack a
 */
class LongPacker {

  static long pack(int positionOfFirstOne, long mantissa, int r) {
    if (positionOfFirstOne > Math.pow(2, 6) - 1) {
      throw new IllegalArgumentException("position of first one must fit into 6 bits");
    }

    if (mantissa > (1L << 57) - 1) {
      throw new IllegalArgumentException("mantissa must fit into 57 bits");
    }

    return ((long) positionOfFirstOne << (long) r) | mantissa;
  }

  static long unpackMantissa(long register, int r) {
    // Just clear the exponent bits + any unused bits if 2^q + r < 64
    return (register << (Long.SIZE - r)) >>> (Long.SIZE - r);
  }

  static int unpackPositionOfFirstOne(long register, int r) {
    return Math.toIntExact(register >>> r);
  }
}
