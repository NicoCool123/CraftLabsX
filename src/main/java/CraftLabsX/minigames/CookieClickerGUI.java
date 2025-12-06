package CraftLabsX.minigames;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class CookieClickerGUI {

    private static final String PREFIX = "§x§3§C§4§1§4§Aᴄ§x§4§1§4§A§5§7ʀ§x§4§6§5§3§6§4ᴀ§x§4§A§5§C§7§2ꜰ§x§4§F§6§5§7§Fᴛ§x§5§4§6§E§8§Cʟ§x§5§9§7§7§9§9ᴀ§x§5§D§8§0§A§7ʙ§x§6§2§8§9§B§4ѕ§x§6§7§9§2§C§1ｘ";

    private static final DecimalFormat df = new DecimalFormat("#,###");

    public static void open(Player player, CookieClickerGame game) {
        Inventory gui = Bukkit.createInventory(null, 54, PREFIX + ChatColor.GOLD + " Cookie Clicker");


        ItemStack cookieItem = createItem(Material.COOKIE,
                ChatColor.YELLOW + "" + ChatColor.BOLD + "Click Me!",
                ChatColor.GRAY + "Cookies: " + df.format(game.getCookies()),
                ChatColor.GRAY + "Click Bonus: +" + game.getUpgradeCount(UpgradeType.MULTIPLIER)
        );
        gui.setItem(13, cookieItem);


        int slot = 28;
        for (UpgradeType type : UpgradeType.values()) {
            int owned = game.getUpgradeCount(type);
            double price = game.getNextPrice(type);

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Owned: " + owned);
            lore.add(ChatColor.GRAY + "Price: " + df.format(price) + " cookies");
            lore.add(ChatColor.YELLOW + "Click to buy 1");
            lore.add(ChatColor.YELLOW + "Shift-Click to buy 10");

            gui.setItem(slot++, createItem(getMaterialForUpgrade(type),
                    ChatColor.AQUA + type.getDisplayName(), lore.toArray(new String[0])));
        }

        player.openInventory(gui);
    }

    private static Material getMaterialForUpgrade(UpgradeType type) {
        return switch (type) {
            case AUTO_CLICKER -> Material.EMERALD;
            case MULTIPLIER -> Material.DIAMOND;
            case GOLDEN_COOKIE -> Material.GOLDEN_APPLE;
            case CLICK_FRENZY -> Material.BLAZE_POWDER;
            case COOKIE_FACTORY -> Material.BARREL;
            case RESEARCH_CENTER -> Material.ENCHANTING_TABLE;
            case PRESTIGE -> Material.NETHER_STAR;
        };
    }

    private static ItemStack createItem(Material mat, String name, String... loreLines) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        List<String> lore = new ArrayList<>();
        for (String line : loreLines) lore.add(line);
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }
}