import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Order {
    private String orderNumber;
    private Date orderDate;
    private BigDecimal orderTotal;
//    private Map<TradingCard, Pair<BigDecimal, Integer>> cardsOrdered;

    Order(String orderNumber, Date orderDate, BigDecimal orderTotal) {
        this.orderNumber = orderNumber;
        this.orderDate = orderDate;
        this.orderTotal = orderTotal;
//        this.cardsOrdered = new HashMap<>();
    }

    void addCardToOrder(TradingCard tradingCard, BigDecimal price) {
//        cardsOrdered.put()
    }

    BigDecimal calculateTotalCardValues() {
        return new BigDecimal(0);
    }
}
