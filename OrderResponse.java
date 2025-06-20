public class OrderResponse {
    long orderId;
    ResponseType responseType;

    public OrderResponse(long orderId, ResponseType responseType) {
        this.orderId = orderId;
        this.responseType = responseType;
    }
}