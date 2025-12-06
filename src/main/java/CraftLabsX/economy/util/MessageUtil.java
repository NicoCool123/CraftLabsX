package CraftLabsX.economy.util;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class MessageUtil {

    private static final DecimalFormat MONEY_FORMATTER = new DecimalFormat("#,###.00");

    public static String color(String message) {
        if (message == null) {
            return "";
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static void send(Player player, String message) {
        if (player != null && message != null) {
            player.sendMessage(color(message));
        }
    }

    public static String formatMoney(double amount) {
        return "$" + MONEY_FORMATTER.format(amount);
    }
}
