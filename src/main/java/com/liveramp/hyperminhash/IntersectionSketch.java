package com.liveramp.hyperminhash;

/**
 * Representation of a set that is able to estimate the cardinality of that set, and perform the
 * operations in SketchCombiner. Each implementation of this interface should have a corresponding
 * implementation of SketchCombiner.
 */
public interface IntersectionSketch<T extends IntersectionSketch<T>> {

  /**
   * Returns an estimate of the cardinality of sets represented by the sketch.
   */
  long cardinality();

  /**
   * @param bytes a representative key or serialized representation of the object to be added to
   *              this sketch. If using a representative key instead of a complete serialized
   *              representation, using at least 128 bits for the key where possible is recommended
   *              to maximize accuracy.
   * @return false if the value returned by cardinality() is unaffected by the appearance of o in
   * the stream.
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

  /**
   * @return a deep copy of the {@link IntersectionSketch} instance.
   */
  T deepCopy();
}

