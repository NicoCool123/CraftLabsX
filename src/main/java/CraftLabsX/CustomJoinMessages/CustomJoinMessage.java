package CraftLabsX.CustomJoinMessages;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomJoinMessage extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("CustomJoinMessage plugin enabled!");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!player.hasPlayedBefore()) {
            // Player is joining for the first time
            int totalPlayers = Bukkit.getOfflinePlayers().length;
            String firstJoinMessage = ChatColor.GREEN + "Welcome to "
                    + ChatColor.AQUA + "mc.redcrafteryt11.net"
                    + ChatColor.GREEN + "! You’re the "
                    + ChatColor.YELLOW + totalPlayers
                    + ChatColor.GREEN + "th member!";

            event.setJoinMessage(firstJoinMessage);
        } else {
            // Returning player
            String joinMessage = ChatColor.YELLOW + player.getName()
                    + ChatColor.GREEN + " joined the server.";

            event.setJoinMessage(joinMessage);
        }
    }
}