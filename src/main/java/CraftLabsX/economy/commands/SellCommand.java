package CraftLabsX.economy.commands;

import CraftLabsX.economy.EconomyManager;
import CraftLabsX.economy.storage.ConfigManager;
import CraftLabsX.economy.util.MessageUtil;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class SellCommand implements CommandExecutor {

    private final EconomyManager economyManager;
    private final ConfigManager configManager;

    public SellCommand(EconomyManager economyManager, ConfigManager configManager) {
        this.economyManager = economyManager;
        this.configManager = configManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can sell items.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0 || args[0].equalsIgnoreCase("hand")) {
            sellHand(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("all")) {
            sellAll(player);
            return true;
        }

        sellHand(player);
        return true;
    }

    private void sellHand(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item == null || item.getType().isAir()) {
            MessageUtil.send(player, configManager.getMessage("sell.no_item"));
            return;
        }

        double basePrice = configManager.getSellPrice(item.getType());

        if (basePrice <= 0) {
            Map<String, String> replacements = new HashMap<>();
            replacements.put("item", item.getType().name());
            MessageUtil.send(player, configManager.getMessage("sell.no_price", replacements));
            return;
        }

        double multiplier = economyManager.getMultiplier(player.getUniqueId());
        int amount = item.getAmount();
        double total = basePrice * amount * multiplier;

        economyManager.deposit(player, total);
        player.getInventory().setItemInMainHand(null);

        Map<String, String> replacements = new HashMap<>();
        replacements.put("amount", String.valueOf(amount));
        replacements.put("item", item.getType().name());
        replacements.put("total", MessageUtil.formatMoney(total));
        replacements.put("base", MessageUtil.formatMoney(basePrice * amount));
        replacements.put("multiplier", String.format("%.1f", multiplier));

        MessageUtil.send(player, configManager.getMessage("sell.success", replacements));
    }

    private void sellAll(Player player) {
        double totalEarned = 0.0;
        int itemsSold = 0;
        double multiplier = economyManager.getMultiplier(player.getUniqueId());

        ItemStack[] contents = player.getInventory().getContents();

        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];

            if (item == null || item.getType().isAir()) {
                continue;
            }

            double basePrice = configManager.getSellPrice(item.getType());

            if (basePrice <= 0) {
                continue;
            }

            int amount = item.getAmount();
            double itemValue = basePrice * amount * multiplier;

            totalEarned += itemValue;
            itemsSold += amount;

            player.getInventory().setItem(i, null);
        }

        if (totalEarned == 0) {
            MessageUtil.send(player, configManager.getMessage("sell.no_sellable"));
            return;
        }

        economyManager.deposit(player, totalEarned);

        Map<String, String> replacements = new HashMap<>();
        replacements.put("total", MessageUtil.formatMoney(totalEarned));
        replacements.put("multiplier", String.format("%.1f", multiplier));

        MessageUtil.send(player, configManager.getMessage("sell.success_all", replacements));
    }
}
