package uk.org.winder.statistics

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import scala.math.sqrt

object Futures {
	def meanStdDev(data:Traversable[Double]): (Double, Double, Int) = {
		val count = Future { data.size }
		val sumv = Future { data.sum }
		val sumsq = Future { data.map(x => x * x).sum }
		val n = Await.result(count, Duration.Inf)
		n match {
			case 0 => (Double.NaN, Double.NaN, -1)
			case 1 => (data.head, Double.NaN, 0)
			case _ => {
				val xb = Await.result(sumv, Duration.Inf) / n
				val df = n - 1
				(xb, sqrt((Await.result(sumsq, Duration.Inf) - n * xb * xb) / df), df)
			}
		}
	}
}
