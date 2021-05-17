import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static MarketDataManager marketDataManager;
    public static SchedulerManager schedulerManager;
    public static EventManager eventManager;
    public static BinanceConnector binanceConnector;
    public static List<EventListener> eventListenerList;


    public static void main(String args[]) {
        marketDataManager = new MarketDataManager();
        schedulerManager = new SchedulerManager();
        eventManager = new EventManager();
        binanceConnector = new BinanceConnector();
        eventListenerList = new ArrayList<>();

        eventListenerList.add(new AnalyticalManager());

        Scanner scanner = new Scanner(System.in);
        String symbol = scanner.next();

        marketDataManager.subscribeOrderBook(symbol);
        schedulerManager.periodicCallBack(500);
    }
}
