package CraftLabsX.economy.tasks;

import CraftLabsX.economy.EconomyManager;
import org.bukkit.scheduler.BukkitRunnable;

public class ExpiryCleanupTask extends BukkitRunnable {

    private final EconomyManager economyManager;

    public ExpiryCleanupTask(EconomyManager economyManager) {
        this.economyManager = economyManager;
    }

    @Override
    public void run() {
        economyManager.getAuctionDAO().deleteExpiredAuctions();
        economyManager.getOrderDAO().deleteExpiredOrders();
    }
}
