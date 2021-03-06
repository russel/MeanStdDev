package uk.org.winder.statistics

import spock.lang.Specification
import spock.lang.Unroll

import java.util.concurrent.CompletionException

import static EqualityTests.areTriplesEqual

class All_Test extends Specification {

  static final functions = [
      [SequentialStreams.&meanStdDev, 'SequentialStreams'],
      [ParallelStreams.&meanStdDev, 'ParallelStreams'],
      [CompletableFutures.&meanStdDev, 'CompletableFutures'],
  ]

  @Unroll
  def '#s: mean and std dev not defined for string argument'() {
    when:
     f.call('123456')
    then:
     thrown MissingMethodException
    where:
     [f, s] << functions
  }

  @Unroll
  def '#s: mean and std dev not defined for map argument'() {
    when:
     f.call([a: 1, b: 2])
    then:
     thrown MissingMethodException
    where:
     [f, s] << functions
  }

  @Unroll
  def '#s: mean and std dev not defined on sequence of non-numeric values'() {
    when:
     f.call(['12345', []])
    then:
     Throwable t = thrown()
     (t instanceof ClassCastException) || ((t instanceof CompletionException) && (t.cause instanceof ClassCastException))
    where:
     [f, s] << functions
  }

  @Unroll
  def '#s: mean and std dev of no data is not defined'() {
    expect:
     f.call([]) == [Double.NaN, Double.NaN, -1]
    where:
     [f, s] << functions
  }

  @Unroll
  def '#s: std dev of single integer item is not defined'() {
    expect:
    f.call([1]) == [1.0, Double.NaN, 0] // Risk here due to int/double equality test.
    where:
    [f, s] << functions
  }

  @Unroll
  def '#s: std dev of single double item is not defined'() {
    expect:
     f.call([1.0]) == [1.0, Double.NaN, 0] // Risk here due to double/double equality test.
    where:
     [f, s] << functions
  }

  static final sqrtHalf = 0.7071067811865476

  static final testData = [
     [[1, 2], 1.5, sqrtHalf, 1],
     [[1, 2.0], 1.5, sqrtHalf, 1],
     [[1.0, 2], 1.5, sqrtHalf, 1],
     [[1.0, 2.0], 1.5, sqrtHalf, 1],
     [[1, 1, 1], 1.0, 0.0, 2],
     [[1, 1, 1.0], 1.0, 0.0, 2],
     [[1, 1.0, 1], 1.0, 0.0, 2],
     [[1.0, 1, 1], 1.0, 0.0, 2],
     [[1.0, 1.0, 1.0], 1.0, 0.0, 2],
     [[1.0, 2.0, 1.0, 2.0], 1.5, 0.5773502691896257, 3],
  ]

  @Unroll
  def '#s meanStdDev(#item) == [#xb, #sd, #df]'() {
    expect:
     areTriplesEqual(f.call(item), [xb, sd, df])
    where:
     [f, s, item,  xb,  sd,  df] << functions.collectMany{a -> testData.collect{d -> [*a, *d]}}
  }

}
