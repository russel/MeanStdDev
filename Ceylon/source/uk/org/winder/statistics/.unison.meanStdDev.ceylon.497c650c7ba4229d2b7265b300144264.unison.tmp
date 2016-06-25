
import java.lang{Math}

shared alias Result => [Float, Float, Integer];

shared Result meanStdDev(Iterable<Float|Integer> data) {
    value sums = data.fold([0.0, 0.0, 0])((a, i) {
      value f = switch (i)
        case (is Integer) i.float
        case (is Float) i;
      return   [a[0] + f, a[1] + f * f, a[2] + 1];
    });
    value n = sums[2];
    value xb = sums[0] / n;
    value df = n - 1;
    return  [xb,  Math.sqrt((sums[1] - n * xb * xb) / df), df];
}
