package com.liveramp.hyperminhash;

public interface CardinalitySketch {

  /**
   * Returns an estimate of the cardinality of sets represented bythe sketch.
   */
  long cardinality();

  /**
   * Add an element whose binary representation is {@code val} to the sketch. This is analagous to
   * adding the object to the set approximated by the sketch.
   */
  void add(byte[] val);

}

