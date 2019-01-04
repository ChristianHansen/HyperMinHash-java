package com.liveramp.hyperminhash;

import java.util.Collection;

public interface CardinalitySketch {

  long cardinality();

  void add(byte[] val);

}

