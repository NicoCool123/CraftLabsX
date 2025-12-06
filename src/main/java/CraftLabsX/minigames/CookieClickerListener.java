package CraftLabsX.minigames;

import CraftLabsX.CraftLabsX;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class CookieClickerListener implements Listener {

    private final CraftLabsX plugin;

    public CookieClickerListener(CraftLabsX plugin) {
        this.plugin = plugin;
        startAutoClickerTask();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;
        Player player = (Player) e.getWhoClicked();

        if (e.getView().getTitle().contains("Cookie Clicker")) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;

            CookieClickerGame game = plugin.getGameManager().getGame(player);

            Material clickedType = e.getCurrentItem().getType();


            if (clickedType == Material.COOKIE) {
                game.addCookies(1);
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1.5f);
                CookieClickerGUI.open(player, game);
                return;
            }


            UpgradeType upgradeType = getUpgradeTypeByMaterial(clickedType);
            if (upgradeType != null) {
                int amountToBuy = e.isShiftClick() ? 10 : 1;
                if (game.buyUpgrade(upgradeType, amountToBuy)) {
                    player.sendMessage(ChatColor.GREEN + "Purchased " + amountToBuy + " " + upgradeType.getDisplayName() + "!");
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
                } else {
                    player.sendMessage(ChatColor.RED + "Not enough cookies!");
                    player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
                }
                CookieClickerGUI.open(player, game);
            }
        }
    }

    private UpgradeType getUpgradeTypeByMaterial(Material mat) {
        return switch (mat) {
            case EMERALD -> UpgradeType.AUTO_CLICKER;
            case DIAMOND -> UpgradeType.MULTIPLIER;
            case GOLDEN_APPLE -> UpgradeType.GOLDEN_COOKIE;
            case BLAZE_POWDER -> UpgradeType.CLICK_FRENZY;
            case BARREL -> UpgradeType.COOKIE_FACTORY;
            case ENCHANTING_TABLE -> UpgradeType.RESEARCH_CENTER;
            case NETHER_STAR -> UpgradeType.PRESTIGE;
            default -> null;
        };
    }

    private void startAutoClickerTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (CookieClickerGame game : plugin.getGameManager().getGames()) {
                    int autoClickers = game.getUpgradeCount(UpgradeType.AUTO_CLICKER);
                    if (autoClickers > 0) {
                        game.addCookies(autoClickers);
                    }
                }
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }
}