//import com.binance.api.client.domain.market.OrderBook;

import com.binance.api.client.domain.market.OrderBook;


public interface EventListener {
    void handleEvent(OrderBook orderBook);
    void handleEvent(ScheduleEvent timer);
}
