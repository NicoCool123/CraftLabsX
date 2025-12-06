package CraftLabsX.economy.gui;

import CraftLabsX.economy.util.GUIUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;

public class SellMultiplierGUI {

    public static void open(Player player, double multiplier) {
        Inventory gui = Bukkit.createInventory(null, 54, "§6§lSell Multiplier Categories");

        // Fill with border items
        for (int i = 0; i < 54; i++) {
            gui.setItem(i, GUIUtil.createBorderItem());
        }

        // Category items
        gui.setItem(10, GUIUtil.createItem(
                Material.WHEAT,
                "§e§lCrops",
                "",
                "§aMultiplier: §e" + String.format("%.1fx", multiplier),
                "",
                "§7Applies to: Wheat, Carrots,",
                "§7Potatoes, Beetroot, etc.",
                "",
                "§eClick to view details!"
        ));

        gui.setItem(12, GUIUtil.createItem(
                Material.DIAMOND_ORE,
                "§e§lOres",
                "",
                "§aMultiplier: §e" + String.format("%.1fx", multiplier),
                "",
                "§7Applies to: Coal, Iron, Gold,",
                "§7Diamond, Emerald, etc.",
                "",
                "§eClick to view details!"
        ));

        gui.setItem(14, GUIUtil.createItem(
                Material.ROTTEN_FLESH,
                "§e§lMob Drops",
                "",
                "§aMultiplier: §e" + String.format("%.1fx", multiplier),
                "",
                "§7Applies to: Rotten Flesh, Bones,",
                "§7Spider Eyes, Gunpowder, etc.",
                "",
                "§eClick to view details!"
        ));

        gui.setItem(16, GUIUtil.createItem(
                Material.OAK_LOG,
                "§e§lNatural Items",
                "",
                "§aMultiplier: §e" + String.format("%.1fx", multiplier),
                "",
                "§7Applies to: Logs, Dirt, Sand,",
                "§7Gravel, Stone, etc.",
                "",
                "§eClick to view details!"
        ));

        gui.setItem(28, GUIUtil.createItem(
                Material.DIAMOND_CHESTPLATE,
                "§e§lArmor",
                "",
                "§aMultiplier: §e" + String.format("%.1fx", multiplier),
                "",
                "§7Applies to: All types of",
                "§7armor pieces",
                "",
                "§eClick to view details!"
        ));

        gui.setItem(30, GUIUtil.createItem(
                Material.DIAMOND_SWORD,
                "§e§lTools",
                "",
                "§aMultiplier: §e" + String.format("%.1fx", multiplier),
                "",
                "§7Applies to: Pickaxes, Swords,",
                "§7Axes, Shovels, Hoes",
                "",
                "§eClick to view details!"
        ));

        gui.setItem(32, GUIUtil.createItem(
                Material.FISHING_ROD,
                "§e§lFishing Loot",
                "",
                "§aMultiplier: §e" + String.format("%.1fx", multiplier),
                "",
                "§7Applies to: Fish, Treasure,",
                "§7and Junk from fishing",
                "",
                "§eClick to view details!"
        ));

        gui.setItem(34, GUIUtil.createItem(
                Material.ENCHANTED_BOOK,
                "§e§lEnchanted Books",
                "",
                "§aMultiplier: §e" + String.format("%.1fx", multiplier),
                "",
                "§7Applies to: All enchanted books",
                "",
                "§eClick to view details!"
        ));

        gui.setItem(37, GUIUtil.createItem(
                Material.POTION,
                "§e§lPotions",
                "",
                "§aMultiplier: §e" + String.format("%.1fx", multiplier),
                "",
                "§7Applies to: Potions and",
                "§7Splash Potions",
                "",
                "§eClick to view details!"
        ));

        gui.setItem(43, GUIUtil.createItem(
                Material.STONE_BRICKS,
                "§e§lBlocks",
                "",
                "§aMultiplier: §e" + String.format("%.1fx", multiplier),
                "",
                "§7Applies to: Building blocks,",
                "§7Decorative blocks, etc.",
                "",
                "§eClick to view details!"
        ));

        player.openInventory(gui);
    }

    public static void openCategory(Player player, String category, double multiplier) {
        Inventory gui = Bukkit.createInventory(null, 54, "§6§l" + category + " Multiplier");

        // Fill with border items
        for (int i = 0; i < 54; i++) {
            gui.setItem(i, GUIUtil.createBorderItem());
        }

        // Back button
        gui.setItem(49, GUIUtil.createItem(
                Material.ARROW,
                "§c§lBack",
                "",
                "§7Return to categories"
        ));

        // Category info
        gui.setItem(22, GUIUtil.createItem(
                getCategoryIcon(category),
                "§e§l" + category + " Multiplier",
                "",
                "§aMultiplier: §e" + String.format("%.1fx", multiplier),
                "",
                "§7This multiplier applies to all",
                "§7" + category.toLowerCase() + " you sell using §e/sell§7.",
                "",
                "§7Higher multipliers mean more money!"
        ));

        player.openInventory(gui);
    }

    private static Material getCategoryIcon(String category) {
        Map<String, Material> icons = new HashMap<>();
        icons.put("Crops", Material.WHEAT);
        icons.put("Ores", Material.DIAMOND_ORE);
        icons.put("Mob Drops", Material.ROTTEN_FLESH);
        icons.put("Natural Items", Material.OAK_LOG);
        icons.put("Armor", Material.DIAMOND_CHESTPLATE);
        icons.put("Tools", Material.DIAMOND_SWORD);
        icons.put("Fishing Loot", Material.FISHING_ROD);
        icons.put("Enchanted Books", Material.ENCHANTED_BOOK);
        icons.put("Potions", Material.POTION);
        icons.put("Blocks", Material.STONE_BRICKS);

        return icons.getOrDefault(category, Material.NETHER_STAR);
    }
}
