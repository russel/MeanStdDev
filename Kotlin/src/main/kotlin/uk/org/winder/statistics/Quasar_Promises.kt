package uk.org.winder.statistics

import co.paralleluniverse.strands.channels.Channels

import co.paralleluniverse.fibers.Suspendable

import co.paralleluniverse.kotlin.fiber

import java.lang.Math.sqrt

object Quasar_Promises {
  fun meanStdDev(data:Iterable<Number>): Result {
    val numbers = Channels.newChannel<Number>(0)
    val toCounter = Channels.newDoubleChannel(0)
    val toSum = Channels.newDoubleChannel(0)
    val toSumSq = Channels.newDoubleChannel(0)
    fiber @Suspendable {
      while (true) {
        val item = numbers.receive()
        if (item == null) break
        val d = item.toDouble()
        toCounter.send(d)
        toSum.send(d)
        toSumSq.send(d)
      }
      toCounter.close()
      toSum.close()
      toSumSq.close()
    }
    val count = fiber @Suspendable {
      var count = 0
      while (toCounter.receive() != null) { ++count }
      count
    }
    val sum = fiber @Suspendable {
      var sum = 0.0
      while (true) {
        val item = toSum.receive()
        if (item == null) break
        sum += item
      }
      sum
    }
    val sumSq = fiber @Suspendable {
      var sumSq = 0.0
      while (true) {
        val item = toSumSq.receive()
        if (item == null) break
        sumSq += item * item
      }
      sumSq
    }
    val result = fiber @Suspendable {
      val n = count.get()
      val df = n - 1
      val xb = sum.get() / n
      val sd = sqrt((sumSq.get() - n * xb * xb) / df)
      Result(xb, sd, df)
    }
    data.forEach{ numbers.send(it) }
    numbers.close()
    return result.get()
  }
}