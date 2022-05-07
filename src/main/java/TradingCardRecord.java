import lombok.Data;
import org.joda.money.Money;

@Data
public class TradingCardRecord {
    private int quantity;
    private CardProductLine productLine;
    private String setName;
    private String cardName;
    private Money price;

    TradingCardRecord(int quantity, CardProductLine productLine, String setName, String cardName, Money price) {
        this.quantity = quantity;
        this.productLine = productLine;
        this.setName = setName;
        this.cardName = cardName;
        this.price = price;
    }

    @Override
    public String toString() {
        return quantity + " " + productLine + " card from " + setName + ": " + cardName + " sold for $" + price;
    }
}

