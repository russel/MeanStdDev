#!/usr/bin/env groovy

import groovy.transform.CompileStatic

import static Math.sqrt

import groovyx.gpars.GParsPool

/*
 * A function that calculates the mean and standard deviation of a sample. The return value is a `Tuple`
 * representing a triple <`Double`, `Double`, `Integer`> being the mean, the standard deviation and the degrees
 * of freedom.
 *
 * @param data sequence of data values, must be a `List<Number>`
 *
 * @return a `Tuple`with mean (`Double`), standard deviation (`Double`), degrees of freedom (`Integer`).
 */
@CompileStatic
static Tuple meanStdDev(final Iterable<Number> data) {
  final Tuple returnValue
  GParsPool.withPool {
    final List<Number> r = (List<Number>)data.inject([0.0d, 0.0d, 0i]){
      List<Number> t, Number i -> double x = i as double; [t[0] + x, t[1] + x * x, t[2] + 1]
    }
    assert r.size() == 3
    final int n = r[2].intValue()
    switch (n) {
      case 0: returnValue = new Tuple (Double.NaN, Double.NaN, -1); break
      case 1: returnValue = new Tuple (r[0] / n, Double.NaN, 0); break
      default:
        final double xb = r[0] / n
        returnValue = new Tuple (xb, sqrt(r[1] - n * xb * xb) / ((double) (n - 1)), n - 1)
        break
    }
  }
  returnValue
}

def file = System.in
switch (args.size()) {
 case 0: break
 case 1: file = new File(args[0]); break
 default: println 'Zero or one arguments only.'; System.exit(-1)
}
def (xb, sd, df) = meanStdDev(file.text.split().collect{Double.parseDouble(it)})
println "Mean = ${xb}, std.dev = ${sd}, df = ${df}"
