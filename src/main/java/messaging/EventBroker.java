package messaging;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import com.binance.api.client.domain.event.AggTradeEvent;

import scheduling.ScheduleEvent;
import source.CachedOrderBook;

public class EventBroker<T> {
    private BlockingQueue<T> eventQueue = new ArrayBlockingQueue<>(1024);

    public void addEvent(T event) throws InterruptedException {
        eventQueue.put(event);
    }

    public T takeEvent() throws InterruptedException {
        return eventQueue.take();
    }
}
