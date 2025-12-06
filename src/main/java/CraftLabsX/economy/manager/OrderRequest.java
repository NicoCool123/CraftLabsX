package CraftLabsX.economy.manager;

public class OrderRequest {

    private final int id;
    private final String buyerUUID;
    private final String buyerName;
    private final String itemType;
    private final int itemAmount;
    private final double offeredPrice;
    private final long createdAt;
    private final long expiry;
    private final String status;

    public OrderRequest(int id, String buyerUUID, String buyerName, String itemType, int itemAmount, double offeredPrice, long createdAt, long expiry, String status) {
        this.id = id;
        this.buyerUUID = buyerUUID;
        this.buyerName = buyerName;
        this.itemType = itemType;
        this.itemAmount = itemAmount;
        this.offeredPrice = offeredPrice;
        this.createdAt = createdAt;
        this.expiry = expiry;
        this.status = status;
    }

    public OrderRequest(String buyerUUID, String buyerName, String itemType, int itemAmount, double offeredPrice, long createdAt, long expiry, String status) {
        this(0, buyerUUID, buyerName, itemType, itemAmount, offeredPrice, createdAt, expiry, status);
    }

    public int getId() {
        return id;
    }

    public String getBuyerUUID() {
        return buyerUUID;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public String getItemType() {
        return itemType;
    }

    public int getItemAmount() {
        return itemAmount;
    }

    public double getOfferedPrice() {
        return offeredPrice;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getExpiry() {
        return expiry;
    }

    public String getStatus() {
        return status;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiry;
    }
}
