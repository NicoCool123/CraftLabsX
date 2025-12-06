package CraftLabsX.placeholders;

import CraftLabsX.CraftLabsX;
import CraftLabsX.lobbyitems.doublejump;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class ShardsPlaceholder extends PlaceholderExpansion {

    private final CraftLabsX plugin;

    public ShardsPlaceholder(CraftLabsX plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "craftlabsx";
    }

    @Override
    public String getAuthor() {
        return "RedCrafterYT11";
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) return "";

        if (identifier.equalsIgnoreCase("shards")) {
            doublejump dj = plugin.getDoubleJumpManager();
            return String.valueOf(dj.getShards(player));
        }
        return null;
    }
}