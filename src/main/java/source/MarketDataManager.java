package source;

import messaging.EventManager;

import java.io.IOException;

public class MarketDataManager implements Runnable{
    public String symbol;
    public BinanceConnector binanceConnector;
    public EventManager eventManager;

    public MarketDataManager(String symbol, EventManager eventManager) throws IOException {
        this.symbol = symbol;
        this.eventManager = eventManager;
        binanceConnector = new BinanceConnector(symbol);
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

    @Override
    public void run() {
        subscribeOrderBook();
    }
}
