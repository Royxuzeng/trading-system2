package messaging;

import com.binance.api.client.domain.market.AggTrade;

import scheduling.ScheduleEvent;
import source.CachedOrderBook;

public class EventManager {
    private EventBroker<CachedOrderBook> orderBookBroker = new EventBroker<>();
    private EventBroker<ScheduleEvent> scheduleEventBroker = new EventBroker<>();
    private EventBroker<AggTrade>aggTradesBroker = new EventBroker<>();

    // put orderbook to eventQueue.
    public void publish(CachedOrderBook orderbook) throws InterruptedException {
        orderBookBroker.addEvent(orderbook);
    }

    // timer executes it
    // put schedule event to eventQueue (which is type BlockingQueue)
    public void publish(ScheduleEvent timer) throws InterruptedException {
        scheduleEventBroker.addEvent(timer);
//        scheduleEventBroker.broadcast();
    }

    public EventBroker getOrderBookEventBroker() {
        return this.orderBookBroker;
    }

    public EventBroker getScheduledEventEventBroker() {
        return this.scheduleEventBroker;
    }
}
