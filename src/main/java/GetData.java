import java.util.List;

import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.BinanceApiWebSocketClient;
import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.domain.event.AggTradeEvent;
import com.binance.api.client.domain.event.DepthEvent;
import com.binance.api.client.domain.market.AggTrade;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.binance.api.client.domain.market.OrderBook;
import com.binance.api.client.domain.market.OrderBookEntry;
import com.binance.api.client.domain.market.TickerPrice;
import com.binance.api.client.domain.market.TickerStatistics;


public class GetData{

    public static void main (String[] args) {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance("API-KEY", "SECRET");
        BinanceApiRestClient client = factory.newRestClient();

        printLatestPrice(client);
        printAllPrices(client);
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