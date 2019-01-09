package com.liveramp.hyperminhash;

public class HyperMinHashCombiner implements SketchCombiner<HyperMinHash> {

  // TODO
//  public HyperMinHash union(Collection<HyperMinHash> sketches){
//    return union(sketches.toArray(new ))
//  }

  @Override
  public HyperMinHash union(HyperMinHash... sketches) {
    if (sketches.length == 0) {
      throw new IllegalArgumentException("Input sketches cannot be empty.");
    }

    if (sketches.length == 1) {
      return sketches[0].deepCopy();
    }

    final int numRegisters = sketches[0].packedRegisters.length;
    final HyperMinHash mergedSketch = sketches[0].deepCopy();

    for (int i = 0; i < numRegisters; i++) {
      for (HyperMinHash sketch : sketches) {
        
        mergedSketch.packedRegisters[i] = Math.max(
            mergedSketch.packedRegisters[i],
            sketch.packedRegisters[i]
        );
      }
    }

    return mergedSketch;
  }

  @Override
  public long intersectionCardinality(HyperMinHash... sketches) {
    return 0;
  }

  @Override
  public double similarity(HyperMinHash... sketches) {
    return 0;
  }
}
