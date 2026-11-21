package character;

public enum Attribute {

    STRENGTH("Staerke"),
    DEXTERITY("Geschick"),
    INTELLIGENCE("Intelligenz");

    private final String displayName;

    Attribute(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
