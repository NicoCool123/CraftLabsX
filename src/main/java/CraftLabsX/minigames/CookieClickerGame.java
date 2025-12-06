package CraftLabsX.minigames;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

public class CookieClickerGame {
    private UUID playerId;
    private int cookies;
    private Map<UpgradeType, Integer> upgrades;
    private int clickBonus;

    public CookieClickerGame(UUID id) {
        this.playerId = id;
        this.cookies = 0;
        this.clickBonus = 0;
        this.upgrades = new EnumMap<>(UpgradeType.class);
        for (UpgradeType type : UpgradeType.values()) {
            upgrades.put(type, 0);
        }
    }

    public UUID getPlayerId() { return playerId; }
    public int getCookies() { return cookies; }
    public void addCookies(int amount) { cookies += (amount + clickBonus); }
    public void removeCookies(int amount) { cookies = Math.max(0, cookies - amount); }
    public int getUpgradeCount(UpgradeType type) { return upgrades.get(type); }

    public double getNextPrice(UpgradeType type) {
        int owned = upgrades.get(type);
        return type.getBasePrice() * Math.pow(1.15, owned);
    }

    public boolean buyUpgrade(UpgradeType type, int amount) {
        double totalCost = 0;
        int owned = upgrades.get(type);

        for (int i = 0; i < amount; i++) {
            totalCost += type.getBasePrice() * Math.pow(1.15, owned + i);
        }

        if (cookies >= totalCost) {
            cookies -= totalCost;
            upgrades.put(type, owned + amount);
            if (type == UpgradeType.MULTIPLIER) clickBonus += amount;
            return true;
        }
        return false;
    }
}