package CraftLabsX.economy.gui;

import CraftLabsX.economy.EconomyManager;
import CraftLabsX.economy.manager.OrderRequest;
import CraftLabsX.economy.storage.ConfigManager;
import CraftLabsX.economy.util.GUIUtil;
import CraftLabsX.economy.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class OrderGUI {

    public static void open(Player player, EconomyManager economyManager, ConfigManager configManager, int page) {
        List<OrderRequest> orders = economyManager.getActiveOrders();
        openPage(player, orders, page, economyManager, configManager, false);
    }

    public static void openPlayerOrders(Player player, EconomyManager economyManager, ConfigManager configManager) {
        List<OrderRequest> orders = economyManager.getPlayerOrders(player.getUniqueId());
        openPage(player, orders, 0, economyManager, configManager, true);
    }

    private static void openPage(Player player, List<OrderRequest> orders, int page, EconomyManager economyManager, ConfigManager configManager, boolean playerOrders) {
        String title = playerOrders ? "§6§lMy Orders" : "§6§lBuy Orders";
        Inventory gui = Bukkit.createInventory(null, 54, title);

        for (int i = 0; i < 54; i++) {
            gui.setItem(i, GUIUtil.createBorderItem());
        }

        int itemsPerPage = 36;
        int start = page * itemsPerPage;
        int end = Math.min(start + itemsPerPage, orders.size());

        for (int i = start; i < end; i++) {
            OrderRequest order = orders.get(i);

            Material itemType;
            try {
                itemType = Material.valueOf(order.getItemType());
            } catch (IllegalArgumentException e) {
                continue;
            }

            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add("§7Buyer: §e" + order.getBuyerName());
            lore.add("§7Item: §e" + itemType.name());
            lore.add("§7Amount: §e" + order.getItemAmount());
            lore.add("§7Price Each: §a" + MessageUtil.formatMoney(order.getOfferedPrice()));
            lore.add("§7Total: §a" + MessageUtil.formatMoney(order.getOfferedPrice() * order.getItemAmount()));
            lore.add("");

            long timeLeft = order.getExpiry() - System.currentTimeMillis();
            long hoursLeft = timeLeft / 3600000;

            if (hoursLeft > 0) {
                lore.add("§7Expires in: §e" + hoursLeft + " hours");
            } else {
                lore.add("§cExpired!");
            }

            lore.add("");

            if (playerOrders) {
                lore.add("§cClick to cancel order");
            } else {
                lore.add("§eClick to fulfill order!");
            }

            ItemStack displayItem = GUIUtil.createItem(
                    itemType,
                    "§e" + itemType.name(),
                    lore.toArray(new String[0])
            );

            int slot = (i - start) + 9;
            if (slot < 45) {
                gui.setItem(slot, displayItem);
            }
        }

        if (page > 0) {
            gui.setItem(45, GUIUtil.createItem(Material.ARROW, "§aPrevious Page"));
        }

        if (end < orders.size()) {
            gui.setItem(53, GUIUtil.createItem(Material.ARROW, "§aNext Page"));
        }

        player.openInventory(gui);
    }
}
