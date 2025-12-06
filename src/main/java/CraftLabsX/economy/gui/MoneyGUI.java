package CraftLabsX.economy.gui;

import CraftLabsX.economy.EconomyManager;
import CraftLabsX.economy.util.GUIUtil;
import CraftLabsX.economy.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class MoneyGUI {

    public static void open(Player player, EconomyManager economyManager) {
        Inventory gui = Bukkit.createInventory(null, 27, ChatColor.GOLD + "Your Balance");

        // Fill with border items
        for (int i = 0; i < 27; i++) {
            if (i != 13) {
                gui.setItem(i, GUIUtil.createBorderItem());
            }
        }

        double balance = economyManager.getBalance(player);

        ItemStack balanceItem = new ItemStack(Material.GOLD_INGOT);
        ItemMeta balanceMeta = balanceItem.getItemMeta();

        if (balanceMeta != null) {
            balanceMeta.setDisplayName(ChatColor.YELLOW + "§e§lYour Money");
            balanceMeta.setLore(List.of(
                    "",
                    ChatColor.GREEN + "Balance: " + ChatColor.WHITE + MessageUtil.formatMoney(balance),
                    "",
                    ChatColor.GRAY + "Use " + ChatColor.YELLOW + "/bal" + ChatColor.GRAY + " to check quickly"
            ));
            balanceItem.setItemMeta(balanceMeta);
        }

        gui.setItem(13, balanceItem);

        player.openInventory(gui);
    }
}
