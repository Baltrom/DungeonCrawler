package check;

import java.util.Random;

public enum Difficulty {

    EASY("leicht", 6, 8),
    MEDIUM("mittel", 10, 12),
    HARD("schwer", 14, 18);

    private final String displayName;
    private final int minTarget;
    private final int maxTarget;

    Difficulty(String displayName, int minTarget, int maxTarget) {
        this.displayName = displayName;
        this.minTarget = minTarget;
        this.maxTarget = maxTarget;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getMinTarget() {
        return minTarget;
    }

    public int getMaxTarget() {
        return maxTarget;
    }

    /** Waehlt einen Zielwert aus dem Bereich dieser Schwierigkeit (z. B. aus dem Dungeon-Seed). */
    public int pickTarget(Random random) {
        return minTarget + random.nextInt(maxTarget - minTarget + 1);
    }
}
