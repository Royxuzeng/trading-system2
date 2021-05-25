import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.quartz.SchedulerException;

import algo.AnalyticalManager;
import algo.SimpleMovingAverage;
import messaging.EventManager;
import scheduling.SchedulerManager;
import source.MarketDataManager;

public class Main {
//    public static source.MarketDataManager marketDataManager;
//    public static scheduling.SchedulerManager schedulerManager;
//    public static messaging.EventManager eventManager;
//    public static source.BinanceConnector binanceConnector;
//    public static List<messaging.EventListener> eventListenerList;


    public static void main(String args[]) throws SchedulerException {
//        marketDataManager = new source.MarketDataManager();
//        schedulerManager = new scheduling.SchedulerManager();
//        eventManager = new messaging.EventManager();
//        binanceConnector = new source.BinanceConnector();
//        eventListenerList = new ArrayList<>();
//
//        eventListenerList.add(new algo.AnalyticalManager());

        EventManager eventManager = new EventManager();
        MarketDataManager marketDataManager = new MarketDataManager("ETHBTC", eventManager);
        SchedulerManager schedulerManager = new SchedulerManager(eventManager);
        SimpleMovingAverage sma = new SimpleMovingAverage(500, 500, eventManager,
                schedulerManager, 20, 20);
        ScheduledExecutorService eS = Executors.newScheduledThreadPool(2);

        eS.execute(marketDataManager);
        eS.execute(sma);
//        AnalyticalManager analyticalManager = new SimpleMovingAverage(5);
//        eventManager.addListener(analyticalManager);


//        marketDataManager.subscribeOrderBook(symbol);
//        schedulerManager.periodicCallBack(500);
    }
}
