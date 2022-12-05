import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.quartz.SchedulerException;

import algo.SimpleMovingAverage;
import messaging.EventManager;
import scheduling.SchedulerManager;
import source.MarketDataManager;

public class Main {
    public static void main(String args[]) throws SchedulerException, IOException {
        EventManager eventManager = new EventManager();
        MarketDataManager marketDataManager = new MarketDataManager("ETHBTC", eventManager);
        SchedulerManager schedulerManager = new SchedulerManager(eventManager);
        SimpleMovingAverage sma = new SimpleMovingAverage(500, 500, eventManager,
                                    schedulerManager, 10, 20);
        ScheduledExecutorService eS = Executors.newScheduledThreadPool(2);

        eS.execute(marketDataManager);
        eS.execute(sma);
//        AnalyticalManager analyticalManager = new SimpleMovingAverage(5);
//        eventManager.addListener(analyticalManager);


//        marketDataManager.subscribeOrderBook(symbol);
//        schedulerManager.periodicCallBack(500);
    }
}
