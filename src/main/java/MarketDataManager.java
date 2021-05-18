import com.binance.api.client.domain.market.OrderBook;
import com.binance.api.client.domain.market.OrderBookEntry;

public class MarketDataManager {
    public String symbol;
    public BinanceConnector binanceConnector;
    public EventManager eventManager;

    public MarketDataManager(String symbol, EventManager eventManager) {
        this.symbol = symbol;
        this.eventManager = eventManager;
        binanceConnector = new BinanceConnector(symbol);
    }

    public void subscribeOrderBook() {
//        OrderBook orderBook = Main.binanceConnector.getOrderBook(symbol);
//        String price = orderBookEntry.getPrice();
//        String qty = orderBookEntry.getQty();
//        Data.orderBookList.add(orderBook);

//        Main.eventManager.publish(orderBook);

        binanceConnector.startDepthEventStreaming(symbol);
    }

    public void subscribeTrades(String symbol) {

    }
}
