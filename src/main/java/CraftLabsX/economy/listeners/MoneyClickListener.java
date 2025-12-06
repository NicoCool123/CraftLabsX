package CraftLabsX.economy.listeners;

import CraftLabsX.economy.gui.MoneyGUI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;

public class MoneyClickListener implements Listener {

    @EventHandler
    public void onMoneyClick(InventoryClickEvent event) {


        if (event.getClickedInventory() == null) return;

        if (!event.getView().getTitle().equals(ChatColor.GOLD + "Your Balance")) return;

        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        Player player = (Player) event.getWhoClicked();

        // TODO: implement buttons like "Deposit", "Withdraw", etc.
        player.sendMessage("§eClicked on: " + clicked.getType().name());
    }
}
