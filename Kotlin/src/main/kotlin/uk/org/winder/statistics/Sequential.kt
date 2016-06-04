package uk.org.winder.statistics

import java.lang.Math.sqrt

object Sequential {
  fun meanStdDev(data:Iterable<Number>): Result {
    data class Reducer(val sum:Double, val sumSq:Double, val count:Int)
    val sums = data.fold(Reducer(0.0, 0.0, 0), {r, n ->
      val x = n.toDouble()
      Reducer(r.sum + x, r.sumSq + x * x, r.count + 1)
    })
    return when (sums.count) {
      0 -> Result(Double.NaN, Double.NaN, 0)
      1 -> Result(sums.sum, Double.NaN, 1)
      else -> {
        val xb = sums.sum / sums.count
        val df = sums.count - 1
        Result(xb, sqrt((sums.sumSq - sums.count * xb * xb) / df), df)
      }
    }
  }
}