import java.util.*;

public class Payment {
    public static void startPaymentProcess(List<FoodItem> orderItems) {
        Scanner scanner = new Scanner(System.in);
        String paymentMethod = "";
        while (true) {
            System.out.println("Выберите способ оплаты:");
            System.out.println("1. Карта");
            System.out.println("2. Наличные");
            String choice = scanner.nextLine();
            if ("1".equals(choice)) {
                paymentMethod = "CARD";
                break;
            } else if ("2".equals(choice)) {
                paymentMethod = "CASH";
                break;
            } else {
                System.out.println("Некорректный выбор.");
            }
        }
        System.out.println(paymentMethod);
    }
}
