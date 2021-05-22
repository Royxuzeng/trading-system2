package messaging;//import com.binance.api.client.domain.market.CachedOrderBook;


import com.binance.api.client.domain.event.AggTradeEvent;
import com.binance.api.client.domain.event.DepthEvent;
import com.binance.api.client.domain.market.OrderBook;

import scheduling.ScheduleEvent;
import source.CachedOrderBook;

public interface EventListener {
    void handleEvent(ScheduleEvent timer) throws InterruptedException;
    void handleEvent(CachedOrderBook orderBook);
    void handleEvent(AggTradeEvent aggTrade);

    void handleEvent(DepthEvent event);
}
