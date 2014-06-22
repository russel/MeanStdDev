#! /usr/bin/env groovy

import groovyx.gpars.dataflow.DataflowBroadcast

import static groovyx.gpars.dataflow.Dataflow.task

import static Math.sqrt

static List<Number> meanStdDev(final Iterable<Number> data) {
  final numberSource = new DataflowBroadcast()
  final countInputs = numberSource.createReadChannel()
  final sumInputs = numberSource.createReadChannel()
  final sumSqInputs = numberSource.createReadChannel()
  final count = task {
    int n = 0
    while (countInputs.val != Double.NaN) { ++n }
    n
  }
  final df = task { count.get() - 1 }
  final mean = task {
    double sum = 0.0d
    double x
    while ((x = (double)sumInputs.val) != Double.NaN) { sum += x }
    sum / count.get()
  }
  final stdDev = task {
    double sum = 0.0d
    double x
    while ((x = (double)sumSqInputs.val) != Double.NaN) { sum += x * x }
    double m = mean.get()
    sqrt(sum - count.get() * m * m) / df.get()
  }
  for (final item in data) { numberSource << item }
  numberSource << Double.NaN
  [mean.get(), stdDev.get(), df.get()]
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
