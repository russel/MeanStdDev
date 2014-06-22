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
    (meanStdDev_futures _, "async")
  )

  val inputData = Table(
    ("item", "xb", "sd", "df"),
    //(List(1, 2), 1.5, 0.7071067811865476, 1),
    (List(1.0, 2.0), 1.5, 0.7071067811865476, 1),
    (Set(1.0, 2.0), 1.5, 0.7071067811865476, 1),
    (List(1.0, 1.0, 1.0), 1.0, 0.0, 2),
    (List(1.0, 2.0, 1.0, 2.0), 1.5, 0.33333333333333333, 3)
  )

  forAll (functions) { (function: Traversable[Double] => (Double, Double, Int), name:String) =>

    test("std. dev. of no items is not defined, " + name) {
      val rv = function(List())
      assert(rv._1.isNaN())
      assert(rv._2.isNaN())
      rv._3 shouldBe -1
    }

    test("std. dev. of single item is not defined, " + name) {
      val rv = function(List(1.0))
      rv._1 shouldBe 1.0
      assert(rv._2.isNaN())
      rv._3 shouldBe 0
    }

    forAll (inputData) {(item:Traversable[Double], xb:Double, sd:Double, df:Int) =>

      test("For " + name + " " + item) {
    	function(item) shouldBe (xb, sd, df)
      }

    }
  }
}
