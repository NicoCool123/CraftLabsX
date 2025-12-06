package CraftLabsX.economy.commands;

import CraftLabsX.economy.EconomyManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AdminMoneyCommand implements CommandExecutor {

    private final EconomyManager economyManager;

    public AdminMoneyCommand(EconomyManager economyManager) {
        this.economyManager = economyManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("econ.admin")) {
            sender.sendMessage("§cYou don't have permission to use this.");
            return true;
        }
        if (args.length < 3) {
            sender.sendMessage("§eUsage: /moneyadmin <add|remove|set> <player> <amount>");
            return true;
        }

        String action = args[0].toLowerCase();
        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            sender.sendMessage("§cPlayer not found.");
            return true;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid amount.");
            return true;
        }


        switch (action) {
            case "add":
                economyManager.deposit(target, amount);
                sender.sendMessage("§aAdded " + amount + " to " + target.getName());
                break;
            case "remove":
                economyManager.withdraw(target, amount);
                sender.sendMessage("§aRemoved " + amount + " from " + target.getName());
                break;
            case "set":
                double currentBalance = economyManager.getBalance(target);
                double difference = amount - currentBalance;
                if (difference > 0) {
                    economyManager.deposit(target, difference);
                } else if (difference < 0) {
                    economyManager.withdraw(target, -difference);
                }
                sender.sendMessage("§aSet " + target.getName() + "'s balance to " + amount);
                break;
            default:
                sender.sendMessage("§eUsage: /moneyadmin <add|remove|set> <player> <amount>");
                break;
        }
        return true;
    }
}