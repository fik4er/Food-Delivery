import org.junit.jupiter.api.Test;
import java.time.LocalTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {
    @Test
    void testCardValidation() {
        assertFalse(Payment.notValidCard("1234567890123456", "12/25", "123"));
        assertTrue(Payment.notValidCard("123456789012345", "12/25", "123"));
        assertTrue(Payment.notValidCard("1234567890123456", "13/25", "123"));
        assertTrue(Payment.notValidCard("1234567890123456", "12/25", "12"));
    }

    @Test
    void testCalculateNormalSum() {
        List<FoodItem> items = List.of(
                new FoodItem("Бургер", 150),
                new FoodItem("Сок", 50)
        );
        LocalTime time = LocalTime.of(11,30);
        double expected = (items.get(0).getPrice() + items.get(1).getPrice()) * (1 + Payment.getNds()) + Payment.getServiceFee();
        assertEquals(expected, Payment.calculateFinalAmount(items, time));
    }

    @Test
    void testCalculateSumOnPeak(){
        List<FoodItem> items = List.of(
                new FoodItem("Пицца", 500),
                new FoodItem("Росинка", 90)
        );
        LocalTime firstPeakTime = LocalTime.of(12,30);
        LocalTime secondPeakTime = LocalTime.of(19,0);
        double expected = (items.get(0).getPrice() + items.get(1).getPrice()) * (1 + Payment.getNds()) * Payment.getMultiplier() + Payment.getServiceFee();
        assertEquals(expected, Payment.calculateFinalAmount(items, firstPeakTime));
        assertEquals(expected, Payment.calculateFinalAmount(items, secondPeakTime));
    }
}