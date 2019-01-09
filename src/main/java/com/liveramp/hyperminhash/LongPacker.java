package com.liveramp.hyperminhash;

/**
 * Class used to pack a long with the position of the first zero in a bitstring and trailing bits.
 */
class LongPacker {

  static long pack(int positionOfFirstOne, long mantissa, int r) {
    return (positionOfFirstOne << r) | mantissa;
  }


  static long unpackMantissa(long register, int r) {
    // Just clear the exponent bits + any unused bits if 2^q + r < 64
    return register << (Long.SIZE - r) >>> (Long.SIZE - r);
  }

  static int unpackPositionOfFirstOne(long register, int r) {
    return Math.toIntExact(register >>> r);
  }

}
