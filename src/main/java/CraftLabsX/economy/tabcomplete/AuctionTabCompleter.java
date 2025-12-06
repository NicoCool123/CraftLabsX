package CraftLabsX.economy.tabcomplete;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AuctionTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return Arrays.asList("sell", "my");
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("sell")) {
            return Arrays.asList("100", "1k", "10k", "100k", "1m");
        }
        return new ArrayList<>();
    }
}
