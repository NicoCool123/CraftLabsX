package CraftLabsX.economy.gui;

import CraftLabsX.economy.EconomyManager;
import CraftLabsX.economy.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

public class BaltopGUI {

    /**
     * Opens the balance top GUI for a specific player.
     */
    public static void open(Player player, EconomyManager economyManager) {
        Inventory gui = Bukkit.createInventory(null, 54, ChatColor.GOLD + "Balance Top");

        // Get the top balances
        Map<String, Double> top = getTopBalances(economyManager);

        int slot = 0;
        int rank = 1;
        for (Map.Entry<String, Double> entry : top.entrySet()) {
            if (slot >= 45) break; // Leave space for border items

            String playerName = entry.getKey();
            double balance = entry.getValue();

            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            ItemMeta meta = head.getItemMeta();

            if (meta != null) {
                meta.setDisplayName(ChatColor.GOLD + "#" + rank + " " + ChatColor.YELLOW + playerName);
                meta.setLore(java.util.List.of(
                        ChatColor.GREEN + "Balance: " + ChatColor.WHITE + MessageUtil.formatMoney(balance)
                ));
                head.setItemMeta(meta);
            }

            gui.setItem(slot, head);
            slot++;
            rank++;
        }

        // Open to player
        player.openInventory(gui);
    }

    /**
     * Gets the top player balances from the economy system.
     */
    private static Map<String, Double> getTopBalances(EconomyManager economyManager) {
        Map<String, Double> balances = new LinkedHashMap<>();

        // Get all players who have played on the server
        OfflinePlayer[] offlinePlayers = Bukkit.getOfflinePlayers();

        // Create a map of player names to balances
        Map<String, Double> allBalances = new HashMap<>();
        for (OfflinePlayer offlinePlayer : offlinePlayers) {
            if (offlinePlayer.getName() != null) {
                double balance = 0.0;
                if (offlinePlayer.isOnline()) {
                    balance = economyManager.getBalance(offlinePlayer.getPlayer());
                }
                allBalances.put(offlinePlayer.getName(), balance);
            }
        }

        // Sort by balance (highest first) and limit to top 45
        balances = allBalances.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(45)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        return balances;
    }
}
