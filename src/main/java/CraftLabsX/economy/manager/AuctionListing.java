package CraftLabsX.economy.manager;

public class AuctionListing {

    private final int id;
    private final String sellerUUID;
    private final String sellerName;
    private final String itemData;
    private final double price;
    private final long createdAt;
    private final long expiry;

    public AuctionListing(int id, String sellerUUID, String sellerName, String itemData, double price, long createdAt, long expiry) {
        this.id = id;
        this.sellerUUID = sellerUUID;
        this.sellerName = sellerName;
        this.itemData = itemData;
        this.price = price;
        this.createdAt = createdAt;
        this.expiry = expiry;
    }

    public AuctionListing(String sellerUUID, String sellerName, String itemData, double price, long createdAt, long expiry) {
        this(0, sellerUUID, sellerName, itemData, price, createdAt, expiry);
    }

    public int getId() {
        return id;
    }

    public String getSellerUUID() {
        return sellerUUID;
    }

    public String getSellerName() {
        return sellerName;
    }

    public String getItemData() {
        return itemData;
    }

    public double getPrice() {
        return price;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getExpiry() {
        return expiry;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiry;
    }
}
