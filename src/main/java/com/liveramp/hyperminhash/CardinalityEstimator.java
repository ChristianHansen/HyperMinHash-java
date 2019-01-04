package com.liveramp.hyperminhash;

import java.util.Collection;

public interface CardinalityEstimator<T extends CardinalitySketch> {

  T merge(Collection<T> sketches);

  T union(Collection<T> sketches);

  long intersectionCardinality(Collection<T> sketches);

  /** Jaccard index estimation. */
  double similarity(Collection<T> sketches);
}
