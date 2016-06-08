package uk.org.winder.statistics

import io.kotlintest.specs.FunSpec

class Test_All: FunSpec() {
  init {

    val algorithms = arrayOf(
        "Sequential" to {l:Iterable<Number> -> Sequential.meanStdDev(l)},
        "Quasar_Channels" to {l:Iterable<Number> -> Quasar_Channels.meanStdDev(l)},
        "Quasar_Promises" to {l:Iterable<Number> -> Quasar_Promises.meanStdDev(l)}
    )

    data class D(val value:List<Number>, val result:Result)

    val sqrtHalf = 0.7071067811865476

    val data = arrayOf(
        D(emptyList(), Result(Double.NaN, Double.NaN, -1)),
        D(listOf(1.0), Result(1.0, Double.NaN, 0)),
        D(listOf(1, 2), Result(1.5, sqrtHalf, 1)),
        D(listOf(1, 2.0), Result(1.5, sqrtHalf, 1)),
        D(listOf(1.0, 2), Result(1.5, sqrtHalf, 1)),
        D(listOf(1.0, 2.0), Result(1.5, sqrtHalf, 1)),
        D(listOf(1, 1, 1), Result(1.0, 0.0, 2)),
        D(listOf(1, 1, 1.0), Result(1.0, 0.0, 2)),
        D(listOf(1, 1.0, 1), Result(1.0, 0.0, 2)),
        D(listOf(1.0, 1, 1), Result(1.0, 0.0, 2)),
        D(listOf(1.0, 1.0, 1.0), Result(1.0, 0.0, 2)),
        D(listOf(1.0, 2.0, 1.0, 2.0), Result(1.5, 0.5773502691896257, 3))
    )

    forAll(algorithms){a ->
      forAll(data){item ->
        test(a.first + ": meanStdDev(" + item.value + ") == " + item.result) {
          a.second(item.value) shouldEqual item.result
        }
      }
    }

  }
}
