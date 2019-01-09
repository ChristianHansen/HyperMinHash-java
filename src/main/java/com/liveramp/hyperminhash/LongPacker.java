package com.liveramp.hyperminhash;

class LongPacker {

  static long pack(int numLeadingZeroes, long rBits, int r) {
    return (numLeadingZeroes << r) | rBits;
  }

  static long unpackMantissa(long register, int r) {
    // Just clear the exponent bits + any unused bits if 2^q + r < 64
    return register << (Long.SIZE - r) >>> (Long.SIZE - r);
  }

  static int unpackExponent(long register, int q, int r) {
    final int numUnusedBits = Long.SIZE - r - (q + 1);
    return Math.toIntExact(
        // Clear unused bits, then shift the exponent down into the 2^q least significant bits.
        (register << (numUnusedBits)) >>> (numUnusedBits + r)
    );
  }

}
