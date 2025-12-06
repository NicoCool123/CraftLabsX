package CraftLabsX.economy.util;

import java.text.DecimalFormat;

public class NumberUtil {

    private static final DecimalFormat FORMATTER = new DecimalFormat("#,###.##");

    public static double parseAmount(String input) throws NumberFormatException {
        if (input == null || input.trim().isEmpty()) {
            throw new NumberFormatException("Input cannot be null or empty");
        }

        String lower = input.toLowerCase().trim();
        double multiplier = 1.0;

        if (lower.endsWith("k")) {
            multiplier = 1_000;
            lower = lower.substring(0, lower.length() - 1);
        } else if (lower.endsWith("m")) {
            multiplier = 1_000_000;
            lower = lower.substring(0, lower.length() - 1);
        } else if (lower.endsWith("b")) {
            multiplier = 1_000_000_000;
            lower = lower.substring(0, lower.length() - 1);
        }

        if (!lower.matches("^\\d+(\\.\\d+)?$")) {
            throw new NumberFormatException("Invalid number format: " + input);
        }

        return Double.parseDouble(lower) * multiplier;
    }

    public static String formatAmount(double amount) {
        if (amount >= 1_000_000_000) {
            return String.format("%.1fb", amount / 1_000_000_000);
        } else if (amount >= 1_000_000) {
            return String.format("%.1fm", amount / 1_000_000);
        } else if (amount >= 1_000) {
            return String.format("%.1fk", amount / 1_000);
        } else {
            return FORMATTER.format(amount);
        }
    }
}
