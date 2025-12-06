package CraftLabsX.shards;

import CraftLabsX.lobbyitems.doublejump;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class shardsmanager implements CommandExecutor {

    private final doublejump shardAPI;

    public shardsmanager(doublejump shardAPI) {
        this.shardAPI = shardAPI;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!cmd.getName().equalsIgnoreCase("shards")) return false;

        if (args.length < 2) {
            sender.sendMessage("§cUsage: /shards <add|remove|set|get> <player> [amount]");
            return true;
        }

        String action = args[0].toLowerCase();
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);

        if (target == null) {
            sender.sendMessage("§cPlayer not found.");
            return true;
        }

        switch (action) {
            case "add":
                if (args.length != 3) {
                    sender.sendMessage("§cUsage: /shards add <player> <amount>");
                    return true;
                }
                int addAmount = parseInt(args[2]);
                if (addAmount <= 0) {
                    sender.sendMessage("§cInvalid amount.");
                    return true;
                }
                shardAPI.addShards(target.getPlayer(), addAmount);
                sender.sendMessage("§aAdded " + addAmount + " shards to " + target.getName());
                break;

            case "remove":
                if (args.length != 3) {
                    sender.sendMessage("§cUsage: /shards remove <player> <amount>");
                    return true;
                }
                int removeAmount = parseInt(args[2]);
                if (removeAmount <= 0) {
                    sender.sendMessage("§cInvalid amount.");
                    return true;
                }
                shardAPI.removeShards(target.getPlayer(), removeAmount);
                sender.sendMessage("§aRemoved " + removeAmount + " shards from " + target.getName());
                break;

            case "set":
                if (args.length != 3) {
                    sender.sendMessage("§cUsage: /shards set <player> <amount>");
                    return true;
                }
                int setAmount = parseInt(args[2]);
                if (setAmount < 0) {
                    sender.sendMessage("§cInvalid amount.");
                    return true;
                }

                shardAPI.removeShards(target.getPlayer(), shardAPI.getShards(target.getPlayer()));
                shardAPI.addShards(target.getPlayer(), setAmount);
                sender.sendMessage("§aSet shards for " + target.getName() + " to " + setAmount);
                break;

            case "get":
                sender.sendMessage("§e" + target.getName() + " has " + shardAPI.getShards(target.getPlayer()) + " shards.");
                break;

            default:
                sender.sendMessage("§cUnknown action: " + action);
                break;
        }

        return true;
    }

    private int parseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}