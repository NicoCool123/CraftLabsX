package CraftLabsX.lobbyitems;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class doublejump implements Listener, CommandExecutor {

    private final JavaPlugin plugin;
    private final Map<UUID, Integer> shards = new HashMap<>();
    private final Set<UUID> disabledDoubleJump = new HashSet<>();

    public doublejump(JavaPlugin plugin) {
        this.plugin = plugin;
        loadShards();
        loadToggleState();


        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    addShards(p, 1);
                }
            }
        }.runTaskTimer(plugin, 1200, 1200);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        e.getPlayer().setAllowFlight(true);
    }

    @EventHandler
    public void onDoubleJump(PlayerToggleFlightEvent e) {
        Player p = e.getPlayer();


        if (disabledDoubleJump.contains(p.getUniqueId())) return;

        if (!p.getAllowFlight()) return;
        if (p.hasPermission("doublejump.use") || hasFeather(p)) {
            e.setCancelled(true);
            p.setVelocity(p.getLocation().getDirection().multiply(1.5).setY(1));
        }
    }

    private boolean hasFeather(Player p) {
        for (ItemStack item : p.getInventory().getContents()) {
            if (item != null && item.getType() == Material.FEATHER && item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();
                if (meta.getDisplayName().equals("§aDouble Jump Feather")) {
                    return true;
                }
            }
        }
        return false;
    }


    public int getShards(Player p) {
        return shards.getOrDefault(p.getUniqueId(), 0);
    }

    public void addShards(Player p, int amount) {
        shards.put(p.getUniqueId(), getShards(p) + amount);
        saveShards();
    }

    public void removeShards(Player p, int amount) {
        shards.put(p.getUniqueId(), Math.max(0, getShards(p) - amount));
        saveShards();
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;

        if (cmd.getName().equalsIgnoreCase("shardshop")) {
            openShardShop(player);
            return true;
        }
        if (cmd.getName().equalsIgnoreCase("doublejump")) {
            UUID uuid = player.getUniqueId();
            if (disabledDoubleJump.contains(uuid)) {
                disabledDoubleJump.remove(uuid);
                player.sendMessage("§aDouble Jump enabled!");
            } else {
                disabledDoubleJump.add(uuid);
                player.sendMessage("§cDouble Jump disabled!");
            }
            saveToggleState();
            return true;
        }
        return false;
    }

    private void openShardShop(Player player) {
        Inventory shop = Bukkit.createInventory(null, 9, "§aShard Shop");

        ItemStack feather = new ItemStack(Material.FEATHER);
        ItemMeta meta = feather.getItemMeta();
        meta.setDisplayName("§aDouble Jump Feather");
        meta.setLore(Arrays.asList("§7Price: §e1000 Shards", "§7Click to purchase"));
        feather.setItemMeta(meta);

        shop.setItem(4, feather);
        player.openInventory(shop);
    }

    @EventHandler
    public void onShopClick(InventoryClickEvent e) {
        if (!e.getView().getTitle().equals("§aShard Shop")) return;
        e.setCancelled(true);

        if (e.getCurrentItem() == null || e.getCurrentItem().getType() != Material.FEATHER) return;

        Player player = (Player) e.getWhoClicked();
        if (getShards(player) >= 1000) {
            removeShards(player, 1000);
            ItemStack feather = new ItemStack(Material.FEATHER);
            ItemMeta meta = feather.getItemMeta();
            meta.setDisplayName("§aDouble Jump Feather");
            feather.setItemMeta(meta);
            player.getInventory().addItem(feather);
            player.sendMessage("§aYou bought a Double Jump Feather!");
        } else {
            player.sendMessage("§cYou don't have enough shards! Need 1000.");
        }
    }


    private void saveShards() {
        for (UUID uuid : shards.keySet()) {
            plugin.getConfig().set("shards." + uuid.toString(), shards.get(uuid));
        }
        plugin.saveConfig();
    }


    private void loadShards() {
        if (plugin.getConfig().isConfigurationSection("shards")) {
            for (String id : plugin.getConfig().getConfigurationSection("shards").getKeys(false)) {
                shards.put(UUID.fromString(id), plugin.getConfig().getInt("shards." + id));
            }
        }
    }


    private void saveToggleState() {
        List<String> list = new ArrayList<>();
        for (UUID uuid : disabledDoubleJump) {
            list.add(uuid.toString());
        }
        plugin.getConfig().set("doublejump-disabled", list);
        plugin.saveConfig();
    }

    private void loadToggleState() {
        disabledDoubleJump.clear();
        if (plugin.getConfig().isList("doublejump-disabled")) {
            for (String id : plugin.getConfig().getStringList("doublejump-disabled")) {
                disabledDoubleJump.add(UUID.fromString(id));
            }
        }
    }
}