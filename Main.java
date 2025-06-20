import java.util.*;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        OrderManagement oms = new OrderManagement();

        // Simulate trading window
        oms.sendLogon();

        // Send 105 orders quickly
        for (int i = 1; i <= 105; i++) {
            OrderRequest req = new OrderRequest(i, 100.0 + i, 10, 'B', i, RequestType.New);
            oms.onData(req);
        }

        // Modify order 101
        oms.onData(new OrderRequest(101, 110.0, 15, 'B', 101, RequestType.Modify));

        // Cancel order 102
        oms.onData(new OrderRequest(102, 0, 0, 'B', 102, RequestType.Cancel));

        Thread.sleep(3000); // wait for queue processing

        // Simulate response
        oms.onData(new OrderResponse(101, ResponseType.Accept));
        oms.sendLogout();
    }
}