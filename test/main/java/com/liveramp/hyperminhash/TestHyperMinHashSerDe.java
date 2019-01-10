package com.liveramp.hyperminhash;

import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

public class TestHyperMinHashSerDe {

  @Test
  public void testRoundtripEmptySketch() {
    HyperMinHashSerDe serde = new HyperMinHashSerDe();
    HyperMinHash hyperMinHash = new HyperMinHash(10, 10);
    Assert.assertEquals(hyperMinHash, serde.fromBytes(serde.toBytes(hyperMinHash)));
  }

  @Test
  public void testRoundtripFilledSketch() {
    HyperMinHashSerDe serde = new HyperMinHashSerDe();
    int iterations = 10_000;
    for (int i = 0; i < iterations; i++) {
      HyperMinHash hmh = new HyperMinHash(14, 30);
      int numElements = 1000;
      for (int j = 0; j < numElements; j++) {
        hmh.offer(randomByteArrayOfLength(50));
      }

      Assert.assertEquals(hmh, serde.fromBytes(serde.toBytes(hmh)));
    }
  }

  @Test
  public void testSizeInBytes() {

  }

  Random rng = new Random();

  private byte[] randomByteArrayOfLength(int n) {
    byte[] bytes = new byte[n];
    rng.nextBytes(bytes);
    return bytes;
  }
}
