package character;

public enum Spell {

    FIREBALL("Feuerball", "D10 Schaden, trifft immer", true, false),
    SHIELD("Schutzschild", "+3 Ruestungsklasse bis zum Ende des Kampfes", true, false),
    ARCANE_SIGHT("Arkane Sicht", "+3 auf deinen naechsten Intelligenz-Check", false, true);

    public static final int SHIELD_BONUS = 3;
    public static final int ARCANE_SIGHT_BONUS = 3;

    private final String displayName;
    private final String description;
    private final boolean usableInCombat;
    private final boolean usableOutsideCombat;

    Spell(String displayName, String description, boolean usableInCombat, boolean usableOutsideCombat) {
        this.displayName = displayName;
        this.description = description;
        this.usableInCombat = usableInCombat;
        this.usableOutsideCombat = usableOutsideCombat;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isUsableInCombat() {
        return usableInCombat;
    }

    public boolean isUsableOutsideCombat() {
        return usableOutsideCombat;
    }
}
