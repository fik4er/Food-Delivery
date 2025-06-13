import java.io.*;
import java.util.*;
import java.time.LocalTime;

public class Payment {
    private static final LocalTime PEAK_HOUR_START_1 = LocalTime.of(12, 0);
    private static final LocalTime PEAK_HOUR_END_1 = LocalTime.of(14, 0);
    private static final LocalTime PEAK_HOUR_START_2 = LocalTime.of(18, 0);
    private static final LocalTime PEAK_HOUR_END_2 = LocalTime.of(20, 0);
    private static final double PEAK_MULTIPLIER = 1.2;
    private static final double SERVICE_FEE = 15.0;
    private static final double NDS = 0.2;

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
            System.out.println("Подтверждение: " + cardDetails);
            boolean cardConfirmed = false;
            while (!cardConfirmed) {
                System.out.print("Верно? (Y/N): ");
                String confirmChoice = scanner.nextLine();
                if (confirmChoice.equalsIgnoreCase("Y")) {
                    cardConfirmed = true;
                } else if (confirmChoice.equalsIgnoreCase("N")) {
                    System.out.println("Выбор карты отменен.");
                    return; // Возвращаемся к началу программы
                } else {
                    System.out.println("Неверный ввод. Попробуйте снова.");
                }
            }
        }

        double finalAmount = calculateFinalAmount(orderItems, LocalTime.now());
        printOrderSummary(orderItems, finalAmount);
        boolean paymentConfirmed = false;
        while (!paymentConfirmed) {
            System.out.print("Подтвердить оплату? (Y/N): ");
            String finalConfirmChoice = scanner.nextLine();
            if (finalConfirmChoice.equalsIgnoreCase("Y")) {
                System.out.println("Оплата подтверждена!");
                paymentConfirmed = true;
            } else if (finalConfirmChoice.equalsIgnoreCase("N")) {
                System.out.println("Оплата отменена.");
                paymentConfirmed = true;
            } else {
                System.out.println("Неверный ввод. Попробуйте снова.");
            }
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
    static double calculateFinalAmount(List<FoodItem> items, LocalTime currentTime) {
        double finalAmount = 0;
        for (FoodItem item : items) {
            finalAmount += item.getPrice();
        }
        finalAmount *= (1 + NDS);
        boolean isPeakHour = (currentTime.isAfter(PEAK_HOUR_START_1) && currentTime.isBefore(PEAK_HOUR_END_1)) ||
                (currentTime.isAfter(PEAK_HOUR_START_2) && currentTime.isBefore(PEAK_HOUR_END_2));
        if (isPeakHour) {
            finalAmount *= PEAK_MULTIPLIER;
        }
        return finalAmount + SERVICE_FEE;
    }

    static void printOrderSummary(List<FoodItem> items, double finalAmount) {
        System.out.println("\nЗаказанные товары:");
        double subtotal = 0;
        for (FoodItem item : items) {
            System.out.println("- " + item.getName() + ": " + item.getPrice() + " руб.");
            subtotal += item.getPrice();
        }

        LocalTime currentTime = LocalTime.now();
        boolean isPeakHour = (currentTime.isAfter(PEAK_HOUR_START_1) && currentTime.isBefore(PEAK_HOUR_END_1)) ||
                (currentTime.isAfter(PEAK_HOUR_START_2) && currentTime.isBefore(PEAK_HOUR_END_2));

        System.out.println("НДС (20%): " + subtotal * NDS + " руб.");
        if (isPeakHour) {
            System.out.println("Сейчас повышенный спрос, поэтому цена выше.");
        }
        System.out.println("Сервисный сбор: " + SERVICE_FEE + " руб.");
        System.out.println("Итоговая сумма: " + finalAmount + " руб.");
    }
    public static double getMultiplier(){return PEAK_MULTIPLIER;}
    public static double getServiceFee(){return SERVICE_FEE;}
    public static double getNds(){return NDS;}
}
