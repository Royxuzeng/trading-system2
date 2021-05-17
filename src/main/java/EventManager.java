import java.util.ArrayList;
import java.util.List;

import com.binance.api.client.domain.market.OrderBook;

public class EventManager {


//    public void addListener(EventListener toAdd) {
//        eventListenerList.add(toAdd);
//    }

    public void publish(OrderBook orderBook) {
        for (EventListener listener: Main.eventListenerList) {
            if (listener instanceof AnalyticalManager) {
                listener.handleEvent(orderBook);
            }
        }
    }

    public void publish(ScheduleEvent timer) {

    }
}
