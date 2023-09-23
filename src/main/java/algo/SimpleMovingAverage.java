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
    // stores the weighted average price data points.
    // Its mean value represents the short-term simple moving average at any point in time.
    public DescriptiveStatistics sma1;

    // long-term simple moving average
    public DescriptiveStatistics sma2;
    public EventManager eventManager;
    public SchedulerManager schedulerManager;
    public CachedOrderBook cachedOrderBook = null;
    public int interval1;
    public int interval2;
    public int shortTermWindow;
    public int longTermWindow;
    public RiskWatcher riskWatcher;
    private int count = 0;
    private double mostRecentSma1 = 0;
    private double mostRecentSma2 = 0;

    // stores the mean values of sma1 data
    private DescriptiveStatistics sma1Data;
    private DescriptiveStatistics sma2Data;


    // interval used in trigger interval in periodic callback
    public SimpleMovingAverage(int interval1, int interval2, EventManager eventManager,
                               SchedulerManager schedulerManager,
                               int shortTermWindow, int longTermWindow) {
        this.interval1 = interval1;
        this.interval2 = interval2;

        // the window size represents the number of data points (or values)
        // that the instance will hold and use for statistical calculations.
        // When the number of stored values exceeds the window size, each
        // new value added will cause the oldest value to be discarded,
        // maintaining a "moving window" of the most recent windowSize1 number of values.
        this.sma1 = new DescriptiveStatistics(shortTermWindow);
        this.sma2 = new DescriptiveStatistics(longTermWindow);
        this.eventManager = eventManager;
        this.schedulerManager = schedulerManager;
        this.shortTermWindow = shortTermWindow;
        this.longTermWindow = longTermWindow;
        this.riskWatcher = new RiskWatcher();
        this.sma1Data = new DescriptiveStatistics(10);
        this.sma2Data = new DescriptiveStatistics(10);
    }

    public void updateOrderBookCache(CachedOrderBook orderBook) {
        cachedOrderBook = orderBook;
    }

    public void computeScheduledSMA(ScheduleEvent scheduledEvent) {
        computeSMA(scheduledEvent);
    }

    // sma1 is descriptiveStatistics with a window size of 10.
    // Each value is a weighted average computed from an orderbook.
    // sma1Data  is descriptiveStatistics with a window size of 10.
    // Each value is the mean of sma1. the most recent value is used to compare
    // with sma2 to generate signal to buy or sell
    private void computeSMA(ScheduleEvent scheduledEvent) {
        String tag = scheduledEvent.getSmaTypeTag();

        // The count variable is acting as a counter for the number of times the computeSMA
        // method has been called to compute the Simple Moving Averages (SMA) since the last print
        // of the SMA data.
        if (count == 40) {
            System.out.println("Statistics summary of prices for sma1: " + sma1Data.toString());
            System.out.println("Statistics summary of prices for sma2: " + sma2Data.toString());
            count = 0;
        }
        if (cachedOrderBook == null) {
            return;
        } else if (tag.equals("sma1")) {
            sma1.addValue(computeWeightedAverage(cachedOrderBook));
            mostRecentSma1 = sma1.getMean();
            sma1Data.addValue(mostRecentSma1);
            System.out.println(tag + ": " + String.format("%.10f", mostRecentSma1));
        } else if (tag.equals("sma2")) {
            sma2.addValue(computeWeightedAverage(cachedOrderBook));
            mostRecentSma2 = sma2.getMean();
            sma2Data.addValue(mostRecentSma2);
            System.out.println(tag + ": " + String.format("%.10f", mostRecentSma2));
        }
        riskWatcher.analyzeAndIssueSignal(mostRecentSma1, mostRecentSma2);
        count++;
    }

    // calculating the weighted average of the best bid and
    // best ask prices from a CachedOrderBook
    private double computeWeightedAverage(CachedOrderBook orderBook) {
        Map.Entry<BigDecimal, BigDecimal> bestBid = orderBook.getBestBid();
        Map.Entry<BigDecimal, BigDecimal> bestAsk = orderBook.getBestAsk();

        double bestBidPrice = bestBid.getKey().doubleValue();
        double bestAskPrice = bestAsk.getKey().doubleValue();
        double bestBidQuantity = bestBid.getValue().doubleValue();
        double bestAskQuantity = bestAsk.getValue().doubleValue();

        //Best Bid Price (buying price): $100
        //Best Bid Quantity: 3 BTC
        //Best Ask Price (selling price): $102
        //Best Ask Quantity: 2 BTC
        //Weighted Average = (100×3+102×2) / (3 + 2) = 100.8
        // This weighted average value represents a balanced price
        // between the best bid and the best ask prices
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
                updateOrderBookCache((CachedOrderBook) orderBookEventBroker.takeEvent());
                computeScheduledSMA((ScheduleEvent) scheduledEventBroker.takeEvent());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
