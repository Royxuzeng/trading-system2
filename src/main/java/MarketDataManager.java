import com.binance.api.client.domain.market.OrderBook;
import com.binance.api.client.domain.market.OrderBookEntry;

public class MarketDataManager {
    BinanceConnector binanceConnector;
    EventManager eventManager;

    public MarketDataManager() {
        this.binanceConnector = new BinanceConnector();
        this.eventManager = new EventManager();
        eventManager.addListener(new AnalyticalManager());
    }

    public void subscribeOrderBook(String symbol) {
        OrderBook orderBook = binanceConnector.getOrderBook(symbol);
//        String price = orderBookEntry.getPrice();
//        String qty = orderBookEntry.getQty();
        Data.orderBookList.add(orderBook);

        eventManager.publish(orderBook);
    }

    public void subscribeTrades(String symbol) {

    }
}
