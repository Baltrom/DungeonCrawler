package character;

import dice.Dice;

public enum CharacterClass {

    MAGE("Magier", "mage", 1, 2, 5, 12, 11, Dice.D6,
            "Alte Runen lesen, 3 Zauberladungen"),
    ROGUE("Schurke", "rogue", 2, 5, 1, 16, 13, Dice.D10,
            "Schloesser knacken, Fallen entschaerfen, Hinterhalt");

    private final String displayName;
    private final String artName;
    private final int strength;
    private final int dexterity;
    private final int intelligence;
    private final int maxHp;
    private final int armorClass;
    private final int damageDie;
    private final String description;

    CharacterClass(String displayName, String artName, int strength, int dexterity,
                   int intelligence, int maxHp, int armorClass, int damageDie,
                   String description) {
        this.displayName = displayName;
        this.artName = artName;
        this.strength = strength;
        this.dexterity = dexterity;
        this.intelligence = intelligence;
        this.maxHp = maxHp;
        this.armorClass = armorClass;
        this.damageDie = damageDie;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getArtName() {
        return artName;
    }

    public int getStrength() {
        return strength;
    }

    public int getDexterity() {
        return dexterity;
    }

    public int getIntelligence() {
        return intelligence;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getArmorClass() {
        return armorClass;
    }

    public int getDamageDie() {
        return damageDie;
    }

    public String getDescription() {
        return description;
    }
}
