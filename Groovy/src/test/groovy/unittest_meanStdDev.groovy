#! /usr/bin/env groovy

import spock.lang.Specification
import spock.lang.Unroll

import static equalityTests.areTriplesEqual

import meanStdDev_sequential
//import meanStdDev_parallel
import meanStdDev_promises
import meanStdDev_taskPromises
import meanStdDev_dataflowVariables
import meanStdDev_dataflowOperators

class unittest_meanStdDev extends Specification {

  final static functions = [
      meanStdDev_sequential.&meanStdDev,
      //meanStdDev_parallel.&meanStdDev, // TODO: Fails due to incorrect missing method.
      meanStdDev_promises.&meanStdDev,
      meanStdDev_taskPromises.&meanStdDev,
      meanStdDev_dataflowVariables.&meanStdDev,
      meanStdDev_dataflowOperators.&meanStdDev,
  ]

  @Unroll
  def 'mean and std dev not defined for string argument'() {
    when:
    f.call('123456')
    then:
    thrown MissingMethodException
    where:
    f << functions
  }

  @Unroll
  def 'mean and std dev not defined for map argument'() {
    when:
     f.call([a: 1, b: 2])
    then:
     thrown MissingMethodException
    where:
     f << functions
  }

  @Unroll
  def 'mean and std dev not defined on sequence of non-numeric values'() {
    when:
     f.call(['12345', []])
    then:
     Throwable t = thrown()
     (t instanceof MissingMethodException) ||
         (t instanceof IllegalArgumentException) ||
         (t instanceof org.codehaus.groovy.runtime.typehandling.GroovyCastException)
    where:
     f << functions
  }

  @Unroll
  def 'mean and std dev of no data is not defined'() {
    expect:
     f.call([]) ==  [Double.NaN, Double.NaN, -1]
    where:
     f << functions
  }

  @Unroll
  def 'std dev of single integer item is not defined'() {
    expect:
     f.call([1]) == [1.0, Double.NaN, 0] // Risk here due to int/double equality test.
    where:
    f << functions
  }

  @Unroll
  def 'std dev of single real item is not defined'() {
    expect:
     f.call([1.0]) == [1.0, Double.NaN, 0] // Risk here due to double/double equality test.
    where:
     f << functions
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
       [[1.0, 2.0, 1.0, 2.0], 1.5, 0.333333333333333, 3],
    ]

  @Unroll
  def 'mean and std dev of some items via some algorithm'() {
    expect:
     areTriplesEqual(f.call(item), [xb, sd, df])
    where:
     [f, item, xb, sd, df] << functions.collectMany {a -> testData.collect{d -> [a, d[0], d[1], d[2], d[3]]}}
  }

}
