package CraftLabsX.serverchanger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public class serverchanger implements Listener, CommandExecutor {

    private final JavaPlugin plugin;

    public serverchanger(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        List<String> enabledServers = plugin.getConfig().getStringList("enabled-servers");
        String currentServerName = plugin.getServer().getName();
        if (enabledServers.contains(currentServerName)) {
            giveServerSelector(e.getPlayer());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        e.getPlayer().getInventory().clear();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can run this command!");
            return true;
        }
        Player player = (Player) sender;

        if (cmd.getName().equalsIgnoreCase("craftlabsx") &&
                args.length > 0 &&
                args[0].equalsIgnoreCase("serverselector")) {
            giveServerSelector(player);
            player.sendMessage("§aYou have received the Server Selector compass!");
            return true;
        }
        return false;
    }

    private void giveServerSelector(Player player) {
        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta meta = compass.getItemMeta();
        meta.setDisplayName("§aServer Selector");
        compass.setItemMeta(meta);
        player.getInventory().addItem(compass);
    }

    @EventHandler
    public void onCompassClick(PlayerInteractEvent e) {
        if (e.getItem() != null && e.getItem().getType() == Material.COMPASS) {
            if (e.getItem().getItemMeta() != null &&
                    "§aServer Selector".equals(e.getItem().getItemMeta().getDisplayName())) {
                openServerSelector(e.getPlayer());
            }
        }
    }

    private void openServerSelector(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, "§bSelect a Server");

        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        fillerMeta.setDisplayName(" ");
        filler.setItemMeta(fillerMeta);

        for (int i = 0; i < 27; i++) {
            inv.setItem(i, filler);
        }

        Set<String> keys = plugin.getConfig().getConfigurationSection("servers").getKeys(false);
        for (String key : keys) {
            String materialName = plugin.getConfig().getString("servers." + key + ".block", "STONE").toUpperCase();
            Material mat = Material.matchMaterial(materialName);

            if (mat == null) {
                plugin.getLogger().warning("[ServerChanger] Invalid block in config for server '" + key + "': '" + materialName + "' - Defaulting to STONE");
                mat = Material.STONE;
            }

            ItemStack item = new ItemStack(mat);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§e" + key);
            item.setItemMeta(meta);

            int slot = plugin.getConfig().getInt("servers." + key + ".slot", 0);
            if (slot >= 0 && slot < 27) {
                inv.setItem(slot, item);
            } else {
                plugin.getLogger().warning("[ServerChanger] Invalid slot for server '" + key + "': " + slot);
            }
        }
        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!e.getView().getTitle().equals("§bSelect a Server")) return;
        e.setCancelled(true);
        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;

        if (e.getCurrentItem().getItemMeta() != null) {
            String serverName = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
            sendPlayerToServer((Player) e.getWhoClicked(), serverName);
        }
    }

    private void sendPlayerToServer(Player player, String server) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(b);
        try {
            out.writeUTF("Connect");
            out.writeUTF(server);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        player.sendPluginMessage(plugin, "BungeeCord", b.toByteArray());
        player.getInventory().clear();
        player.closeInventory();
    }
}