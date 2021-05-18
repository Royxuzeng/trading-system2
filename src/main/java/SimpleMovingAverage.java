import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.binance.api.client.domain.market.AggTrade;

public class SimpleMovingAverage extends AnalyticalManager {
    private int period;
    private Long lastAggTradeId = 0L;
    private List<List<Double>> recentPrices;
    private int pointer = 0;

    public SimpleMovingAverage(int period) {
        super();
        this.period = period;
        recentPrices = new ArrayList<>(Collections.nCopies(period, new ArrayList<Double>()));
    }

    public void updateRecentPrices() throws InterruptedException {
        while(aggTradesCache.isEmpty()) {
            Thread.sleep(500);
        }
        List<Double> mostRecentPrices = new ArrayList<>();
        Map<Long, AggTrade> recentAggTrades = aggTradesCache.tailMap(lastAggTradeId);
        lastAggTradeId = aggTradesCache.lastKey();
        recentAggTrades.entrySet().forEach(entry -> mostRecentPrices.add(Double.valueOf(entry.getValue().getPrice())));
        recentPrices.set(pointer, mostRecentPrices);
        if (pointer >= period - 1) {
            pointer = 0;
        } else {
            pointer++;
        }
    }

    public Double getMovingAverage() {
        Double totalPrice = 0.0;
        int count = 0;
        for (List<Double> priceList : recentPrices) {
            if (priceList.isEmpty()) {
                break;
            }
            for (Double price : priceList) {
                totalPrice += price;
                count++;
            }
        }
        double movingAverage = totalPrice / count;
        return movingAverage;
    }
    
    @Override
    public void handleEvent(ScheduleEvent timer) throws InterruptedException {
        updateRecentPrices();
        System.out.println(getMovingAverage());
    }
}
