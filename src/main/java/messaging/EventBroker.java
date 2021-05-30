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
    private List<EventListener> listenerList = new ArrayList<>();

    public void addEvent(T event) throws InterruptedException {
        eventQueue.put(event);
    }

    public T takeEvent() throws InterruptedException {
        return eventQueue.take();
    }

//    public void takeEvent() throws InterruptedException {
//        broadcast();
//    }

    public void broadcast() throws InterruptedException {
        T event = eventQueue.take();
        sendToListeners(event);
    }


    public void sendToListeners(T event) throws InterruptedException {
        for (EventListener listener : listenerList) {
            if (event instanceof AggTradeEvent) {
                listener.handleEvent((AggTradeEvent) event);
            } else if (event instanceof CachedOrderBook) {
                listener.handleEvent((CachedOrderBook) event);
            } else {
                listener.handleEvent((ScheduleEvent) event);
            }
        }
    }

    public void addListener(EventListener listener) {
        if (!listenerList.contains(listener)) {
            listenerList.add(listener);
        } else {
            System.out.println("Listener already exists in list.");
        }
    }

    public void removeListener(EventListener listener) {
        if (listenerList.contains(listener)) {
            listenerList.remove(listener);
        } else {
            System.out.println("Listener does not exist in list");
        }
    }

}
