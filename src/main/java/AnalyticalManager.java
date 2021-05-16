import java.util.Scanner;

import com.binance.api.client.domain.market.OrderBook;


//import com.binance.api.client.domain.market.OrderBook;

public class AnalyticalManager implements EventListener{
    @Override
    public void handleEvent(OrderBook orderBook) {

    }

    @Override
    public void handleEvent(ScheduleEvent timer) {

    }

    public static void main(String args[]) {
        Scanner scanner = new Scanner(System.in);
        String symbol = scanner.next();

        MarketDataManager marketDataManager = new MarketDataManager();


    }
}
