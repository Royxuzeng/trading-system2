package algo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.binance.api.client.domain.market.AggTrade;

import scheduling.ScheduleEvent;

public class SimpleMovingAverage {
    private int period;
    private DescriptiveStatistics descriptiveStatistics;

    public SimpleMovingAverage(int period) {
        this.period = period;
        this.descriptiveStatistics = new DescriptiveStatistics(period);
    }

    public void addValue(double value) {
        descriptiveStatistics.addValue(value);
    }

    public double getMovingAverage() {
        if (descriptiveStatistics.getN() < period) {
            return -1;
        } else {
            return descriptiveStatistics.getMean();
        }
    }
}
