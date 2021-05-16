import java.util.List;

import com.binance.api.client.BinanceApiAsyncRestClient;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.domain.account.Account;
import com.binance.api.client.domain.account.Trade;
import com.binance.api.client.domain.event.AggTradeEvent;
import com.binance.api.client.domain.event.DepthEvent;
import com.binance.api.client.domain.market.AggTrade;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.binance.api.client.domain.market.OrderBook;
import com.binance.api.client.domain.market.OrderBookEntry;
import com.binance.api.client.domain.market.TickerPrice;
import com.binance.api.client.domain.market.TickerStatistics;


public class BinanceConnector {

    public static void main (String[] args) {
//        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(
//                "fybfJOTBNTMbPEbhKd9CibhYDFgwmhaee8eyhqJZTEo9J9UwIXmIUJFqtppRGZCt",
//                "ZR5Jtu0dMpGvIFDcoLmboJXmn083uNWVtnw8gMDUDLX378W9GblRIMCT5brlzwwt");
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance("API-KEY", "SECRET");
        BinanceApiRestClient client = factory.newRestClient();
//        BinanceApiAsyncRestClient client1 = factory.newAsyncRestClient();

//        Account account = client.getAccount();
//        System.out.println(account.getBalances());
//        System.out.println(account.getAssetBalance("ETH").getFree());
        OrderBook orderBook = client.getOrderBook("NEOETH", 10);
        List<OrderBookEntry> asks = orderBook.getAsks();
        OrderBookEntry firstAskEntry = asks.get(0);
        System.out.println(firstAskEntry.getPrice() + " / " + firstAskEntry.getQty());
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

    public static void printLatestPrice(BinanceApiRestClient client) {
        TickerStatistics tickerStatistics = client.get24HrPriceStatistics("NEOETH");
        System.out.println(tickerStatistics.getLastPrice());
    }

    public static void printAllPrices(BinanceApiRestClient client) {
        List<TickerPrice> allPrices = client.getAllPrices();
        System.out.println(allPrices);
    }


}