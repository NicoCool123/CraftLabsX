package CraftLabsX.economy.storage;

import CraftLabsX.economy.manager.ShopItem;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ConfigManager {

    private final JavaPlugin plugin;
    private FileConfiguration shopConfig;
    private FileConfiguration sellConfig;
    private FileConfiguration messagesConfig;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        loadConfigs();
    }

    private void loadConfigs() {
        shopConfig = loadConfig("economy/shop.yml");
        sellConfig = loadConfig("economy/sell.yml");
        messagesConfig = loadConfig("economy/messages.yml");
    }

    private FileConfiguration loadConfig(String fileName) {
        File file = new File(plugin.getDataFolder(), fileName);

        if (!file.exists()) {
            file.getParentFile().mkdirs();
            plugin.saveResource(fileName, false);
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        InputStream defConfigStream = plugin.getResource(fileName);
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream));
            config.setDefaults(defConfig);
        }

        return config;
    }

    public void reload() {
        loadConfigs();
    }

    public List<String> getShopCategories() {
        List<String> categories = new ArrayList<>();
        ConfigurationSection categoriesSection = shopConfig.getConfigurationSection("categories");

        if (categoriesSection != null) {
            categories.addAll(categoriesSection.getKeys(false));
        }

        return categories;
    }

    public List<ShopItem> getShopItems(String category) {
        List<ShopItem> items = new ArrayList<>();
        ConfigurationSection categorySection = shopConfig.getConfigurationSection("categories." + category);

        if (categorySection == null) {
            return items;
        }

        List<Map<?, ?>> itemsList = categorySection.getMapList("items");

        for (Map<?, ?> itemMap : itemsList) {
            try {
                Material material = Material.valueOf((String) itemMap.get("material"));
                String name = (String) itemMap.get("name");
                double price = ((Number) itemMap.get("price")).doubleValue();
                int amount = ((Number) itemMap.get("amount")).intValue();
                int slot = ((Number) itemMap.get("slot")).intValue();

                List<String> lore = new ArrayList<>();

                ShopItem shopItem = new ShopItem(material, name, lore, price, amount, category, slot);
                items.add(shopItem);
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to load shop item: " + itemMap);
            }
        }

        return items;
    }

    public String getCategoryDisplayName(String category) {
        return shopConfig.getString("categories." + category + ".display_name", category);
    }

    public Material getCategoryIcon(String category) {
        String iconName = shopConfig.getString("categories." + category + ".icon", "CHEST");
        try {
            return Material.valueOf(iconName);
        } catch (IllegalArgumentException e) {
            return Material.CHEST;
        }
    }

    public int getCategorySlot(String category) {
        return shopConfig.getInt("categories." + category + ".slot", 0);
    }

    public double getSellPrice(Material material) {
        String path = "prices." + material.name();
        if (sellConfig.contains(path)) {
            return sellConfig.getDouble(path);
        }
        return sellConfig.getDouble("default_price", 1.0);
    }

    public String getMessage(String key) {
        String message = messagesConfig.getString(key, key);
        String prefix = messagesConfig.getString("prefix", "");
        return message.replace("{prefix}", prefix);
    }

    public String getMessage(String key, Map<String, String> replacements) {
        String message = getMessage(key);

        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            message = message.replace("{" + entry.getKey() + "}", entry.getValue());
        }

        return message;
    }

    public int getAuctionExpiryHours() {
        return plugin.getConfig().getInt("economy.auction_expiry_hours", 72);
    }

    public int getOrderExpiryHours() {
        return plugin.getConfig().getInt("economy.order_expiry_hours", 168);
    }

    public int getMaxAuctionsPerPlayer() {
        return plugin.getConfig().getInt("economy.max_auctions_per_player", 10);
    }

    public int getMaxOrdersPerPlayer() {
        return plugin.getConfig().getInt("economy.max_orders_per_player", 5);
    }
}
