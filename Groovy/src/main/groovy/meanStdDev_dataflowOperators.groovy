#! /usr/bin/env groovy

import groovyx.gpars.dataflow.DataflowQueue
import groovyx.gpars.dataflow.operator.PoisonPill

import static groovyx.gpars.dataflow.Dataflow.operator
import static groovyx.gpars.dataflow.Dataflow.splitter

import static Math.sqrt

static List<Number> meanStdDev(final Iterable<Number> data) {
  final numberSource = new DataflowQueue<Number>()
  final countInputs = new DataflowQueue<Number>()
  final sumInputs = new DataflowQueue<Number>()
  final sumSqInputs = new DataflowQueue<Number>()
  final distributor = splitter(numberSource, [countInputs, sumInputs, sumSqInputs])
  final count = new DataflowQueue<Integer>()
  final sum = new DataflowQueue<Double>()
  final sumSq = new DataflowQueue<Double>()
  final counter = operator(inputs:[countInputs], outputs:[count], stateObject: [total: 0i]) { x ->
    ++stateObject.total
    bindOutput 0, stateObject.total
  }
  final summer = operator(inputs: [sumInputs], outputs:[sum], stateObject:[total: 0.0d]) { Number x ->
    stateObject.total += x.doubleValue()
    bindOutput 0, stateObject.total
  }
  final summerSquares = operator(inputs: [sumSqInputs], outputs: [sumSq], stateObject: [total: 0.0d]) { Number x ->
    final double a = x.doubleValue()
    stateObject.total += a * a
    bindOutput 0, stateObject.total
  }
  final results = new DataflowQueue<List<Number>>()
  final calculator = operator(inputs: [count, sum, sumSq], outputs: [results]) { int n, double s, double ss ->
    double xb = s / n
    bindOutput 0, [xb, sqrt(ss - n * xb * xb) / (n -1), n - 1]
  }
  for (final item in data) {
    if (item instanceof Number) { numberSource << item }
    else { throw new IllegalArgumentException() }
  }
  numberSource << PoisonPill.instance
  [distributor, counter, summer, summerSquares, calculator]*.join()
  assert results.length() > 0
  if (results.length() == 1) { return [Double.NaN, Double.NaN, -1] }
  def result
  while (results.length() > 1) { result = results.val }
  result
}

def file = System.in
switch (args.size()) {
 case 0:
  break
 case 1:
  file = new File(args[0])
  break
 default:
  println 'Zero or one arguments only.'
  return
}

def (xb, sd, df) = meanStdDev(file.text.split().collect{Double.parseDouble(it)})
println "Mean = ${xb}, std.dev = ${sd}, df = ${df}"
