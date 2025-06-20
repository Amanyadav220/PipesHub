import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class OrderManagement {
    private final int THROTTLE_LIMIT = 100;
    private final LocalTime START_TIME = LocalTime.of(10, 0);
    private final LocalTime END_TIME = LocalTime.of(13, 0);

    private final Queue<OrderRequest> orderQueue = new ConcurrentLinkedQueue<>();
    private final Map<Long, Long> sendTimes = new ConcurrentHashMap<>();
    private final AtomicInteger currentSecondCount = new AtomicInteger(0);
    private long currentSecond = System.currentTimeMillis() / 1000;

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public OrderManagement() {
        scheduler.scheduleAtFixedRate(this::dispatchOrders, 0, 1, TimeUnit.SECONDS);
    }

    public synchronized void onData(OrderRequest req) {
        LocalTime now = LocalTime.now();
        if (now.isBefore(START_TIME) || now.isAfter(END_TIME)) {
            System.out.println("Rejected (outside time): " + req.orderId);
            return;
        }

        long nowSec = System.currentTimeMillis() / 1000;
        if (nowSec != currentSecond) {
            currentSecond = nowSec;
            currentSecondCount.set(0);
        }

        if (currentSecondCount.get() < THROTTLE_LIMIT) {
            send(req);
            currentSecondCount.incrementAndGet();
        } else {
            handleModifyCancel(req);
            orderQueue.add(req);
        }
    }

    private void handleModifyCancel(OrderRequest req) {
        if (req.requestType == RequestType.Modify) {
            for (OrderRequest o : orderQueue) {
                if (o.orderId == req.orderId) {
                    o.price = req.price;
                    o.qty = req.qty;
                    break;
                }
            }
        } else if (req.requestType == RequestType.Cancel) {
            orderQueue.removeIf(o -> o.orderId == req.orderId);
        }
    }

    public synchronized void onData(OrderResponse res) {
        Long sent = sendTimes.get(res.orderId);
        long latency = System.currentTimeMillis() - (sent != null ? sent : 0);
        System.out.println("Response: " + res.orderId + " " + res.responseType + " (Latency: " + latency + "ms)");
    }

    public void send(OrderRequest req) {
        sendTimes.put(req.orderId, System.currentTimeMillis());
        System.out.println("Sent order: " + req.orderId);
    }

    public void sendLogon() {
        System.out.println("Logon sent");
    }

    public void sendLogout() {
        System.out.println("Logout sent");
    }

    private void dispatchOrders() {
        long nowSec = System.currentTimeMillis() / 1000;
        currentSecond = nowSec;
        currentSecondCount.set(0);

        while (!orderQueue.isEmpty() && currentSecondCount.get() < THROTTLE_LIMIT) {
            OrderRequest req = orderQueue.poll();
            if (req != null) {
                send(req);
                currentSecondCount.incrementAndGet();
            }
        }
    }
}