import java.util.ArrayList;
import java.util.List;

import com.binance.api.client.domain.market.OrderBook;

public class EventManager {
    public List<EventListener> eventListenerList = new ArrayList<>();

    public void addListener(EventListener toAdd) {
        eventListenerList.add(toAdd);
    }

    public void publish(OrderBook orderBook) {

    }

    public void publish(ScheduleEvent timer) {

    }
}
