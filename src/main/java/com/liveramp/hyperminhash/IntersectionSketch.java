package com.liveramp.hyperminhash;

import java.io.IOException;
import java.io.Serializable;

public interface IntersectionSketch extends Serializable {

  /**
   * Returns an estimate of the cardinality of sets represented bythe sketch.
   */
  long cardinality();
  /**
   * @param bytes serialized representation of the object to be added to this sketch
   * @return false if the value returned by cardinality() is unaffected by the appearance of o in the stream.
   */
  boolean offer(byte[] bytes);

  /**
   * @return size in bytes needed for serialization
   */
  int sizeInBytes();

  /**
   * @return A serialized representation of this sketch.
   */
  byte[] getBytes();
}

