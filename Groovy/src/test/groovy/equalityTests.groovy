import spock.lang.Specification
import spock.lang.Unroll

import static java.lang.Math.abs

class equalityTests extends Specification {

  static realEqual(Number x, Number y, Number e = 0.000000001) {
      abs(x - y) < e
    }

    @Unroll
    def 'realEqual does what it needs to'() {
      expect:
      realEqual(a, b) == result
      where:
      a | b | result
      1 | 1 | true
      1 | 1.0 | true
      1.0 | 1 | true
      1.0 | 1.0 | true
      1 | 2 | false
      1 | 2.0 | false
      1.0 | 2 | false
      1 | 2.0 | false
      2 | 1 | false
      2 | 1.0 | false
      2.0 | 1 | false
      2 | 1.0 | false
    }

    static areTriplesEqual(List<Number> x, List<Number> y, Number e = 0.000000001) {
      assert x.size() == 3 && y.size() == 3
      assert x[0] instanceof Number && x[1] instanceof Number
      assert x[2] instanceof Byte || x[2] instanceof Short || x[2] instanceof Integer || x[2] instanceof Long
      assert y[0] instanceof Number && y[1] instanceof Number
      assert y[2] instanceof Byte || y[2] instanceof Short || y[2] instanceof Integer || y[2] instanceof Long
      abs(x[0] - y[0]) < e && abs(x[1] - y[1]) < e && x[2] == y[2]
    }

    def 'the third entry in the list must be an integer'() {
      when:
       areTriplesEqual([1, 1, 1], [1, 1, 1.0])
      then:
       thrown AssertionError
      when:
       areTriplesEqual([1, 1, 1.0], [1, 1, 1])
      then:
       thrown AssertionError
    }

    @Unroll
    def 'realEqualTriple does what it needs to'() {
      expect:
      areTriplesEqual([a, b, c], [x, y, z]) == result
      where:
      a | b | c | x | y | z | result
      1 | 1 | 1 | 1 |1 | 1 |  true
      1 | 1 | 1 | 1 |1.0 | 1 |  true
      1 | 1 | 1 | 1.0 | 1 | 1 | true
      1 | 1.0 | 1 | 1 |1 | 1 |  true
      1.0 | 1 | 1 | 1 | 1 | 1 | true
      1.0 | 1.0 | 1 | 1.0 | 1.0 | 1 | true
      1 | 1 | 2 | 1 | 1 | 1 | false
      1 | 2 | 1 | 1 | 1 | 1 | false
      2 | 1 | 1 | 1 | 1 | 1 | false
      1 | 1 | 1 | 1 | 1 | 2 | false
      1 | 1 | 1 | 1 | 2 | 1 | false
      1 | 1 | 1 | 2 | 1 | 1 | false
    }

}
