public class Main {
//    public static MarketDataManager marketDataManager;
//    public static SchedulerManager schedulerManager;
//    public static EventManager eventManager;
//    public static BinanceConnector binanceConnector;
//    public static List<EventListener> eventListenerList;


    public static void main(String args[]) {
//        marketDataManager = new MarketDataManager();
//        schedulerManager = new SchedulerManager();
//        eventManager = new EventManager();
//        binanceConnector = new BinanceConnector();
//        eventListenerList = new ArrayList<>();
//
//        eventListenerList.add(new AnalyticalManager());

        EventManager eventManager = new EventManager();

        MarketDataManager marketDataManager = new MarketDataManager("ETHBTC", eventManager);

        marketDataManager.subscribeOrderBook();
        AnalyticalManager smaFive = new SimpleMovingAverage(5);
        eventManager.addListener(smaFive);


//        marketDataManager.subscribeOrderBook(symbol);
//        schedulerManager.periodicCallBack(500);
    }
}
