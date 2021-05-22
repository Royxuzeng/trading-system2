import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;

import algo.SimpleMovingAverage;

public class SimpleMovingAverageTest {

    @Test
    public void test() {
        SimpleMovingAverage sma = new SimpleMovingAverage(3);
        sma.addValue(1);
        sma.addValue(2);
        sma.addValue(3);
        assertTrue(sma.getMovingAverage() == 2);
    }
}
