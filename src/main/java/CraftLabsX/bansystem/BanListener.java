package CraftLabsX.bansystem;

import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class BanListener implements Listener {

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        BanList banList = Bukkit.getBanList(BanList.Type.PROFILE);
        if (banList.isBanned(event.getPlayerProfile())) {
            String reason = banList.getBanEntry(event.getPlayerProfile()).getReason();
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED,
                    reason != null ? reason : "You are banned from this server.");
        }
    }
}