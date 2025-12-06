package CraftLabsX.economy.gui;

import CraftLabsX.economy.EconomyManager;
import CraftLabsX.economy.manager.ShopItem;
import CraftLabsX.economy.storage.ConfigManager;
import CraftLabsX.economy.util.GUIUtil;
import CraftLabsX.economy.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ShopGUI {

    public static void open(Player player, EconomyManager economyManager, ConfigManager configManager) {
        Inventory gui = Bukkit.createInventory(null, 54, "§6§lServer Shop");

        for (int i = 0; i < 54; i++) {
            gui.setItem(i, GUIUtil.createBorderItem());
        }

        List<String> categories = configManager.getShopCategories();

        for (String category : categories) {
            int slot = configManager.getCategorySlot(category);
            Material icon = configManager.getCategoryIcon(category);
            String displayName = configManager.getCategoryDisplayName(category);

            ItemStack categoryItem = GUIUtil.createItem(
                    icon,
                    displayName,
                    "",
                    "§7Click to browse this category!"
            );

            if (slot < 54) {
                gui.setItem(slot, categoryItem);
            }
        }

        player.openInventory(gui);
    }

    public static void openCategory(Player player, String category, EconomyManager economyManager, ConfigManager configManager) {
        String displayName = configManager.getCategoryDisplayName(category);
        Inventory gui = Bukkit.createInventory(null, 54, MessageUtil.color(displayName));

        for (int i = 0; i < 54; i++) {
            gui.setItem(i, GUIUtil.createBorderItem());
        }

        List<ShopItem> items = configManager.getShopItems(category);

        for (ShopItem shopItem : items) {
            ItemStack item = new ItemStack(shopItem.getMaterial(), shopItem.getAmount());

            ItemStack displayItem = GUIUtil.createItem(
                    shopItem.getMaterial(),
                    shopItem.getDisplayName(),
                    "",
                    "§7Amount: §e" + shopItem.getAmount(),
                    "§7Price: §a" + MessageUtil.formatMoney(shopItem.getPrice()),
                    "",
                    "§eClick to purchase!"
            );

            int slot = shopItem.getGuiSlot();
            if (slot >= 0 && slot < 45) {
                gui.setItem(slot, displayItem);
            }
        }

        gui.setItem(49, GUIUtil.createBackButton());

        player.openInventory(gui);
    }
}
