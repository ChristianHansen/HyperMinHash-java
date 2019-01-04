package com.liveramp.hyperminhash;


public interface SketchCombiner<T extends IntersectionSketch> {

  /**
   * Return a sketch representing the union of the sets represented by the sketches in {@code
   * sketches}.
   */
  T union(T... sketches);

  /**
   * Return an estimate of the cardinality of the intersection of the elements in the sets
   * represented by {@code sketches}.
   */
  long intersectionCardinality(T... sketches);

  /**
   * Return an estimate of the Jaccard index of the sets represented by {@code sketches}. The
   * Jaccard index is the ratio of the cardinality of the intersection of sets divided by the
   * cardinality of the union of those ses.
   */
  double similarity(T... sketches);

}
