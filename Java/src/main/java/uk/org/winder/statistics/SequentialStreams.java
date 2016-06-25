package uk.org.winder.statistics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static java.lang.Math.sqrt;

public class SequentialStreams {
  public static List<Number> meanStdDev(final Iterable<Number> iterable) {
    // TODO There must be a better way of pulling on the iterable to create the list.
    final List<Number> data = new ArrayList<Number>();
    for (final Number n: iterable) { data.add(n); }
    return meanStdDev(data);
  }
  public static List<Number> meanStdDev(final Collection<Number> data) {
    final int n = data.size();
    switch (n) {
      case 0: return Arrays.asList(Double.NaN, Double.NaN, -1);
      case 1: return Arrays.asList(data.iterator().next(), Double.NaN, 0);
      default: {
        final double xb = data.stream().mapToDouble(Number::doubleValue).sum() / n;
        final double sumSq = data.stream().mapToDouble(x -> {
          final double y = x.doubleValue();
          return y * y;
        }).sum();
        return Arrays.asList(xb, sqrt((sumSq - n * xb * xb) / (n - 1)), n - 1);
      }
    }
  }
}
