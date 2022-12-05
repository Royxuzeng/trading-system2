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

    @Override
    public void run() {
        binanceConnector.startDepthEventStreaming(symbol, eventManager);
    }
}
