package CraftLabsX.economy;

import CraftLabsX.economy.database.*;
import CraftLabsX.economy.manager.AuctionListing;
import CraftLabsX.economy.manager.OrderRequest;
import CraftLabsX.economy.util.ItemSerializer;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class EconomyManager {

    private final Economy economy;
    private final DatabaseManager databaseManager;
    private final AuctionDAO auctionDAO;
    private final OrderDAO orderDAO;
    private final MultiplierDAO multiplierDAO;

    public EconomyManager(Economy economy, DatabaseManager databaseManager) {
        this.economy = economy;
        this.databaseManager = databaseManager;
        this.auctionDAO = new AuctionDAO(databaseManager);
        this.orderDAO = new OrderDAO(databaseManager);
        this.multiplierDAO = new MultiplierDAO(databaseManager);
    }

    public boolean createAuction(Player seller, ItemStack item, double price, long expiryHours) {
        try {
            String serializedItem = ItemSerializer.serialize(item);
            long now = System.currentTimeMillis();
            long expiry = now + (expiryHours * 3600000);

            AuctionListing listing = new AuctionListing(
                    seller.getUniqueId().toString(),
                    seller.getName(),
                    serializedItem,
                    price,
                    now,
                    expiry
            );

            return auctionDAO.insertAuction(listing);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean purchaseAuction(Player buyer, int auctionId) {
        AuctionListing auction = auctionDAO.getAuctionById(auctionId);

        if (auction == null || auction.isExpired()) {
            return false;
        }

        if (auction.getSellerUUID().equals(buyer.getUniqueId().toString())) {
            return false;
        }

        if (!withdraw(buyer, auction.getPrice())) {
            return false;
        }

        try {
            ItemStack item = ItemSerializer.deserialize(auction.getItemData());

            if (item == null) {
                deposit(buyer, auction.getPrice());
                return false;
            }

            if (buyer.getInventory().firstEmpty() == -1) {
                deposit(buyer, auction.getPrice());
                return false;
            }

            buyer.getInventory().addItem(item);

            depositOffline(auction.getSellerUUID(), auction.getPrice());

            auctionDAO.deleteAuction(auctionId);

            return true;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            deposit(buyer, auction.getPrice());
            return false;
        }
    }

    public List<AuctionListing> getActiveAuctions() {
        return auctionDAO.getActiveAuctions();
    }

    public List<AuctionListing> getAuctionsSorted(String sortType) {
        return auctionDAO.getAuctionsSortedBy(sortType);
    }

    public List<AuctionListing> getPlayerAuctions(UUID uuid) {
        return auctionDAO.getPlayerAuctions(uuid.toString());
    }

    public void cancelAuction(int auctionId, Player player) {
        AuctionListing auction = auctionDAO.getAuctionById(auctionId);

        if (auction != null && auction.getSellerUUID().equals(player.getUniqueId().toString())) {
            try {
                ItemStack item = ItemSerializer.deserialize(auction.getItemData());
                if (item != null) {
                    player.getInventory().addItem(item);
                }
                auctionDAO.deleteAuction(auctionId);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public int countPlayerAuctions(UUID uuid) {
        return auctionDAO.countPlayerAuctions(uuid.toString());
    }

    public boolean createOrder(Player buyer, Material type, int amount, double price, long expiryHours) {
        long now = System.currentTimeMillis();
        long expiry = now + (expiryHours * 3600000);

        double totalCost = price * amount;

        if (!withdraw(buyer, totalCost)) {
            return false;
        }

        OrderRequest order = new OrderRequest(
                buyer.getUniqueId().toString(),
                buyer.getName(),
                type.name(),
                amount,
                price,
                now,
                expiry,
                "ACTIVE"
        );

        boolean success = orderDAO.insertOrder(order);

        if (!success) {
            deposit(buyer, totalCost);
        }

        return success;
    }

    public boolean fulfillOrder(Player seller, int orderId) {
        OrderRequest order = orderDAO.getOrderById(orderId);

        if (order == null || order.isExpired() || !order.getStatus().equals("ACTIVE")) {
            return false;
        }

        Material itemType;
        try {
            itemType = Material.valueOf(order.getItemType());
        } catch (IllegalArgumentException e) {
            return false;
        }

        ItemStack requiredItem = new ItemStack(itemType, order.getItemAmount());

        if (!seller.getInventory().containsAtLeast(requiredItem, order.getItemAmount())) {
            return false;
        }

        seller.getInventory().removeItem(requiredItem);

        double totalPayout = order.getOfferedPrice() * order.getItemAmount();
        deposit(seller, totalPayout);

        orderDAO.updateOrderStatus(orderId, "FULFILLED");

        return true;
    }

    public List<OrderRequest> getActiveOrders() {
        return orderDAO.getActiveOrders();
    }

    public List<OrderRequest> getPlayerOrders(UUID uuid) {
        return orderDAO.getOrdersByBuyer(uuid.toString());
    }

    public void cancelOrder(int orderId, Player player) {
        OrderRequest order = orderDAO.getOrderById(orderId);

        if (order != null && order.getBuyerUUID().equals(player.getUniqueId().toString())) {
            double refund = order.getOfferedPrice() * order.getItemAmount();
            deposit(player, refund);
            orderDAO.deleteOrder(orderId);
        }
    }

    public int countPlayerOrders(UUID uuid) {
        return orderDAO.countPlayerOrders(uuid.toString());
    }

    public double getMultiplier(UUID uuid) {
        return multiplierDAO.getMultiplier(uuid.toString());
    }

    public void setMultiplier(UUID uuid, double multiplier) {
        multiplierDAO.setMultiplier(uuid.toString(), multiplier);
    }

    public boolean withdraw(Player player, double amount) {
        if (economy == null) {
            return false;
        }

        if (!economy.has(player, amount)) {
            return false;
        }

        return economy.withdrawPlayer(player, amount).transactionSuccess();
    }

    public boolean deposit(Player player, double amount) {
        if (economy == null) {
            return false;
        }

        return economy.depositPlayer(player, amount).transactionSuccess();
    }

    public void depositOffline(String uuid, double amount) {
        if (economy == null) {
            return;
        }

        try {
            org.bukkit.OfflinePlayer offlinePlayer = org.bukkit.Bukkit.getOfflinePlayer(UUID.fromString(uuid));
            economy.depositPlayer(offlinePlayer, amount);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public double getBalance(Player player) {
        if (economy == null) {
            return 0.0;
        }

        return economy.getBalance(player);
    }

    public AuctionDAO getAuctionDAO() {
        return auctionDAO;
    }

    public OrderDAO getOrderDAO() {
        return orderDAO;
    }

    public MultiplierDAO getMultiplierDAO() {
        return multiplierDAO;
    }
}
