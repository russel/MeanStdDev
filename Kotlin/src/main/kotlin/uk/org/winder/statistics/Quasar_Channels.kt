package uk.org.winder.statistics

import co.paralleluniverse.strands.channels.Channels

import co.paralleluniverse.fibers.Suspendable

import co.paralleluniverse.kotlin.fiber

import java.lang.Math.sqrt

object Quasar_Channels {
  fun meanStdDev(data:Iterable<Number>): Result {
    val numbers = Channels.newChannel<Number>(10)
    val toCounter = Channels.newDoubleChannel(10)
    val toSum = Channels.newDoubleChannel(10)
    val toSumSq = Channels.newDoubleChannel(10)
    val fromCounter = Channels.newIntChannel(10)
    val fromSum = Channels.newDoubleChannel(10)
    val fromSumSq = Channels.newDoubleChannel(10)
    val result = Channels.newChannel<Result>(10)
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
    fiber @Suspendable {
      var count = 0
      while (toCounter.receive() != null) { ++count }
      fromCounter.send(count)
    }
    fiber @Suspendable {
      var sum = 0.0
      while (true) {
        val item = toSum.receive()
        if (item == null) break
        sum += item
      }
      fromSum.send(sum)
    }
    fiber @Suspendable {
      var sumSq = 0.0
      while (true) {
        val item = toSumSq.receive()
        if (item == null) break
        sumSq += item * item
      }
      fromSumSq.send(sumSq)
    }
    fiber @Suspendable {
      val count = fromCounter.receive()
      val sum = fromSum.receive()
      val sumSq = fromSumSq.receive()
      val df = count - 1
      val xb = sum / count
      val sd = sqrt((sumSq - count * xb * xb) / df)
      result.send(Result(xb, sd, df))
    }
    data.forEach{ numbers.send(it) }
    numbers.close()
    return result.receive()
  }
}