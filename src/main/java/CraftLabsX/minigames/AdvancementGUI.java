package CraftLabsX.minigames;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class AdvancementGUI {

    public static void open(Player player, AdvancementManager manager, CookieClickerGame game, Economy econ) {
        Inventory gui = Bukkit.createInventory(null, 54, ChatColor.LIGHT_PURPLE + "Advancements");

        int slot = 0;


        List<AdvancementType> allAdvancements = new ArrayList<>();
        allAdvancements.addAll(AdvancementType.getDefaultAdvancements());
        allAdvancements.addAll(manager.getCustomAdvancements());

        for (AdvancementType type : allAdvancements) {
            slot = addAdvancementItem(gui, player, manager, game, econ, type, slot);
        }

        player.openInventory(gui);
    }

    private static int addAdvancementItem(Inventory gui, Player player, AdvancementManager manager,
                                          CookieClickerGame game, Economy econ, AdvancementType type, int slot) {

        boolean unlocked = isUnlocked(player, type, game, econ);
        boolean claimed = manager.isClaimed(player.getUniqueId(), type);

        ItemStack item = new ItemStack(unlocked ? Material.EMERALD : Material.COAL);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName((claimed ? ChatColor.GREEN
                : unlocked ? ChatColor.YELLOW
                : ChatColor.RED) + type.getDisplayName());
        meta.setLore(Arrays.asList(
                ChatColor.GRAY + "Requirement: " + type.getRequirement(),
                ChatColor.GRAY + "Reward: " + type.getRewardAmount() + " " + type.getRewardType(),
                claimed ? ChatColor.DARK_GREEN + "CLAIMED" :
                        unlocked ? ChatColor.GOLD + "Click to claim!" :
                                ChatColor.DARK_RED + "Locked"
        ));
        item.setItemMeta(meta);

        gui.setItem(slot++, item);
        return slot;
    }


    public static boolean isUnlocked(Player player, AdvancementType type, CookieClickerGame game, Economy econ) {
        if (type.getRewardType() == AdvancementType.RewardType.MONEY && econ == null) {
            Bukkit.getLogger().warning("Economy provider is null! Could not check money-based advancement.");
            return false;
        }


        switch (type.getDisplayName().toUpperCase()) {
            case "COOKIES_10K":
                return game.getCookies() >= type.getRequirement();

            case "MULTIPLIERS_50":
                return game.getUpgradeCount(UpgradeType.MULTIPLIER) >= type.getRequirement();

            case "MONEY_100":
                return econ != null && econ.getBalance(player) >= type.getRequirement();

            case "PRESTIGE_ONE":
                return game.getUpgradeCount(UpgradeType.PRESTIGE) >= type.getRequirement();

            default:

                return game.getCookies() >= type.getRequirement();
        }
    }
}