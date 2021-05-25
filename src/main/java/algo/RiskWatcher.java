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

//    @Override
//    public void run() {
//
//    }


    public RiskWatcher(DescriptiveStatistics sma1, DescriptiveStatistics sma2) {
        this.sma1 = sma1;
        this.sma2 = sma2;
    }

    @Override
    public void handleEvent(ScheduleEvent timer) throws InterruptedException {
        System.out.println("sma1 n " + sma1.getN());
        System.out.println("sma2 n " + sma2.getN());

        if (sma1.getN() >= 5) {
            System.out.println("statistics of prices for sma1 are " +
                    sma1.toString());

        }

        if (sma2.getN() >= 10) {
            System.out.println("statistics of prices for sma2 are " +
                    sma2.toString());
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
