package CraftLabsX.economy.listeners;

import CraftLabsX.economy.EconomyManager;
import CraftLabsX.economy.gui.AuctionHouseGUI;
import CraftLabsX.economy.manager.AuctionListing;
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

public class AuctionHouseClickListener implements Listener {

    private final EconomyManager economyManager;
    private final ConfigManager configManager;
    private final Map<String, String> lastSort = new HashMap<>();
    private final Map<String, Integer> lastPage = new HashMap<>();

    public AuctionHouseClickListener(EconomyManager economyManager, ConfigManager configManager) {
        this.economyManager = economyManager;
        this.configManager = configManager;
    }

    @EventHandler
    public void onAuctionClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;

        String title = event.getView().getTitle();
        if (!title.contains("Auction House") && !title.contains("My Auctions") && !title.contains("Sort Auctions")) return;

        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        Player player = (Player) event.getWhoClicked();
        int slot = event.getSlot();

        if (title.contains("Sort Auctions")) {
            handleSortClick(player, slot);
            return;
        }

        if (slot == 45 && clicked.getType() == Material.ARROW) {
            int page = lastPage.getOrDefault(player.getName(), 0);
            if (page > 0) {
                page--;
                lastPage.put(player.getName(), page);
                String sort = lastSort.getOrDefault(player.getName(), "NEWEST");
                AuctionHouseGUI.open(player, economyManager, configManager, sort, page);
            }
            return;
        }

        if (slot == 53 && clicked.getType() == Material.ARROW) {
            int page = lastPage.getOrDefault(player.getName(), 0);
            page++;
            lastPage.put(player.getName(), page);
            String sort = lastSort.getOrDefault(player.getName(), "NEWEST");
            AuctionHouseGUI.open(player, economyManager, configManager, sort, page);
            return;
        }

        if (slot == 49 && clicked.getType() == Material.HOPPER) {
            AuctionHouseGUI.openSortMenu(player);
            return;
        }

        if (title.contains("My Auctions")) {
            List<AuctionListing> playerAuctions = economyManager.getPlayerAuctions(player.getUniqueId());
            int index = slot - 9;
            if (index >= 0 && index < playerAuctions.size()) {
                AuctionListing auction = playerAuctions.get(index);
                economyManager.cancelAuction(auction.getId(), player);
                player.sendMessage("§aAuction cancelled!");
                player.closeInventory();
            }
        } else {
            String sort = lastSort.getOrDefault(player.getName(), "NEWEST");
            List<AuctionListing> auctions = economyManager.getAuctionsSorted(sort);
            int page = lastPage.getOrDefault(player.getName(), 0);
            int index = (page * 36) + (slot - 9);

            if (index >= 0 && index < auctions.size()) {
                AuctionListing auction = auctions.get(index);
                boolean success = economyManager.purchaseAuction(player, auction.getId());

                if (success) {
                    Map<String, String> replacements = new HashMap<>();
                    replacements.put("item", clicked.getType().name());
                    replacements.put("price", MessageUtil.formatMoney(auction.getPrice()));
                    MessageUtil.send(player, configManager.getMessage("auction.purchased", replacements));
                    player.closeInventory();
                } else {
                    MessageUtil.send(player, configManager.getMessage("errors.insufficient_funds"));
                }
            }
        }
    }

    private void handleSortClick(Player player, int slot) {
        String sortType = switch (slot) {
            case 10 -> "CHEAPEST";
            case 12 -> "EXPENSIVE";
            case 14 -> "NEWEST";
            case 16 -> "OLDEST";
            default -> "NEWEST";
        };

        lastSort.put(player.getName(), sortType);
        lastPage.put(player.getName(), 0);
        AuctionHouseGUI.open(player, economyManager, configManager, sortType, 0);
    }
}
