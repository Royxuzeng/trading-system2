package algo;

import java.math.BigDecimal;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.quartz.SchedulerException;

import messaging.EventBroker;
import messaging.EventManager;
import scheduling.ScheduleEvent;
import scheduling.SchedulerManager;
import source.CachedOrderBook;

public class SimpleMovingAverage implements Runnable {
    public DescriptiveStatistics sma1;
    public DescriptiveStatistics sma2;
    public EventManager eventManager;
    public SchedulerManager schedulerManager;
    public CachedOrderBook cachedOrderBook = null;
    public int interval1;
    public int interval2;
    public int windowSize1;
    public int windowSize2;
    public RiskWatcher riskWatcher;
    private int count = 0;
    private double mostRecentSma1 = 0;
    private double mostRecentSma2 = 0;


    // interval used in trigger interval in periodic callback
    public SimpleMovingAverage(int interval1, int interval2, EventManager eventManager,
                               SchedulerManager schedulerManager,
                               int windowSize1, int windowSize2) {
        this.interval1 = interval1;
        this.interval2 = interval2;
        this.sma1 = new DescriptiveStatistics(windowSize1);
        this.sma2 = new DescriptiveStatistics(windowSize2);
        this.eventManager = eventManager;
        this.schedulerManager = schedulerManager;
        this.windowSize1 = windowSize1;
        this.windowSize2 = windowSize2;
        this.riskWatcher = new RiskWatcher(sma1, sma2);
    }

    public void handleEvent(CachedOrderBook orderBook) {
        cachedOrderBook = orderBook;
    }

    public void handleEvent(ScheduleEvent scheduledEvent) {
        computeSMA(scheduledEvent);
    }

    // sma1 is descriptiveStatistics with a window size of 10.
    // Each value is a weighted average computed from an orderbook.
    // sma1Data  is descriptiveStatistics with a window size of 10.
    // Each value is the mean of sma1. the most recent value is used to compare
    // with sma2 to generate signal to buy or sell
    private void computeSMA(ScheduleEvent scheduledEvent) {
        String tag = scheduledEvent.getTag();
        if (count == 40) {
            riskWatcher.printSma1Data();
            riskWatcher.printSma2Data();
            count = 0;
        }
        if (cachedOrderBook == null) {
            return;
        } else if (tag.equals("sma1")) {
            sma1.addValue(computeWeightedAverage(cachedOrderBook));
            mostRecentSma1 = sma1.getMean();
            riskWatcher.sma1Data.addValue(mostRecentSma1);
            System.out.println(tag + ": " + String.format("%.10f", mostRecentSma1));
        } else if (tag.equals("sma2")) {
            sma2.addValue(computeWeightedAverage(cachedOrderBook));
            mostRecentSma2 = sma2.getMean();
            riskWatcher.sma2Data.addValue(mostRecentSma2);
            System.out.println(tag + ": " + String.format("%.10f", mostRecentSma2));
        }
        riskWatcher.handleEvent(mostRecentSma1, mostRecentSma2);
        count++;
    }

    private double computeWeightedAverage(CachedOrderBook orderBook) {
        Map.Entry<BigDecimal, BigDecimal> bestBid = orderBook.getBestBid();
        Map.Entry<BigDecimal, BigDecimal> bestAsk = orderBook.getBestAsk();

        double bestBidPrice = bestBid.getKey().doubleValue();
        double bestAskPrice = bestAsk.getKey().doubleValue();
        double bestBidQuantity = bestBid.getValue().doubleValue();
        double bestAskQuantity = bestAsk.getValue().doubleValue();

        double weightedAverage = (bestBidQuantity * bestBidPrice +
                bestAskQuantity * bestAskPrice) / (bestBidQuantity + bestAskQuantity);
        return weightedAverage;
    }

    @Override
    public void run() {
        try {
            this.schedulerManager.periodicCallBack(interval1, "sma1");
            this.schedulerManager.periodicCallBack(interval2, "sma2");
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

        EventBroker orderBookEventBroker = this.eventManager.getOrderBookEventBroker();
        EventBroker scheduledEventBroker = this.eventManager.getScheduledEventEventBroker();
        while (true) {
            try {
                handleEvent((CachedOrderBook) orderBookEventBroker.takeEvent());
                handleEvent((ScheduleEvent) scheduledEventBroker.takeEvent());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
