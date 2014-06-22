package uk.org.winder.statistics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static java.lang.Math.sqrt;

public class ParallelStreams {
  public static List<Number> meanStdDev(final Iterable<Number> iterable) {
    final List<Number> data = new ArrayList<Number>();
    for (final Number n : iterable) { data.add(n); }
    return meanStdDev(data);
  }
  public static List<Number> meanStdDev(final Collection<Number> data) {
    final int n = data.size();
    // Assume n is not negative.
    switch (n) {
      case 0: return Arrays.asList(Double.NaN, Double.NaN, -1);
      case 1: return Arrays.asList(data.iterator().next(), Double.NaN, 0);
    }
    final double xb = data.parallelStream().mapToDouble(x -> x.doubleValue()).sum() / n;
    final double sumSq = data.parallelStream().mapToDouble(x -> {
      final Double y = x.doubleValue();
      return y * y;
    }).sum();
    return Arrays.asList(xb, sqrt(sumSq - n * xb * xb) / (n - 1), n - 1);
  }
}
