#! /usr/bin/env groovy

import groovyx.gpars.dataflow.DataflowQueue
import groovyx.gpars.dataflow.operator.PoisonPill

import static groovyx.gpars.dataflow.Dataflow.operator
import static groovyx.gpars.dataflow.Dataflow.splitter

import static Math.sqrt

final numberSource = new DataflowQueue<Double>()

final countInputs = new DataflowQueue<Double>()
final sumInputs = new DataflowQueue<Double>()
final sumSqInputs = new DataflowQueue<Double>()

final distributor = splitter(numberSource, [countInputs, sumInputs, sumSqInputs])

final count = new DataflowQueue<Integer>()
final sum = new DataflowQueue<Double>()
final sumSq = new DataflowQueue<Double>()

final counter = operator(inputs: [countInputs], outputs: [count], stateObject: [total: 0i]) {Double x ->
  ++stateObject.total
  bindOutput 0, stateObject.total
}

final summer = operator(inputs: [sumInputs], outputs:[sum], stateObject:[total: 0.0d]) {Double x ->
  stateObject.total += x
  bindOutput 0, stateObject.total
}

final summerSquares = operator(inputs: [sumSqInputs], outputs: [sumSq], stateObject: [total: 0.0d]) {Double x ->
  stateObject.total += x * x
  bindOutput 0, stateObject.total
}

final results = new DataflowQueue<List<Double>>()

final calculator = operator(inputs: [count, sum, sumSq], outputs: [results]) {int n, double s, double ss ->
  double xb = s / n
  bindOutput 0, [xb, sqrt(ss - n * xb * xb) / (n -1), n - 1]
}

final printer = operator(inputs: [results], outputs: []) {item ->
  assert item.size() == 3
  println "Mean = ${item[0]}, std.dev = ${item[1]}, df = ${item[2]}"
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

file.eachLine { line ->
  line.split().collect{Double.parseDouble(it)}.each{numberSource << it}
}

numberSource << PoisonPill.instance
[distributor, counter, summer, summerSquares, calculator, printer]*.join()