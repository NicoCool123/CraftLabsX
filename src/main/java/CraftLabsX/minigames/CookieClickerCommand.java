package CraftLabsX.minigames;

import CraftLabsX.CraftLabsX;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CookieClickerCommand implements CommandExecutor {
    private final CraftLabsX plugin;

    public CookieClickerCommand(CraftLabsX plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Players only!");
            return true;
        }
        Player player = (Player) sender;
        CookieClickerGame game = plugin.getGameManager().getGame(player);
        CookieClickerGUI.open(player, game);
        return true;
    }
}