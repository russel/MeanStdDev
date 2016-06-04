package uk.org.winder.statistics

import io.kotlintest.specs.FunSpec

class Test_All: FunSpec() {
  init {

    data class D(val value:List<Number>, val result:Result)

    val sqrtHalf = 0.7071067811865476

    val data = arrayOf(
        D(emptyList(), Result(Double.NaN, Double.NaN, 0)),
        D(listOf(1.0), Result(1.0, Double.NaN, 1)),
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

    forAll(data) {item ->
      test("meanStdDev(" + item.value+ ") == " + item.result) {
        Sequential.meanStdDev(item.value) shouldEqual item.result
      }
    }

  }
}