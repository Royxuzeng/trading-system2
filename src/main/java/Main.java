import algo.AnalyticalManager;
import algo.SimpleMovingAverage;
import messaging.EventManager;
import source.MarketDataManager;

public class Main {
//    public static source.MarketDataManager marketDataManager;
//    public static source.SchedulerManager schedulerManager;
//    public static messaging.EventManager eventManager;
//    public static source.BinanceConnector binanceConnector;
//    public static List<messaging.EventListener> eventListenerList;


    public static void main(String args[]) {
//        marketDataManager = new source.MarketDataManager();
//        schedulerManager = new source.SchedulerManager();
//        eventManager = new messaging.EventManager();
//        binanceConnector = new source.BinanceConnector();
//        eventListenerList = new ArrayList<>();
//
//        eventListenerList.add(new AnalyticalManager());

        EventManager eventManager = new EventManager();

        MarketDataManager marketDataManager = new MarketDataManager("ETHBTC", eventManager);

        marketDataManager.subscribeOrderBook();
        AnalyticalManager analyticalManager = new SimpleMovingAverage(5);
        eventManager.addListener(analyticalManager);


//        marketDataManager.subscribeOrderBook(symbol);
//        schedulerManager.periodicCallBack(500);
    }
}
