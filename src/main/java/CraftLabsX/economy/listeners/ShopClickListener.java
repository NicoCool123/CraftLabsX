package CraftLabsX.economy.listeners;

import CraftLabsX.economy.EconomyManager;
import CraftLabsX.economy.gui.ShopGUI;
import CraftLabsX.economy.manager.ShopItem;
import CraftLabsX.economy.storage.ConfigManager;
import CraftLabsX.economy.util.MessageUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopClickListener implements Listener {

    private final EconomyManager economyManager;
    private final ConfigManager configManager;
    private final Map<String, String> lastCategory = new HashMap<>();

    public ShopClickListener(EconomyManager economyManager, ConfigManager configManager) {
        this.economyManager = economyManager;
        this.configManager = configManager;
    }

    @EventHandler
    public void onShopClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;

        String title = event.getView().getTitle();
        if (!title.contains("Shop") && !configManager.getShopCategories().stream()
                .anyMatch(cat -> title.contains(MessageUtil.color(configManager.getCategoryDisplayName(cat))))) {
            return;
        }

        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();

        if (slot == 49 && clicked.getType() == Material.ARROW) {
            ShopGUI.open(player, economyManager, configManager);
            return;
        }

        if (title.contains("Server Shop")) {
            handleCategoryClick(player, slot);
        } else {
            handleItemPurchase(player, slot);
        }
    }

    private void handleCategoryClick(Player player, int slot) {
        List<String> categories = configManager.getShopCategories();

        for (String category : categories) {
            int categorySlot = configManager.getCategorySlot(category);
            if (slot == categorySlot) {
                lastCategory.put(player.getName(), category);
                ShopGUI.openCategory(player, category, economyManager, configManager);
                return;
            }
        }
    }

    private void handleItemPurchase(Player player, int slot) {
        String category = lastCategory.get(player.getName());
        if (category == null) return;

        List<ShopItem> items = configManager.getShopItems(category);

        for (ShopItem shopItem : items) {
            if (shopItem.getGuiSlot() == slot) {
                if (player.getInventory().firstEmpty() == -1) {
                    MessageUtil.send(player, configManager.getMessage("shop.no_space"));
                    return;
                }

                if (!economyManager.withdraw(player, shopItem.getPrice())) {
                    Map<String, String> replacements = new HashMap<>();
                    replacements.put("amount", MessageUtil.formatMoney(shopItem.getPrice()));
                    MessageUtil.send(player, configManager.getMessage("errors.insufficient_funds", replacements));
                    return;
                }

                ItemStack item = new ItemStack(shopItem.getMaterial(), shopItem.getAmount());
                player.getInventory().addItem(item);

                Map<String, String> replacements = new HashMap<>();
                replacements.put("amount", String.valueOf(shopItem.getAmount()));
                replacements.put("item", shopItem.getMaterial().name());
                replacements.put("price", MessageUtil.formatMoney(shopItem.getPrice()));
                MessageUtil.send(player, configManager.getMessage("shop.purchased", replacements));
                return;
            }
        }
    }
}
