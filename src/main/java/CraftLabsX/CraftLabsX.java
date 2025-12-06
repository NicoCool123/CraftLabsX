package CraftLabsX;

import CraftLabsX.bansystem.BanCommand;
import CraftLabsX.bansystem.BanListener;
import CraftLabsX.minigames.*;
import CraftLabsX.placeholders.ShardsPlaceholder;
import CraftLabsX.serverchanger.serverchanger;
import CraftLabsX.lobbyitems.doublejump;
import CraftLabsX.shards.shardsmanager;
import CraftLabsX.economy.commands.*;
import CraftLabsX.economy.tabcomplete.*;
import CraftLabsX.economy.listeners.*;
import CraftLabsX.economy.database.*;
import CraftLabsX.economy.EconomyManager;
import CraftLabsX.economy.storage.ConfigManager;
import CraftLabsX.economy.tasks.ExpiryCleanupTask;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class CraftLabsX extends JavaPlugin {

    private doublejump doubleJump;
    private serverchanger serverChanger;
    private GameManager gameManager;
    private AdvancementManager advancementManager;
    private Economy econ;
    private ConfigManager configManager;
    private DatabaseManager databaseManager;
    private EconomyManager economyManager;

    @Override
    public void onEnable() {

        setupEconomy();


        gameManager = new GameManager();
        advancementManager = new AdvancementManager();


        getServer().getPluginManager().registerEvents(new CookieClickerListener(this), this);
        getServer().getPluginManager().registerEvents(new AdvancementListener(advancementManager, gameManager, econ), this);
        getCommand("cookieclicker").setExecutor(new CookieClickerCommand(this));
        getCommand("advancement").setExecutor(new AdvancementCommand(this, gameManager, advancementManager, econ));
        getCommand("tempban").setExecutor(new BanCommand());
        getCommand("permaban").setExecutor(new BanCommand());
        getCommand("unban").setExecutor(new BanCommand());
        getServer().getPluginManager().registerEvents(new BanListener(), this);


        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new ShardsPlaceholder(this).register();
        }


        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        serverChanger = new serverchanger(this);
        doubleJump = new doublejump(this);
        Bukkit.getPluginManager().registerEvents(serverChanger, this);
        Bukkit.getPluginManager().registerEvents(doubleJump, this);


        getCommand("craftlabsx").setExecutor(serverChanger);
        getCommand("shardshop").setExecutor(doubleJump);
        getCommand("doublejump").setExecutor(doubleJump);
        getCommand("shards").setExecutor(new shardsmanager(doubleJump));


        setupDatabase();
        configManager = new ConfigManager(this);
        economyManager = new EconomyManager(econ, databaseManager);
        registerEconomyCommands();
        registerEconomyListeners();
        startCleanupTask();

        saveDefaultConfig();
        getLogger().info("CraftLabsX enabled!");
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) databaseManager.disconnect();
        getLogger().info("CraftLabsX disabled!");
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public AdvancementManager getAdvancementManager() {
        return advancementManager;
    }

    public Economy getEconomy() {
        return econ;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            getLogger().warning("Vault plugin not found! Economy features will be disabled.");
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;
        econ = rsp.getProvider();
        return econ != null;
    }

    public doublejump getDoubleJumpManager() {
        return doubleJump;
    }

    private void setupDatabase() {
        String type = getConfig().getString("database.type", "sqlite").toLowerCase();
        if (type.equals("mysql")) {
            databaseManager = new MySQLDatabase(
                    getConfig().getString("database.host"),
                    getConfig().getInt("database.port"),
                    getConfig().getString("database.name"),
                    getConfig().getString("database.user"),
                    getConfig().getString("database.password")
            );
        } else {
            databaseManager = new SQLiteDatabase(getDataFolder());
        }
        databaseManager.connect();
        databaseManager.initTables();
    }

    private void registerEconomyCommands() {
        bindCommand("ah", new AuctionCommand(economyManager, configManager), new AuctionTabCompleter());
        bindCommand("order", new OrderCommand(economyManager, configManager), new OrderTabCompleter());
        bindCommand("shop", new ShopCommand(economyManager, configManager), new ShopTabCompleter());
        bindCommand("sell", new SellCommand(economyManager, configManager), new SellTabCompleter());
        bindCommand("sellmulti", new SellMultiplierCommand(economyManager, configManager), new SellMultiplierTabCompleter());
        bindCommand("money", new MoneyCommand() ,  new MoneyTabCompleter());
        bindCommand("bal", new BalCommand(), new BalTabCompleter());
        bindCommand("baltop", new BaltopCommand(), new BaltopTabCompleter());
        bindCommand("moneyadmin", new AdminMoneyCommand(economyManager), new AdminMoneyTabCompleter());
    }

    private void registerEconomyListeners() {
        Bukkit.getPluginManager().registerEvents(new ShopClickListener(economyManager, configManager), this);
        Bukkit.getPluginManager().registerEvents(new AuctionHouseClickListener(economyManager, configManager), this);
        Bukkit.getPluginManager().registerEvents(new OrderClickListener(economyManager, configManager), this);
        Bukkit.getPluginManager().registerEvents(new SellMultiplierClickListener(), this);
        Bukkit.getPluginManager().registerEvents(new MoneyClickListener(), this);
        Bukkit.getPluginManager().registerEvents(new BaltopClickListener(), this);
    }

    private void startCleanupTask() {
        new ExpiryCleanupTask(economyManager)
                .runTaskTimerAsynchronously(this, 6000L, 6000L);
    }


    private void bindCommand(String name, Object executor, Object completer) {
        PluginCommand cmd = getCommand(name);
        if (cmd != null) {
            if (executor instanceof org.bukkit.command.CommandExecutor) {
                cmd.setExecutor((org.bukkit.command.CommandExecutor) executor);
            }
            if (completer instanceof org.bukkit.command.TabCompleter) {
                cmd.setTabCompleter((org.bukkit.command.TabCompleter) completer);
            }
        }
    }
}