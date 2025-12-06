package CraftLabsX.economy.listeners;

import CraftLabsX.economy.EconomyManager;
import CraftLabsX.economy.gui.OrderGUI;
import CraftLabsX.economy.manager.OrderRequest;
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

public class OrderClickListener implements Listener {

    private final EconomyManager economyManager;
    private final ConfigManager configManager;
    private final Map<String, Integer> lastPage = new HashMap<>();

    public OrderClickListener(EconomyManager economyManager, ConfigManager configManager) {
        this.economyManager = economyManager;
        this.configManager = configManager;
    }

    @EventHandler
    public void onOrderClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;

        String title = event.getView().getTitle();
        if (!title.contains("Buy Orders") && !title.contains("My Orders")) return;

        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();

        if (slot == 45 && clicked.getType() == Material.ARROW) {
            int page = lastPage.getOrDefault(player.getName(), 0);
            if (page > 0) {
                page--;
                lastPage.put(player.getName(), page);
                OrderGUI.open(player, economyManager, configManager, page);
            }
            return;
        }

        if (slot == 53 && clicked.getType() == Material.ARROW) {
            int page = lastPage.getOrDefault(player.getName(), 0);
            page++;
            lastPage.put(player.getName(), page);
            OrderGUI.open(player, economyManager, configManager, page);
            return;
        }

        if (title.contains("My Orders")) {
            List<OrderRequest> playerOrders = economyManager.getPlayerOrders(player.getUniqueId());
            int index = slot - 9;
            if (index >= 0 && index < playerOrders.size()) {
                OrderRequest order = playerOrders.get(index);
                economyManager.cancelOrder(order.getId(), player);
                player.sendMessage("§aOrder cancelled and refunded!");
                player.closeInventory();
            }
        } else {
            List<OrderRequest> orders = economyManager.getActiveOrders();
            int page = lastPage.getOrDefault(player.getName(), 0);
            int index = (page * 36) + (slot - 9);

            if (index >= 0 && index < orders.size()) {
                OrderRequest order = orders.get(index);
                boolean success = economyManager.fulfillOrder(player, order.getId());

                if (success) {
                    Map<String, String> replacements = new HashMap<>();
                    replacements.put("total", MessageUtil.formatMoney(order.getOfferedPrice() * order.getItemAmount()));
                    MessageUtil.send(player, configManager.getMessage("order.fulfilled", replacements));
                    player.closeInventory();
                } else {
                    MessageUtil.send(player, configManager.getMessage("order.insufficient_items"));
                }
            }
        }
    }
}
