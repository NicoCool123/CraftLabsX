package CraftLabsX.minigames;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.ArrayList;
import java.util.List;

public class AdvancementListener implements Listener {
    private final AdvancementManager manager;
    private final GameManager gameManager;
    private final Economy econ;

    public AdvancementListener(AdvancementManager manager, GameManager gameManager, Economy econ) {
        this.manager = manager;
        this.gameManager = gameManager;
        this.econ = econ;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;
        Player player = (Player) e.getWhoClicked();
        if (!e.getView().getTitle().contains("Advancements")) return;

        e.setCancelled(true);
        int slot = e.getRawSlot();


        List<AdvancementType> allAdvancements = new ArrayList<>();
        allAdvancements.addAll(AdvancementType.getDefaultAdvancements());
        allAdvancements.addAll(manager.getCustomAdvancements());

        if (slot < allAdvancements.size()) {
            AdvancementType type = allAdvancements.get(slot);
            CookieClickerGame game = gameManager.getGame(player);

            if (AdvancementGUI.isUnlocked(player, type, game, econ) && !manager.isClaimed(player.getUniqueId(), type)) {

                switch (type.getRewardType()) {
                    case MONEY:
                        econ.depositPlayer(player, type.getRewardAmount());
                        break;
                    case COOKIES:
                        game.addCookies(type.getRewardAmount());
                        break;
                    case CLICK_BONUS:
                        player.sendMessage("Click bonus applied!");
                        break;
                }

                manager.claim(player.getUniqueId(), type);
                player.sendMessage("§aAdvancement claimed: " + type.getDisplayName());
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);


                AdvancementGUI.open(player, manager, game, econ);
            }
        }
    }
}