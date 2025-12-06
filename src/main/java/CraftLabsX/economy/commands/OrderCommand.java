package CraftLabsX.economy.commands;

import CraftLabsX.economy.EconomyManager;
import CraftLabsX.economy.gui.OrderGUI;
import CraftLabsX.economy.storage.ConfigManager;
import CraftLabsX.economy.util.MessageUtil;
import CraftLabsX.economy.util.NumberUtil;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class OrderCommand implements CommandExecutor {

    private final EconomyManager economyManager;
    private final ConfigManager configManager;

    public OrderCommand(EconomyManager economyManager, ConfigManager configManager) {
        this.economyManager = economyManager;
        this.configManager = configManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cYou must be a player.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            OrderGUI.open(player, economyManager, configManager, 0);
            return true;
        }

        if (args[0].equalsIgnoreCase("create")) {
            if (args.length < 3) {
                player.sendMessage("§cUsage: /order create <amount> <price>");
                return true;
            }

            ItemStack item = player.getInventory().getItemInMainHand();
            if (item == null || item.getType().isAir()) {
                MessageUtil.send(player, configManager.getMessage("order.no_item"));
                return true;
            }

            int amount;
            try {
                amount = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                MessageUtil.send(player, configManager.getMessage("order.invalid_amount"));
                return true;
            }

            if (amount <= 0) {
                MessageUtil.send(player, configManager.getMessage("order.invalid_amount"));
                return true;
            }

            double price;
            try {
                price = NumberUtil.parseAmount(args[2]);
            } catch (NumberFormatException e) {
                MessageUtil.send(player, configManager.getMessage("errors.invalid_number"));
                return true;
            }

            if (price <= 0) {
                MessageUtil.send(player, configManager.getMessage("errors.invalid_number"));
                return true;
            }

            int currentOrders = economyManager.countPlayerOrders(player.getUniqueId());
            int maxOrders = configManager.getMaxOrdersPerPlayer();

            if (currentOrders >= maxOrders) {
                MessageUtil.send(player, configManager.getMessage("order.limit_reached"));
                return true;
            }

            long expiryHours = configManager.getOrderExpiryHours();
            boolean success = economyManager.createOrder(player, item.getType(), amount, price, expiryHours);

            if (success) {
                Map<String, String> replacements = new HashMap<>();
                replacements.put("amount", String.valueOf(amount));
                replacements.put("item", item.getType().name());
                replacements.put("price", MessageUtil.formatMoney(price));

                MessageUtil.send(player, configManager.getMessage("order.created", replacements));
            } else {
                MessageUtil.send(player, configManager.getMessage("errors.insufficient_funds"));
            }

            return true;
        }

        if (args[0].equalsIgnoreCase("my")) {
            OrderGUI.openPlayerOrders(player, economyManager, configManager);
            return true;
        }

        OrderGUI.open(player, economyManager, configManager, 0);
        return true;
    }
}
