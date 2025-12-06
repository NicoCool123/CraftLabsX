package CraftLabsX.minigames;

public enum UpgradeType {
    AUTO_CLICKER("Auto Clicker", 10),
    MULTIPLIER("Multiplier", 50),
    GOLDEN_COOKIE("Golden Cookie", 200),
    CLICK_FRENZY("Click Frenzy", 500),
    COOKIE_FACTORY("Cookie Factory", 1000),
    RESEARCH_CENTER("Research Center", 5000),
    PRESTIGE("Prestige", 25000);

    private final String displayName;
    private final double basePrice;

    UpgradeType(String displayName, double basePrice) {
        this.displayName = displayName;
        this.basePrice = basePrice;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getBasePrice() {
        return basePrice;
    }
}