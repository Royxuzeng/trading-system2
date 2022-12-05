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

        // 2 tasks
        // marketDataManager gets order book from binance and puts it into blocking queue
        // sma takes order book from blocking queue
        ScheduledExecutorService eS = Executors.newScheduledThreadPool(2);
        eS.execute(marketDataManager);
        eS.execute(sma);
    }
}
