package CraftLabsX.economy.listeners;

import CraftLabsX.economy.gui.SellMultiplierGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class SellMultiplierClickListener implements Listener {

    private static final Map<Integer, String> CATEGORY_SLOTS = new HashMap<>();

    static {
        CATEGORY_SLOTS.put(10, "Crops");
        CATEGORY_SLOTS.put(12, "Ores");
        CATEGORY_SLOTS.put(14, "Mob Drops");
        CATEGORY_SLOTS.put(16, "Natural Items");
        CATEGORY_SLOTS.put(28, "Armor");
        CATEGORY_SLOTS.put(30, "Tools");
        CATEGORY_SLOTS.put(32, "Fishing Loot");
        CATEGORY_SLOTS.put(34, "Enchanted Books");
        CATEGORY_SLOTS.put(37, "Potions");
        CATEGORY_SLOTS.put(43, "Blocks");
    }

    @EventHandler
    public void onMultiplierClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;

        String title = event.getView().getTitle();
        if (!title.contains("Sell Multiplier")) return;

        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();

        // Handle back button in category view
        if (title.contains("Multiplier") && !title.contains("Categories") && slot == 49) {
            SellMultiplierGUI.open(player, 1.0); // Default multiplier for now
            return;
        }

        // Handle category selection in main view
        if (title.contains("Categories")) {
            String category = CATEGORY_SLOTS.get(slot);
            if (category != null) {
                SellMultiplierGUI.openCategory(player, category, 1.0); // Default multiplier for now
            }
        }
    }
}
