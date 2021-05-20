package source;

import messaging.EventManager;

public class MarketDataManager {
    public String symbol;
    public BinanceConnector1 binanceConnector;
    public EventManager eventManager;

    public MarketDataManager(String symbol, EventManager eventManager) {
        this.symbol = symbol;
        this.eventManager = eventManager;
        binanceConnector = new BinanceConnector1(symbol);
    }

    public void subscribeOrderBook() {
//        CachedOrderBook orderBook = Main.binanceConnector.getOrderBook(symbol);
//        String price = orderBookEntry.getPrice();
//        String qty = orderBookEntry.getQty();
//        Data.orderBookList.add(orderBook);

//        Main.eventManager.publish(orderBook);

        binanceConnector.startDepthEventStreaming(symbol, eventManager);
    }

    public void subscribeTrades(String symbol) {

    }
}
