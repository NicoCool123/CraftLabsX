package CraftLabsX.economy.commands;

import CraftLabsX.economy.EconomyManager;
import CraftLabsX.economy.gui.BaltopGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BaltopCommand implements CommandExecutor {

    private final EconomyManager economyManager;

    public BaltopCommand(EconomyManager economyManager) {
        this.economyManager = economyManager;
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {

        // Only players can use this command
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cOnly players can use this command.");
            return true;
        }

        Player player = (Player) sender;

        // Open the Baltop GUI
        BaltopGUI.open(player, economyManager);

        return true;
    }
}