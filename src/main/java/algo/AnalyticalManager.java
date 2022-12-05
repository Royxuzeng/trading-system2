package algo;

import java.util.NavigableMap;
import java.util.TreeMap;

import com.binance.api.client.domain.event.AggTradeEvent;
import com.binance.api.client.domain.event.DepthEvent;
import com.binance.api.client.domain.market.AggTrade;
import com.binance.api.client.domain.market.OrderBook;

import scheduling.ScheduleEvent;
import source.CachedOrderBook;

public class AnalyticalManager {
    protected NavigableMap<Long, AggTrade> aggTradesCache = new TreeMap<>();
    protected NavigableMap<Long, OrderBook> orderBookCache =
            new TreeMap<>();
    private long orderBookId = 0L;
}