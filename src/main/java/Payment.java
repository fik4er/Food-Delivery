import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Payment {
    public static void startPaymentProcess(List<FoodItem> orderItems) {
        Scanner scanner = new Scanner(System.in);
        String paymentMethod;
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
        if (paymentMethod.equals("CARD")) {
            String cardDetails = selectCard(scanner);
        }
    }

    static String selectCard(Scanner scanner) {
        List<String> savedCards = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("saved_cards.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                savedCards.add(line);
            }
        } catch (IOException e) {
            System.out.println("Файл сохраненных карт не найден.");
        }

        if (savedCards.isEmpty()) {
            System.out.println("Сохраненных карт нет. Введите новую карту:");
            return enterNewCard(scanner);
        }

        System.out.println("Выберите сохраненную карту или введите новую:");
        System.out.println("0. Ввести новую карту");
        for (int i = 0; i < savedCards.size(); i++) {
            System.out.println((i + 1) + ". " + savedCards.get(i));
        }

        while (true) {
            System.out.print("Номер: ");
            String choice = scanner.nextLine();
            if ("0".equals(choice)) {
                return enterNewCard(scanner);
            }
            try {
                int index = Integer. parseInt(choice)- 1;
                if (index >= 0 && index < savedCards.size()) {
                    return savedCards.get(index);
                }
            } catch (NumberFormatException e) {}
            System.out.println("Неверный выбор. Попробуйте снова.");
        }
    }

    static String enterNewCard(Scanner scanner) {
        String cardNumber, expiryDate, cvv;
        do {
            System.out.print("Номер карты: ");
            cardNumber = scanner.nextLine();
            System.out.print("Дата истечения (MM/YY): ");
            expiryDate = scanner.nextLine();
            System.out.print("CVV: ");
            cvv = scanner.nextLine();
        } while (notValidCard(cardNumber, expiryDate, cvv));

        System.out.print("Сохранить карту? (Y/N): ");
        if (scanner.nextLine().equalsIgnoreCase("Y")) {
            saveCard(cardNumber + "," + expiryDate + "," + cvv);
        }
        return cardNumber + " | " + expiryDate + " | " + cvv;
    }

    static boolean notValidCard(String cardNumber, String expiryDate, String cvv) {
        if (!cardNumber.matches("\\d{16}")) {
            System.out.println("Номер карты должен быть 16 цифр.");
            return true;
        }
        if (!expiryDate.matches("(0[1-9]|1[0-2])/(\\d{2}|\\d{4})")) {
            System.out.println("Неверный формат даты.");
            return true;
        }
        if (!cvv.matches("\\d{3}")) {
            System.out.println("CVV должен быть 3 цифры.");
            return true;
        }
        return false;
    }

    static void saveCard(String cardDetails) {
        try (FileWriter writer = new FileWriter("saved_cards.txt", true)) {
            writer.write(cardDetails + "\n");
            System.out.println("Карта сохранена!");
        } catch (IOException e) {
            System.out.println("Ошибка сохранения: " + e.getMessage());
        }
    }
}
