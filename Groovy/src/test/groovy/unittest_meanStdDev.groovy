#!/usr/bin/env groovy

import spock.lang.Specification
import spock.lang.Unroll

import static equalityTests.areTriplesEqual

import meanStdDev_sequential
//import meanStdDev_parallel
import meanStdDev_futures
import meanStdDev_dataflowVariables
import meanStdDev_dataflowOperators

class unittest_meanStdDev extends Specification {

  final static functions = [
      [meanStdDev_sequential.&meanStdDev, 'meanStdDev_sequential'],
      //[meanStdDev_parallel.&meanStdDev, 'meanStdDev_parallel'], // TODO: Fails due to incorrect missing method.
      [meanStdDev_futures.&meanStdDev, 'meanStdDev_futures'],
      [meanStdDev_dataflowVariables.&meanStdDev, 'meanStdDev_dataflowVariables'],
      [meanStdDev_dataflowOperators.&meanStdDev, 'meanStdDev_dataflowOperators'],
  ]

  @Unroll
  def 'mean and std dev not defined for string argument, #s'() {
    when:
    f.call('123456')
    then:
    thrown MissingMethodException
    where:
    [f, s] << functions
  }

  @Unroll
  def 'mean and std dev not defined for map argument, #s'() {
    when:
     f.call([a: 1, b: 2])
    then:
     thrown MissingMethodException
    where:
     [f, s] << functions
  }

  @Unroll
  def 'mean and std dev not defined on sequence of non-numeric values, #s'() {
    when:
     f.call(['12345', []])
    then:
     Throwable t = thrown()
     (t instanceof MissingMethodException) ||
         (t instanceof IllegalArgumentException) ||
         (t instanceof org.codehaus.groovy.runtime.typehandling.GroovyCastException)
    where:
     [f, s] << functions
  }

  @Unroll
  def 'mean and std dev of no data is not defined, #s'() {
    expect:
     f.call([]) ==  [Double.NaN, Double.NaN, -1]
    where:
     [f, s] << functions
  }

  @Unroll
  def 'std dev of single integer item is not defined, #s'() {
    expect:
     f.call([1]) == [1.0, Double.NaN, 0] // Risk here due to int/double equality test.
    where:
    [f, s] << functions
  }

  @Unroll
  def 'std dev of single real item is not defined, #s'() {
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
  def '#s(#item) == [#xb, #sd, #df]'() {
    expect:
     areTriplesEqual(f.call(item), [xb, sd, df])
    where:
     [f, s, item, xb, sd, df] << functions.collectMany {a -> testData.collect{d -> [*a, *d]}}
  }

}
