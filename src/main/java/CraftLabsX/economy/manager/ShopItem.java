package CraftLabsX.economy.manager;

import org.bukkit.Material;

import java.util.List;

public class ShopItem {

    private final Material material;
    private final String displayName;
    private final List<String> lore;
    private final double price;
    private final int amount;
    private final String category;
    private final int guiSlot;

    public ShopItem(Material material, String displayName, List<String> lore, double price, int amount, String category, int guiSlot) {
        this.material = material;
        this.displayName = displayName;
        this.lore = lore;
        this.price = price;
        this.amount = amount;
        this.category = category;
        this.guiSlot = guiSlot;
    }

    public Material getMaterial() {
        return material;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getLore() {
        return lore;
    }

    public double getPrice() {
        return price;
    }

    public int getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public int getGuiSlot() {
        return guiSlot;
    }
}
