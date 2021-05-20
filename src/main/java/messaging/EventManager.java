package messaging;

import com.binance.api.client.domain.market.AggTrade;

import scheduling.ScheduleEvent;
import source.CachedOrderBook;

public class EventManager {
    private EventBroker<CachedOrderBook> orderBookBroker = new EventBroker<>();
    private EventBroker<ScheduleEvent> scheduleQueue = new EventBroker<>();
    private EventBroker<AggTrade>aggTradesBroker = new EventBroker<>();

//    public void addListener(messaging.EventListener toAdd) {
//        eventListenerList.add(toAdd);
//    }

//    public void publish(CachedOrderBook orderBook) {
//        for (messaging.EventListener listener: Main.eventListenerList) {
//            if (listener instanceof algo.AnalyticalManager) {
//                listener.handleEvent(orderBook);
//            }
//        }
//    }

    public void publish(CachedOrderBook orderbook) throws InterruptedException {
        orderBookBroker.addEvent(orderbook);
        orderBookBroker.broadcast();
    }

    public void publish(ScheduleEvent timer) {

    }

    public void addListener(EventListener listener) {
        aggTradesBroker.addListener(listener);
        orderBookBroker.addListener(listener);
        scheduleQueue.addListener(listener);
    }
}
