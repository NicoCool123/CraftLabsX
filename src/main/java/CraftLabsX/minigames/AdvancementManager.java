package CraftLabsX.minigames;
import java.util.*;

public class AdvancementManager {
    private final Map<UUID, Set<AdvancementType>> claimedAdvancements = new HashMap<>();
    private final List<AdvancementType> customAdvancements = new ArrayList<>();

    public boolean isClaimed(UUID playerId, AdvancementType advancement) {
        return claimedAdvancements.getOrDefault(playerId, Collections.emptySet()).contains(advancement);
    }

    public void claim(UUID playerId, AdvancementType advancement) {
        claimedAdvancements.computeIfAbsent(playerId, id -> new HashSet<>()).add(advancement);
    }

    public void addCustomAdvancement(AdvancementType advancement) {
        customAdvancements.add(advancement);
    }

    public List<AdvancementType> getCustomAdvancements() {
        return Collections.unmodifiableList(customAdvancements);
    }
}