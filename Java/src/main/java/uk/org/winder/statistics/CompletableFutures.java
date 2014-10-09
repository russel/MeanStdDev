package uk.org.winder.statistics;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.lang.Math.sqrt;

public class CompletableFutures {
  public static List<Number> meanStdDev(final Iterable<Number> data) {
    final CompletableFuture<Integer> count = CompletableFuture.supplyAsync(() -> {
      int total = 0;
      for (final Number item: data) { ++total; }
      return total;
    });
    final CompletableFuture<Double> sum = CompletableFuture.supplyAsync(() -> {
      double total = 0.0;
      for (final Number item: data) { total += item.doubleValue(); }
      return total;
    });
    final java.util.concurrent.CompletableFuture<Double> sumsq = java.util.concurrent.CompletableFuture.supplyAsync(() -> {
      double total = 0.0;
      for (final Number item: data) {
        final Double x = item.doubleValue();
        total += x * x;
      }
      return total;
    });
    final Integer n = count.join();
    // Assume n is not negative.
    switch (n) {
      case 0: return Arrays.asList(Double.NaN, Double.NaN, -1);
      case 1: return Arrays.asList(data.iterator().next(), Double.NaN, 0);
    }
    final Double xb = sum.join() / n;
    return Arrays.asList(xb, sqrt(sumsq.join() - n * xb * xb) / (n - 1), n - 1);
  }
}
