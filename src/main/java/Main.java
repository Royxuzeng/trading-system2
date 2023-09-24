import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.quartz.SchedulerException;

import algo.SMAComputeTask;
import messaging.EventManager;
import scheduling.SchedulerManager;
import source.MarketDataStreamingTask;

public class Main {
    public static void main(String args[]) throws SchedulerException, IOException {
        EventManager eventManager = new EventManager();
        MarketDataStreamingTask marketDataStreamingTask = new MarketDataStreamingTask("ETHBTC", eventManager);
        SchedulerManager schedulerManager = new SchedulerManager(eventManager);
        SMAComputeTask smaComputeTask = new SMAComputeTask(eventManager, schedulerManager, 10, 20);

        // 2 tasks
        // marketDataStreamingTask gets order book from binance and puts it into blocking queue
        // sma takes order book from blocking queue
        ExecutorService eS = Executors.newFixedThreadPool(2);
        eS.execute(marketDataStreamingTask);
        eS.execute(smaComputeTask);


        try {
            schedulerManager.scheduleEventPublisherJobWithInterval(500, "sma1");
            schedulerManager.scheduleEventPublisherJobWithInterval(500, "sma2");
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

}
