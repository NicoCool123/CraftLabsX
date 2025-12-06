package CraftLabsX.minigames;

import org.bukkit.entity.Player;
import java.util.*;

public class GameManager {
    private Map<UUID, CookieClickerGame> games = new HashMap<>();

    public CookieClickerGame getGame(Player player) {
        return games.computeIfAbsent(player.getUniqueId(), id -> new CookieClickerGame(id));
    }

    public Collection<CookieClickerGame> getGames() {
        return games.values();
    }
}