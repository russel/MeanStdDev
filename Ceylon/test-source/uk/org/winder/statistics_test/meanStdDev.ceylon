import ceylon.test{test, testExecutor, assertEquals}

import com.athaydes.specks{feature, Specification, SpecksTestExecutor}
import com.athaydes.specks.assertion{expect}
import com.athaydes.specks.matcher{sameAs}

import uk.org.winder.statistics{meanStdDev, Result}

test
void no_data_leads_to_mean_and_sd_NaN() {
    value [xb, sd, df] = meanStdDev([]);
    assert(xb.undefined);
    assert(sd.undefined);
    assertEquals(df, -1);
}

test
void one_datum_leads_to_sd_NaN() {
    value [xb, sd, df] = meanStdDev([1]);
    assertEquals(xb, 1.0);
    assert(sd.undefined);
    assertEquals(df, 0);
}

Float sqrtHalf = 0.7071067811865476;

[[Float|Integer*], Result][] testTable = [
    [[1, 2], [1.5, sqrtHalf, 1]],
    [[1, 2.0], [1.5, sqrtHalf, 1]],
    [[1.0, 2], [1.5, sqrtHalf, 1]],
    [[1.0, 2.0], [1.5, sqrtHalf, 1]],
    [[1, 1, 1], [1.0, 0.0, 2]],
    [[1, 1, 1.0], [1.0, 0.0, 2]],
    [[1, 1.0, 1], [1.0, 0.0, 2]],
    [[1.0, 1, 1], [1.0, 0.0, 2]],
    [[1.0, 1.0, 1.0], [1.0, 0.0, 2]],
    [[1.0, 2.0, 1.0, 2.0], [1.5, 0.5773502691896257, 3]]
];

test
testExecutor(`class SpecksTestExecutor`)
shared Specification meanStdDev_test() => Specification{
		feature{
			when([Float|Integer*] item, Result result) => [meanStdDev(item), result];
			examples = testTable;
			(Result actual, Result expected) => expect(actual, sameAs(expected))
		}
	};
