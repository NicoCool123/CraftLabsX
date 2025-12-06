package CraftLabsX.economy.gui;

import CraftLabsX.economy.EconomyManager;
import CraftLabsX.economy.manager.AuctionListing;
import CraftLabsX.economy.storage.ConfigManager;
import CraftLabsX.economy.util.GUIUtil;
import CraftLabsX.economy.util.ItemSerializer;
import CraftLabsX.economy.util.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AuctionHouseGUI {

    public static void open(Player player, EconomyManager economyManager, ConfigManager configManager, String sortType, int page) {
        List<AuctionListing> auctions = economyManager.getAuctionsSorted(sortType);
        openPage(player, auctions, page, sortType, economyManager, configManager, false);
    }

    public static void openPlayerAuctions(Player player, EconomyManager economyManager, ConfigManager configManager) {
        List<AuctionListing> auctions = economyManager.getPlayerAuctions(player.getUniqueId());
        openPage(player, auctions, 0, "NEWEST", economyManager, configManager, true);
    }

    private static void openPage(Player player, List<AuctionListing> auctions, int page, String sortType, EconomyManager economyManager, ConfigManager configManager, boolean playerAuctions) {
        String title = playerAuctions ? "§6§lMy Auctions" : "§6§lAuction House";
        Inventory gui = Bukkit.createInventory(null, 54, title);

        for (int i = 0; i < 54; i++) {
            gui.setItem(i, GUIUtil.createBorderItem());
        }

        int itemsPerPage = 36;
        int start = page * itemsPerPage;
        int end = Math.min(start + itemsPerPage, auctions.size());

        for (int i = start; i < end; i++) {
            AuctionListing auction = auctions.get(i);

            try {
                ItemStack item = ItemSerializer.deserialize(auction.getItemData());

                if (item != null) {
                    ItemStack displayItem = item.clone();
                    ItemMeta meta = displayItem.getItemMeta();

                    if (meta != null) {
                        List<String> lore = new ArrayList<>();
                        lore.add("");
                        lore.add("§7Seller: §e" + auction.getSellerName());
                        lore.add("§7Price: §a" + MessageUtil.formatMoney(auction.getPrice()));
                        lore.add("");

                        long timeLeft = auction.getExpiry() - System.currentTimeMillis();
                        long hoursLeft = timeLeft / 3600000;

                        if (hoursLeft > 0) {
                            lore.add("§7Expires in: §e" + hoursLeft + " hours");
                        } else {
                            lore.add("§cExpired!");
                        }

                        lore.add("");

                        if (playerAuctions) {
                            lore.add("§cClick to cancel auction");
                        } else {
                            lore.add("§eClick to purchase!");
                        }

                        meta.setLore(lore);
                        displayItem.setItemMeta(meta);
                    }

                    int slot = (i - start) + 9;
                    if (slot < 45) {
                        gui.setItem(slot, displayItem);
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        if (page > 0) {
            gui.setItem(45, GUIUtil.createItem(Material.ARROW, "§aPrevious Page"));
        }

        if (!playerAuctions) {
            gui.setItem(49, GUIUtil.createItem(Material.HOPPER, "§eSort: " + sortType, "", "§7Click to change sorting"));
        }

        if (end < auctions.size()) {
            gui.setItem(53, GUIUtil.createItem(Material.ARROW, "§aNext Page"));
        }

        player.openInventory(gui);
    }

    public static void openSortMenu(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, "§6§lSort Auctions");

        for (int i = 0; i < 27; i++) {
            gui.setItem(i, GUIUtil.createBorderItem());
        }

        gui.setItem(10, GUIUtil.createItem(Material.GOLD_INGOT, "§aCheapest First", "", "§7Sort by lowest price"));
        gui.setItem(12, GUIUtil.createItem(Material.DIAMOND, "§eMost Expensive", "", "§7Sort by highest price"));
        gui.setItem(14, GUIUtil.createItem(Material.CLOCK, "§bNewest", "", "§7Sort by most recent"));
        gui.setItem(16, GUIUtil.createItem(Material.BIRCH_SIGN, "§7Oldest", "", "§7Sort by oldest first"));

        player.openInventory(gui);
    }
}
