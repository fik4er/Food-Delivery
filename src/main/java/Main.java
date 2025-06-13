import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<FoodItem> orderItems = new ArrayList<>();
        orderItems.add(new FoodItem("Бургер", 150.0));
        orderItems.add(new FoodItem("Кокос", 50.0));
        Payment.startPaymentProcess(orderItems);
    }
}