package CraftLabsX.minigames;

import CraftLabsX.CraftLabsX;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdvancmentCreateingCommand implements CommandExecutor {

    private final CraftLabsX plugin;

    public AdvancmentCreateingCommand(CraftLabsX plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
            return true;
        }
        Player player = (Player) sender;

        if (!player.hasPermission("craftlabsx.advancement.create")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to create advancements!");
            return true;
        }

        if (args.length < 4) {
            player.sendMessage(ChatColor.YELLOW + "Usage: /addadvancement <name> <requirement> <rewardType> <rewardAmount>");
            player.sendMessage(ChatColor.GRAY + "Reward types: MONEY, COOKIES, CLICK_BONUS");
            return true;
        }

        try {
            String name = args[0];
            int requirement = Integer.parseInt(args[1]);
            AdvancementType.RewardType rewardType = AdvancementType.RewardType.valueOf(args[2].toUpperCase());
            int rewardAmount = Integer.parseInt(args[3]);


            plugin.getAdvancementManager().addCustomAdvancement(
                    new AdvancementType(name, requirement, rewardType, rewardAmount)
            );

            player.sendMessage(ChatColor.GREEN + "Advancement created: " + ChatColor.AQUA + name);
        } catch (IllegalArgumentException e) {
            player.sendMessage(ChatColor.RED + "Invalid arguments. Check reward type or number format.");
        }

        return true;
    }
}