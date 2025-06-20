public class OrderRequest {
    int symbolId;
    double price;
    long qty;
    char side;
    long orderId;
    RequestType requestType;

    public OrderRequest(int symbolId, double price, long qty, char side, long orderId, RequestType requestType) {
        this.symbolId = symbolId;
        this.price = price;
        this.qty = qty;
        this.side = side;
        this.orderId = orderId;
        this.requestType = requestType;
    }
}