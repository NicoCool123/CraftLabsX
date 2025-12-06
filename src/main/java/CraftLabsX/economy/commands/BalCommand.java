package CraftLabsX.economy.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BalCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can check their balance.");
            return true;
        }
        Player player = (Player) sender;

        // TODO: fetch balance from Vault
        double balance = 0.0; // HonkBen
        player.sendMessage("§aYour balance: §e$" + balance);
        return true;
    }
}