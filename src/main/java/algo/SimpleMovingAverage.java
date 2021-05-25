package algo;

import java.math.BigDecimal;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.quartz.SchedulerException;

import com.binance.api.client.domain.event.AggTradeEvent;
import com.binance.api.client.domain.event.DepthEvent;

import messaging.EventBroker;
import messaging.EventListener;
import messaging.EventManager;
import scheduling.ScheduleEvent;
import scheduling.SchedulerManager;
import source.CachedOrderBook;

public class SimpleMovingAverage implements Runnable, EventListener {
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


    @Override
    public void handleEvent(CachedOrderBook orderBook) {
        cachedOrderBook = orderBook;
    }

    @Override
    public void handleEvent(AggTradeEvent aggTrade) {

    }

    @Override
    public void handleEvent(DepthEvent event) {

    }

    @Override
    public void handleEvent(ScheduleEvent scheduledEvent) {
        computeSMA(scheduledEvent);
    }

    private void computeSMA(ScheduleEvent scheduledEvent) {
        String tag = scheduledEvent.getTag();
        if (cachedOrderBook == null) {
            return;
        } else if (tag.equals("sma1")) {
            if (sma1.getN() == 5) {
                riskWatcher.handleSma1();
                sma1 = new DescriptiveStatistics(windowSize1);
                riskWatcher.sma1 = sma1;
            }

            sma1.addValue(computeWeightedAverage(cachedOrderBook));
            System.out.println(tag + ": " + String.format("%.4f", sma1.getMean()));
        } else if (tag.equals("sma2")) {
            if (sma2.getN() == 5) {
                riskWatcher.handleSma2();
                sma2 = new DescriptiveStatistics(windowSize2);
                riskWatcher.sma2 = sma2;
            }

            sma2.addValue(computeWeightedAverage(cachedOrderBook));
            System.out.println(tag + ": " + String.format("%.4f", sma2.getMean()));
        }


    }

    private double computeWeightedAverage(CachedOrderBook orderBook) {
        Map.Entry<BigDecimal, BigDecimal> bestBid = orderBook.getBestBid();
        Map.Entry<BigDecimal, BigDecimal> bestAsk = orderBook.getBestAsk();

        double bestBidPrice = bestBid.getValue().doubleValue();
        double bestAskPrice = bestAsk.getValue().doubleValue();

        double bestBidQuantity = bestBid.getKey().doubleValue();
        double bestAskQuantity = bestAsk.getKey().doubleValue();


        double totalQuantity = (bestBidQuantity * bestBidPrice +
                bestAskQuantity + bestAskPrice) / (bestBidQuantity + bestAskQuantity);

        return totalQuantity;
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
                ScheduleEvent scheduleEvent = (ScheduleEvent) scheduledEventBroker.takeEvent();
                handleEvent(scheduleEvent);
//                handleEvent((ScheduleEvent) scheduledEventBroker.takeEvent());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
