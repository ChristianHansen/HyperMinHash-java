package com.liveramp.hyperminhash;

public class HyperMinHashSerDe implements IntersectionSketch.SerDe<HyperMinHash> {

  @Override
  public HyperMinHash fromBytes(byte[] bytes) {
    return null;
  }

  @Override
  public byte[] toBytes(HyperMinHash sketch) {

  }

  @Override
  public long sizeInBytes(HyperMinHash sketch) {
    return 0;
  }
}
