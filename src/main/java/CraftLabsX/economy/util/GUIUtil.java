package CraftLabsX.economy.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GUIUtil {

    public static ItemStack createItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            if (name != null) {
                meta.setDisplayName(MessageUtil.color(name));
            }
            if (lore != null && lore.length > 0) {
                List<String> coloredLore = Arrays.stream(lore)
                    .map(MessageUtil::color)
                    .collect(Collectors.toList());
                meta.setLore(coloredLore);
            }
            item.setItemMeta(meta);
        }

        return item;
    }

    public static ItemStack createButton(Material material, String name, String... lore) {
        return createItem(material, name, lore);
    }

    public static ItemStack createBorderItem() {
        return createItem(Material.GRAY_STAINED_GLASS_PANE, " ");
    }

    public static ItemStack createBackButton() {
        return createItem(Material.ARROW, "&cBack", "&7Click to go back");
    }

    public static ItemStack createCloseButton() {
        return createItem(Material.BARRIER, "&cClose", "&7Click to close");
    }
}
