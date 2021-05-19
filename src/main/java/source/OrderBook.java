package source;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;

public class OrderBook {
    private Map<String, NavigableMap<BigDecimal, BigDecimal>> orderBook = new HashMap<>();

    public NavigableMap<BigDecimal, BigDecimal> getAsks() {
        return orderBook.get("ASKS");
    }

    public NavigableMap<BigDecimal, BigDecimal> getBids() {
        return orderBook.get("BIDS");
    }

    public void put(String string, NavigableMap<BigDecimal, BigDecimal> map) {
        orderBook.put(string, map);
    }

    /**
     * @return the best ask in the order book
     */
    public Map.Entry<BigDecimal, BigDecimal> getBestAsk() {
        return getAsks().lastEntry();
    }

    /**
     * @return the best bid in the order book
     */
    public Map.Entry<BigDecimal, BigDecimal> getBestBid() {
        return getBids().firstEntry();
    }

    /**
     * @return a depth cache, containing two keys (ASKs and BIDs), and for each, an ordered list of book entries.
     */
    public Map<String, NavigableMap<BigDecimal, BigDecimal>> getOrderBookCache() {
        return orderBook;
    }
}
