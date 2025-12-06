package CraftLabsX.minigames;

import java.util.Arrays;
import java.util.List;

public class AdvancementType {
    private final String displayName;
    private final int requirement;
    private final RewardType rewardType;
    private final int rewardAmount;

    public AdvancementType(String displayName, int requirement, RewardType rewardType, int rewardAmount) {
        this.displayName = displayName;
        this.requirement = requirement;
        this.rewardType = rewardType;
        this.rewardAmount = rewardAmount;
    }

    public String getDisplayName() { return displayName; }
    public int getRequirement() { return requirement; }
    public RewardType getRewardType() { return rewardType; }
    public int getRewardAmount() { return rewardAmount; }

    public enum RewardType {
        MONEY,
        COOKIES,
        CLICK_BONUS
    }


    public static List<AdvancementType> getDefaultAdvancements() {
        return Arrays.asList(
                new AdvancementType("COOKIES_10K", 10000, RewardType.MONEY, 1000),
                new AdvancementType("MULTIPLIERS_50", 50, RewardType.CLICK_BONUS, 10),
                new AdvancementType("MONEY_100", 100, RewardType.COOKIES, 1000000),
                new AdvancementType("PRESTIGE_ONE", 1, RewardType.CLICK_BONUS, 5)
        );
    }
}