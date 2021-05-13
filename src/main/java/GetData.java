import java.util.List;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.domain.event.AggTradeEvent;
import com.binance.api.client.domain.event.DepthEvent;
import com.binance.api.client.domain.market.OrderBook;
import com.binance.api.client.domain.market.OrderBookEntry;


public class GetData{

    public static void main (String[] args) {
//        BinanceApiWebSocketClient client = BinanceApiClientFactory.newInstance().newWebSocketClient();
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance("API-KEY", "SECRET");
        BinanceApiRestClient client = factory.newRestClient();

        OrderBook orderBook = client.getOrderBook("NEOETH", 10);
        List<OrderBookEntry> asks = orderBook.getAsks();
        OrderBookEntry firstAskEntry = asks.get(0);
        System.out.println(firstAskEntry.getPrice() + " / " + firstAskEntry.getQty());
        //printAggTradeEvents(client, "ethbtc");

//        printOrderBookChanges(client, "btcbusd");
    }

    public static void printAggTradeEvents(BinanceApiWebSocketClient client, String symbols) {
        client.onAggTradeEvent(symbols , (AggTradeEvent response) -> {
            System.out.print("Price: ");
            System.out.print(response.getPrice());
            System.out.print(" | Quantity: ");
            System.out.println(response.getQuantity());
        });
    }

    public static void printOrderBookChanges(BinanceApiWebSocketClient client, String symbols) {
        client.onDepthEvent(symbols, (DepthEvent response) -> {
            System.out.println(response.getAsks());
        });
    }

}