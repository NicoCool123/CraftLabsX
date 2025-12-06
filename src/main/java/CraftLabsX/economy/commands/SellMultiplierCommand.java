package CraftLabsX.economy.commands;

import CraftLabsX.economy.EconomyManager;
import CraftLabsX.economy.gui.SellMultiplierGUI;
import CraftLabsX.economy.storage.ConfigManager;
import CraftLabsX.economy.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class SellMultiplierCommand implements CommandExecutor {

    private final EconomyManager economyManager;
    private final ConfigManager configManager;

    public SellMultiplierCommand(EconomyManager economyManager, ConfigManager configManager) {
        this.economyManager = economyManager;
        this.configManager = configManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can view multipliers.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            double multiplier = economyManager.getMultiplier(player.getUniqueId());
            SellMultiplierGUI.open(player, multiplier);
            return true;
        }

        if (args[0].equalsIgnoreCase("set")) {
            if (!player.hasPermission("economy.sellmulti.admin")) {
                MessageUtil.send(player, configManager.getMessage("errors.no_permission"));
                return true;
            }

            if (args.length < 3) {
                player.sendMessage("§cUsage: /sellmulti set <player> <multiplier>");
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                MessageUtil.send(player, configManager.getMessage("errors.player_not_found"));
                return true;
            }

            double multiplier;
            try {
                multiplier = Double.parseDouble(args[2]);
            } catch (NumberFormatException e) {
                MessageUtil.send(player, configManager.getMessage("errors.invalid_number"));
                return true;
            }

            if (multiplier < 0) {
                MessageUtil.send(player, configManager.getMessage("errors.invalid_number"));
                return true;
            }

            economyManager.setMultiplier(target.getUniqueId(), multiplier);

            Map<String, String> replacements = new HashMap<>();
            replacements.put("player", target.getName());
            replacements.put("multiplier", String.format("%.1f", multiplier));

            MessageUtil.send(player, configManager.getMessage("multiplier.set", replacements));
            return true;
        }

        double multiplier = economyManager.getMultiplier(player.getUniqueId());
        SellMultiplierGUI.open(player, multiplier);
        return true;
    }
}
