package source;

import java.io.*;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.domain.market.OrderBook;
import com.binance.api.client.domain.market.OrderBookEntry;
import com.binance.api.client.domain.market.TickerPrice;
import com.binance.api.client.domain.market.TickerStatistics;
import com.binance.api.client.impl.BinanceApiServiceGenerator;

import messaging.EventManager;


public class BinanceConnector {

    private long orderBookLastUpdateId;
    private CachedOrderBook orderBookCache;

    public BinanceConnector(String symbol) throws IOException {
        initializeDepthCache(symbol);
    }

    /**
     * Initializes the depth cache by using the REST API.
     */
    public void initializeDepthCache(String symbol) throws IOException {
        File file = new File("secret.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;
        while ((st = br.readLine()) != null);
        String apikey = "UyYr92Z8wqKAyurjUjDq7evelP2OpwH4AqMPa4ca85JQRJvBZFwIf0JRdOr2tA3A";

        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(apikey, st);
        BinanceApiRestClient client = factory.newRestClient();
        // limit is 10 means orderbook has 10 bid data and 10 ask data
        OrderBook orderBook = client.getOrderBook(symbol.toUpperCase(), 10);

        this.orderBookCache = new CachedOrderBook();
        this.orderBookLastUpdateId = orderBook.getLastUpdateId();

        NavigableMap<BigDecimal, BigDecimal> asks = new TreeMap<>(Comparator.reverseOrder());
        for (OrderBookEntry ask : orderBook.getAsks()) {
            asks.put(new BigDecimal(ask.getPrice()), new BigDecimal(ask.getQty()));
        }
        orderBookCache.put("ASKS", asks);

        NavigableMap<BigDecimal, BigDecimal> bids = new TreeMap<>(Comparator.reverseOrder());
        for (OrderBookEntry bid : orderBook.getBids()) {
            bids.put(new BigDecimal(bid.getPrice()), new BigDecimal(bid.getQty()));
        }
        orderBookCache.put("BIDS", bids);
    }

    public void startDepthEventStreaming(String symbol, EventManager eventManager) {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
        BinanceApiWebSocketClient client = factory.newWebSocketClient();

        // Listen for changes in the order book for symbol, eg.ETH/BTC
        // second arg is called for each event received from the Binance servers
        System.out.println("here");
        client.onDepthEvent(symbol.toLowerCase(), response -> {
            System.out.println("Got change");
            if (response.getFinalUpdateId() > orderBookLastUpdateId) {
                orderBookLastUpdateId = response.getFinalUpdateId();
                updateOrderBook(orderBookCache.getAsks(), response.getAsks());
                updateOrderBook(orderBookCache.getBids(), response.getBids());

                // printDepthCache();
                try {
                    eventManager.publish(orderBookCache);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void printDepthCache() {
        System.out.println(orderBookCache);
        System.out.println("ASKS:");
        orderBookCache.getAsks().entrySet().forEach(entry -> System.out.println(toDepthCacheEntryString(entry)));
        System.out.println("BIDS:");
        orderBookCache.getBids().entrySet().forEach(entry -> System.out.println(toDepthCacheEntryString(entry)));
        System.out.println("BEST ASK: " + toDepthCacheEntryString(orderBookCache.getBestAsk()));
        System.out.println("BEST BID: " + toDepthCacheEntryString(orderBookCache.getBestBid()));
    }

    private static String toDepthCacheEntryString(Map.Entry<BigDecimal, BigDecimal> depthCacheEntry) {
        return depthCacheEntry.getKey().toPlainString() + " / " + depthCacheEntry.getValue();
    }

    private void updateOrderBook(NavigableMap<BigDecimal,
            BigDecimal> orderBookCache, List<OrderBookEntry> newOrderBook) {
        for (OrderBookEntry newEntry : newOrderBook) {
            BigDecimal price = new BigDecimal(newEntry.getPrice());
            BigDecimal qty = new BigDecimal(newEntry.getQty());
            if (qty.compareTo(BigDecimal.ZERO) == 0) {
                // qty=0 means remove this entry
                orderBookCache.remove(price);
            } else {
                orderBookCache.put(price, qty);
            }
        }
    }

    public static void printLatestPrice(BinanceApiRestClient client) {
        TickerStatistics tickerStatistics = client.get24HrPriceStatistics("NEOETH");
        System.out.println(tickerStatistics.getLastPrice());
    }

    public static void printAllPrices(BinanceApiRestClient client) {
        List<TickerPrice> allPrices = client.getAllPrices();
        System.out.println(allPrices);
    }


}