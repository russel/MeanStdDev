package uk.org.winder.statistics

import org.scalatest.FunSuite
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.Matchers

import Sequential.{ meanStdDev => meanStdDev_sequential }
import Parallel.{ meanStdDev => meanStdDev_parallel }
import Futures.{meanStdDev => meanStdDev_futures}

class All_Test extends FunSuite with Matchers with TableDrivenPropertyChecks {

  val functions = Table(
    ("function", "name"),
    (meanStdDev_sequential _, "sequential"),
    (meanStdDev_parallel _, "parallel"),
    (meanStdDev_futures _, "futures")
  )

  val sqrtHalf = 0.7071067811865476

  val inputData = Table(
    ("item", "xb", "sd", "df"),
    //(List(1, 2), 1.5, sqrtHalf, 1),
    (List(1.0, 2.0), 1.5, sqrtHalf, 1),
    (Set(1.0, 2.0), 1.5, sqrtHalf, 1),
    (List(1.0, 1.0, 1.0), 1.0, 0.0, 2),
    (List(1.0, 2.0, 1.0, 2.0), 1.5, 0.5773502691896257, 3)
  )

  forAll (functions) { (function: Traversable[Double] => (Double, Double, Int), name:String) =>

    test(name + ": std. dev. of no items is not defined,") {
      val rv = function(List())
      assert(rv._1.isNaN())
      assert(rv._2.isNaN())
      rv._3 shouldBe -1
    }

    test(name + ": std. dev. of single item is not defined, ") {
      val rv = function(List(1.0))
      rv._1 shouldBe 1.0
      assert(rv._2.isNaN())
      rv._3 shouldBe 0
    }

    forAll (inputData) {(item:Traversable[Double], xb:Double, sd:Double, df:Int) =>

      test(name + " meanStdDev(" + item + ") == (" + xb +", " + sd + ", " + df + ")") {
        function(item) shouldBe (xb, sd, df)
      }

    }
  }
}
