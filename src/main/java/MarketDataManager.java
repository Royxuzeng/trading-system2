import com.binance.api.client.domain.market.OrderBook;
import com.binance.api.client.domain.market.OrderBookEntry;

public class MarketDataManager {
    public void subscribeOrderBook(String symbol) {
        OrderBook orderBook = Main.binanceConnector.getOrderBook(symbol);
//        String price = orderBookEntry.getPrice();
//        String qty = orderBookEntry.getQty();
        Data.orderBookList.add(orderBook);

        Main.eventManager.publish(orderBook);
    }

    public void subscribeTrades(String symbol) {

    }
}
