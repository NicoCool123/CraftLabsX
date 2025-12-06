package CraftLabsX.economy.listeners;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class BaltopClickListener implements Listener {

    @EventHandler
    public void onBaltopClick(InventoryClickEvent event) {


        if (event.getClickedInventory() == null) return;


        if (!event.getView().getTitle().equals(ChatColor.GOLD + "Balance Top")) return;

        event.setCancelled(true);


        ItemStack clicked = event.getCurrentItem();
        if (clicked == null) return;


        // TODO: add functionality if desired
    }
}
