import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;


import com.binance.api.client.BinanceApiCallback;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.domain.event.DepthEvent;
import com.binance.api.client.domain.market.OrderBook;
import com.binance.api.client.domain.market.OrderBookEntry;
import com.binance.api.client.domain.market.TickerPrice;
import com.binance.api.client.domain.market.TickerStatistics;


public class BinanceConnector {
    private BinanceApiRestClient client;

    private static final String BIDS = "BIDS";
    private static final String ASKS = "ASKS";

    private long lastUpdateId;

    private final WsCallback wsCallback = new WsCallback();

    private Source.OrderBook orderBookCache = new Source.OrderBook();

    public BinanceConnector(String symbol) {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
        client = factory.newRestClient();
        initializeOrderBookCache(symbol);
    }

    /**
     * Initializes the depth cache by using the REST API.
     */
    public void initializeOrderBookCache(String symbol) {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
        BinanceApiRestClient client = factory.newRestClient();
        OrderBook orderBook = client.getOrderBook(symbol.toUpperCase(), 10);

        this.lastUpdateId = orderBook.getLastUpdateId();

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

    private void applyPendingDeltas(final List<DepthEvent> pendingDeltas) {
        final Consumer<DepthEvent> updateOrderBook = newEvent -> {
            if (newEvent.getFinalUpdateId() > lastUpdateId) {
                System.out.println(newEvent);
                lastUpdateId = newEvent.getFinalUpdateId();
                updateOrderBook(getAsks(), newEvent.getAsks());
                updateOrderBook(getBids(), newEvent.getBids());
                printDepthCache();
            }
        };

        final Consumer<DepthEvent> drainPending = newEvent -> {
            pendingDeltas.add(newEvent);

            // 3. Apply any deltas received on the web socket that have an update-id indicating they come
            // after the snapshot.
            pendingDeltas.stream()
                    .filter(
                            e -> e.getFinalUpdateId() > lastUpdateId) // Ignore any updates before the snapshot
                    .forEach(updateOrderBook);

            // 4. Start applying any newly received depth events to the depth cache.
            wsCallback.setHandler(updateOrderBook);
        };

        wsCallback.setHandler(drainPending);
    }

    public NavigableMap<BigDecimal, BigDecimal> getAsks() {
        return orderBookCache.orderBook.get(ASKS);
    }

    public NavigableMap<BigDecimal, BigDecimal> getBids() {
        return orderBookCache.orderBook.get(BIDS);
    }

    public static void main (String[] args) {
        BinanceApiWebSocketClient client = BinanceApiClientFactory.newInstance().newWebSocketClient();

    }

    public OrderBook getOrderBook(String symbol) {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance("API-KEY", "SECRET");
        BinanceApiRestClient client = factory.newRestClient();

        OrderBook orderBook = client.getOrderBook(symbol, 10);
        List<OrderBookEntry> asks = orderBook.getAsks();
        OrderBookEntry firstAskEntry = asks.get(0);
        System.out.println(firstAskEntry.getPrice() + " / " + firstAskEntry.getQty());

        return orderBook;
    }

    public void startOrderBookEventStreaming(String symbol, EventManager eventManager) {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
        BinanceApiWebSocketClient client = factory.newWebSocketClient();

        client.onDepthEvent(symbol.toLowerCase(), response -> {
            if (response.getFinalUpdateId() > lastUpdateId) {
                lastUpdateId = response.getFinalUpdateId();
                updateOrderBook(orderBookCache.getAsks(), response.getAsks());
                updateOrderBook(orderBookCache.getBids(), response.getBids());

                try {
                    eventManager.publish(orderBookCache);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateOrderBook(NavigableMap<BigDecimal,
            BigDecimal> lastOrderBookEntries, List<OrderBookEntry> orderBookDeltas) {
        for (OrderBookEntry orderBookDelta : orderBookDeltas) {
            BigDecimal price = new BigDecimal(orderBookDelta.getPrice());
            BigDecimal qty = new BigDecimal(orderBookDelta.getQty());
            if (qty.compareTo(BigDecimal.ZERO) == 0) {
                // qty=0 means remove this level
                lastOrderBookEntries.remove(price);
            } else {
                lastOrderBookEntries.put(price, qty);
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

    private final class WsCallback implements BinanceApiCallback<DepthEvent> {

        private final AtomicReference<Consumer<DepthEvent>> handler = new AtomicReference<>();

        @Override
        public void onResponse(DepthEvent depthEvent) {
            try {
                handler.get().accept(depthEvent);
            } catch (final Exception e) {
                System.err.println("Exception caught processing depth event");
                e.printStackTrace(System.err);
            }
        }

        @Override
        public void onFailure(Throwable cause) {
            System.out.println("WS connection failed. Reconnecting. cause:" + cause.getMessage());

            initialize();
        }

        private void setHandler(final Consumer<DepthEvent> handler) {
            this.handler.set(handler);
        }
    }

}