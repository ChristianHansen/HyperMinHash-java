package com.liveramp.hyperminhash.tunable;

import com.liveramp.hyperminhash.IntersectionSketch;

public class HyperMinHash implements IntersectionSketch {


  public static HyperMinHash fromBytes(byte[] bytes) {
    //TODO
  }

  @Override
  public long cardinality() {
    return 0;
  }

  @Override
  public boolean offer(byte[] bytes) {
    return false;
  }

  @Override
  public int sizeInBytes() {
    return 0;
  }

  @Override
  public byte[] getBytes() {
    return new byte[0];
  }

  @Override
  public IntersectionSketch deepCopy() {
    return null;
  }
}
