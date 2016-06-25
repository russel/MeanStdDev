#!/usr/bin/env groovy

import groovyx.gpars.dataflow.DataflowBroadcast

import static groovyx.gpars.dataflow.Dataflow.task

import static Math.sqrt

static Tuple meanStdDev(final Iterable<Number> data) {
  final numberSource = new DataflowBroadcast()
  final countInputs = numberSource.createReadChannel()
  final sumInputs = numberSource.createReadChannel()
  final sumSqInputs = numberSource.createReadChannel()
  final count = task {
    int n = 0
    while (countInputs.val != Double.NaN) { ++n }
    n
  }
  final sum = task {
    double total = 0.0d
    double x
    while ((x = (double)sumInputs.val) != Double.NaN) { total += x }
    total
  }
  final sumSq = task {
    double total = 0.0d
    double x
    while ((x = (double)sumSqInputs.val) != Double.NaN) { total += x * x }
    total
  }
  final result = task {
    final int n = count.val
    final double mean = sum.val / n
    new Tuple (mean, sqrt((sumSq.val - n * mean * mean) / (n - 1)), n - 1)
  }
  for (final item in data) {
    if (!(item instanceof Number)) { throw new IllegalArgumentException() }
    numberSource << item
  }
  numberSource << Double.NaN
  result.val
}

def file = System.in
switch (args.size()) {
 case 0: break
 case 1: file = new File(args[0]); break
 default: println 'Zero or one arguments only.'; System.exit(-1)
}

def (xb, sd, df) = meanStdDev(file.text.split().collect{Double.parseDouble(it)})
println "Mean = ${xb}, std.dev = ${sd}, df = ${df}"