package CraftLabsX.economy.manager;

public class SellMultiplier {

    private final String playerUUID;
    private final double multiplier;

    public SellMultiplier(String playerUUID, double multiplier) {
        this.playerUUID = playerUUID;
        this.multiplier = multiplier;
    }

    public String getPlayerUUID() {
        return playerUUID;
    }

    public double getMultiplier() {
        return multiplier;
    }
}
