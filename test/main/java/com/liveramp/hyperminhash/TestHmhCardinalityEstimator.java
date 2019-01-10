package com.liveramp.hyperminhash;

import org.junit.Assert;
import org.junit.Test;

public class TestHmhCardinalityEstimator {

  @Test
  public void testBiasCorrection() {
    // TODO this is broken
    final double[] rawEstimates = {0.0, 5.0, 10.0, 15.0};
    final double[] biases = {1.0, 3.0, -2.0, 4.0};
    final long basicEstimate = 7;
    final double slope = (-2.0 - 3.0) / (10.0 - 5.0);
    final double expectedOutput = 7 - 5 + (slope * (7.0 - 5.0));
    final double actualEstimate = HmhCardinalityEstimator.biasCorrectEstimate(
        basicEstimate,
        rawEstimates,
        biases
    );

    Assert.assertEquals(expectedOutput, actualEstimate, 0.0001);
  }


}
