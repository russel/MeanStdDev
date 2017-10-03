package uk.org.winder.statistics

import scala.collection.Traversable
import scala.math.sqrt

object Sequential {
	def meanStdDev(data: Traversable[Double]): (Double, Double, Int) = {
		val n = data.size
		n match {
			case 0 => (Double.NaN, Double.NaN, -1)
			case 1 => (data.head, Double.NaN, 0)
			case _ => {
				val xb = data.sum / n
				val df = n - 1
				val sumsq = data.map(x => x * x).sum
				(xb, sqrt((sumsq - n * xb * xb) / df), df)
			}
		}
	}
}
