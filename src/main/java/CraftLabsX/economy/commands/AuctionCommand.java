package CraftLabsX.economy.commands;

import CraftLabsX.economy.EconomyManager;
import CraftLabsX.economy.gui.AuctionHouseGUI;
import CraftLabsX.economy.storage.ConfigManager;
import CraftLabsX.economy.util.MessageUtil;
import CraftLabsX.economy.util.NumberUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class AuctionCommand implements CommandExecutor {

    private final EconomyManager economyManager;
    private final ConfigManager configManager;

    public AuctionCommand(EconomyManager economyManager, ConfigManager configManager) {
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
            AuctionHouseGUI.open(player, economyManager, configManager, "NEWEST", 0);
            return true;
        }

        if (args[0].equalsIgnoreCase("sell")) {
            if (args.length < 2) {
                MessageUtil.send(player, configManager.getMessage("auction.invalid_price"));
                return true;
            }

            ItemStack item = player.getInventory().getItemInMainHand();
            if (item == null || item.getType().isAir()) {
                MessageUtil.send(player, configManager.getMessage("auction.no_item"));
                return true;
            }

            double price;
            try {
                price = NumberUtil.parseAmount(args[1]);
            } catch (NumberFormatException e) {
                MessageUtil.send(player, configManager.getMessage("auction.invalid_price"));
                return true;
            }

            if (price <= 0) {
                MessageUtil.send(player, configManager.getMessage("auction.invalid_price"));
                return true;
            }

            int currentAuctions = economyManager.countPlayerAuctions(player.getUniqueId());
            int maxAuctions = configManager.getMaxAuctionsPerPlayer();

            if (currentAuctions >= maxAuctions) {
                MessageUtil.send(player, configManager.getMessage("auction.limit_reached"));
                return true;
            }

            long expiryHours = configManager.getAuctionExpiryHours();
            boolean success = economyManager.createAuction(player, item.clone(), price, expiryHours);

            if (success) {
                player.getInventory().setItemInMainHand(null);

                Map<String, String> replacements = new HashMap<>();
                replacements.put("item", item.getType().name());
                replacements.put("price", MessageUtil.formatMoney(price));

                MessageUtil.send(player, configManager.getMessage("auction.listed", replacements));
            } else {
                MessageUtil.send(player, configManager.getMessage("errors.generic"));
            }

            return true;
        }

        if (args[0].equalsIgnoreCase("my")) {
            AuctionHouseGUI.openPlayerAuctions(player, economyManager, configManager);
            return true;
        }

        AuctionHouseGUI.open(player, economyManager, configManager, "NEWEST", 0);
        return true;
    }
}
