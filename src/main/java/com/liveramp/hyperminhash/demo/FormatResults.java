package com.liveramp.hyperminhash.demo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;

public class FormatResults {
  public static void main(String[] args) throws FileNotFoundException {
    List<Double> percentages = Lists.newArrayList(1.0);

    for (double pct = 0.1; pct >= 0.00_000_000_002; pct /= 10) {
      for (int i = 1; i < 10; i++) {
        percentages.add(pct * i);
      }
    }

    Collections.sort(percentages);
    Collections.reverse(percentages);

    List<String> lines = new BufferedReader(new FileReader("/Users/shnada/Downloads/HyperMinHash results - Sheet9.csv"))
        .lines().collect(Collectors.toList());

    boolean skipHeader = true;
    int i = 0;

    //    System.out.println(percentages);
    for (String line : lines) {
      if (skipHeader) {
        skipHeader = false;
        continue;
      }
      String[] fields = StringUtils.split(line, ',');
      try {
        if (i < percentages.size()) {
          double actualJaccard = Double.parseDouble(fields[8]);
          BigDecimal bd1 = new BigDecimal(percentages.get(i)).subtract(BigDecimal.valueOf(actualJaccard)).abs();
          BigDecimal bd2 = new BigDecimal(percentages.get(i + 1)).subtract(BigDecimal.valueOf(actualJaccard)).abs();
          if (bd1.compareTo(bd2) > 0) {
            i++;
          }
        }
        System.out.println(percentages.get(i));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
