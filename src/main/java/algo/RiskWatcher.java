package algo;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.binance.api.client.domain.event.AggTradeEvent;
import com.binance.api.client.domain.event.DepthEvent;

import messaging.EventListener;
import scheduling.ScheduleEvent;
import source.CachedOrderBook;

public class RiskWatcher implements EventListener {
    DescriptiveStatistics sma1;
    DescriptiveStatistics sma2;
    private final int SMA1LARGER = 1;
    private final int SMA2LARGER = 0;
    private int state;

    public RiskWatcher(DescriptiveStatistics sma1, DescriptiveStatistics sma2) {
        this.sma1 = sma1;
        this.sma2 = sma2;
        this.state = -1;
    }

    @Override
    public void handleEvent(ScheduleEvent timer) {
        System.out.println("sma1 n " + sma1.getN());
        System.out.println("sma2 n " + sma2.getN());
    }

    public void handleEvent(double sma1Value, double sma2Value) {
        if (sma1Value == 0 || sma2Value == 0) {
            return;
        } else {
            int state1 = -1;
            if (sma1Value > sma2Value) {
                state1 = SMA1LARGER;
            } else {
                state1 = SMA2LARGER;
            }

            if (state != state1) {
                if (state1 == SMA1LARGER) {
                    System.out.println("The short term moving average crosses above the " +
                            "long term moving average, this indicates a buy signal.");
                } else {
                    System.out.println("The short term moving average crosses below the " +
                            "long term moving average, it may be a good moment to sell.");
                }
                state = state1;
            }
        }
    }

    public void handleSma1() {
        System.out.println("statistics of prices for sma1 are " +
                sma1.toString());
    }

    public void handleSma2() {
        System.out.println("statistics of prices for sma2 are " +
                sma2.toString());
    }

    @Override
    public void handleEvent(CachedOrderBook orderBook) {

    }

    @Override
    public void handleEvent(AggTradeEvent aggTrade) {

    }

    @Override
    public void handleEvent(DepthEvent event) {

    }
}
