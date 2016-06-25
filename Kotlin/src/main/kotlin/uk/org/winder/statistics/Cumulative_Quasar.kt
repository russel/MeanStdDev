package uk.org.winder.statistics

import co.paralleluniverse.strands.channels.Channels

import co.paralleluniverse.fibers.Suspendable

import co.paralleluniverse.kotlin.fiber

import java.lang.Math.sqrt

object Cumulative_Quasar {
  fun meanStdDev(data:Iterable<Number>): Result {
    val numbers = Channels.newChannel<Number>(0)
    val toCounter = Channels.newDoubleChannel(0)
    val toSum = Channels.newDoubleChannel(0)
    val toSumSq = Channels.newDoubleChannel(0)
    val fromCounter = Channels.newIntChannel(0)
    val fromSum = Channels.newDoubleChannel(0)
    val fromSumSq = Channels.newDoubleChannel(0)
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
      data.forEach{
        val d = it.toDouble()
        toCounter.send(d)
        toSum.send(d)
        toSumSq.send(d)
      }
    }
    fiber @Suspendable {
      var count = 0
      while (true) {
        toCounter.receive()
        ++count
        fromCounter.send(count)
      }
    }
    fiber @Suspendable {
      var sum = 0.0
      while (true) {
        val item = toSum.receive()
        sum += item
        fromSum.send(sum)
      }
    }
    fiber @Suspendable {
      var sumSq = 0.0
      while (true) {
        val item = toSumSq.receive()
        sumSq += item * item
        fromSumSq.send(sumSq)
      }
    }
    fiber @Suspendable {
      while (true) {
        val count = fromCounter.receive()
        val sum = fromSum.receive()
        val sumSq = fromSumSq.receive()
        val df = count - 1
        val xb = sum / count
        val sd = sqrt((sumSq - count * xb * xb) / df)
        print("xb = " + xb + ", s = " + sd + ", df = " + df)
      }
    }
    // TODO temporary hack to get compilation
    return Result(1.0, 1.0, 1)
  }
}