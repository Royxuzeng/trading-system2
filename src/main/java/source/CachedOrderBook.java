package source;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;

public class CachedOrderBook {

    // key is the "ASKS" or "BIDS". ask is to sell. bid is to buy.
    // NavigableMap: https://www.geeksforgeeks.org/navigablemap-interface-in-java-with-example/
    // for the map, first value is price, second value is quantity. sorted from big to small
    private Map<String, NavigableMap<BigDecimal, BigDecimal>> orderBook;

    public CachedOrderBook() {
        this.orderBook = new HashMap<>();
    }

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
    // the lowest (or best) price someone is willing to sell
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
