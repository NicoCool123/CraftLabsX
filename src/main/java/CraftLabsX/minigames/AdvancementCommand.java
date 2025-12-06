package CraftLabsX.minigames;

import CraftLabsX.CraftLabsX;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdvancementCommand implements CommandExecutor {

    private final CraftLabsX plugin;
    private final GameManager gameManager;
    private final AdvancementManager advancementManager;
    private final Economy econ;

    public AdvancementCommand(CraftLabsX plugin, GameManager gameManager, AdvancementManager advancementManager, Economy econ) {
        this.plugin = plugin;
        this.gameManager = gameManager;
        this.advancementManager = advancementManager;
        this.econ = econ;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this.");
            return true;
        }
        Player player = (Player) sender;
        CookieClickerGame game = gameManager.getGame(player);
        AdvancementGUI.open(player, advancementManager, game, econ);
        return true;
    }
}