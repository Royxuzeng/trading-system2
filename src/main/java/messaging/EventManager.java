package messaging;

import com.binance.api.client.domain.market.AggTrade;

import scheduling.ScheduleEvent;
import source.CachedOrderBook;

public class EventManager {
    private EventBroker<CachedOrderBook> orderBookBroker = new EventBroker<>();
    private EventBroker<ScheduleEvent> scheduleEventBroker = new EventBroker<>();
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
//        orderBookBroker.broadcast();
    }

    public void publish(ScheduleEvent timer) throws InterruptedException {
        scheduleEventBroker.addEvent(timer);
//        scheduleEventBroker.broadcast();
    }

    public void addListener(EventListener listener) {
        aggTradesBroker.addListener(listener);
        orderBookBroker.addListener(listener);
        scheduleEventBroker.addListener(listener);
    }


    public EventBroker getOrderBookEventBroker() {
        return this.orderBookBroker;
    }

    public EventBroker getScheduledEventEventBroker() {
        return this.scheduleEventBroker;
    }
}
