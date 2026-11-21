package item;

import java.util.Arrays;
import java.util.List;

public enum Item {

    HEALING_POTION("Heiltrank", ItemType.POTION, "Heilt 6 Lebenspunkte", Bonuses.NONE, 0),
    CHARGE_POTION("Ladungstrank", ItemType.POTION, "Fuellt die Zauberladungen des Magiers auf", Bonuses.NONE, 2),
    RING_OF_LIFE("Ring des Lebens", ItemType.PASSIVE, "+3 maximale Lebenspunkte", Bonuses.maxHp(3), 1),
    RING_OF_WISDOM("Ring der Klugheit", ItemType.PASSIVE, "+1 Intelligenz", Bonuses.intelligence(1), 2),
    BOOTS_OF_SHADOWS("Stiefel der Schatten", ItemType.PASSIVE, "+1 Geschick", Bonuses.dexterity(1), 2),
    STRENGTH_BELT("Kraftguertel", ItemType.PASSIVE, "+1 Staerke", Bonuses.strength(1), 2),
    PROTECTION_AMULET("Schutzamulett", ItemType.PASSIVE, "+1 Ruestungsklasse", Bonuses.armorClass(1), 2),
    MAGIC_DAGGER("Magischer Dolch", ItemType.PASSIVE, "+1 auf Treffer und +1 Schaden", Bonuses.hitAndDamage(1), 2),
    IRON_KEY("Eiserner Schluessel", ItemType.KEY, "Oeffnet eine Tuer ohne Wurf, danach ist er weg", Bonuses.NONE, 2);

    public static final int HEALING_POTION_AMOUNT = 6;

    private final String displayName;
    private final ItemType type;
    private final String description;
    private final Bonuses bonuses;
    private final int lootWeight;

    Item(String displayName, ItemType type, String description, Bonuses bonuses, int lootWeight) {
        this.displayName = displayName;
        this.type = type;
        this.description = description;
        this.bonuses = bonuses;
        this.lootWeight = lootWeight;
    }

    public String getDisplayName() {
        return displayName;
    }

    public ItemType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public Bonuses getBonuses() {
        return bonuses;
    }

    /** Wie haeufig das Item als zufaellige Beute vorkommt. 0 = kommt nie als Zufallsbeute vor. */
    public int getLootWeight() {
        return lootWeight;
    }

    /** Alle Items, die als zufaellige Beute vorkommen koennen (der einzige Heiltrank ist fest platziert). */
    public static List<Item> lootPool() {
        return Arrays.stream(values()).filter(item -> item.lootWeight > 0).toList();
    }
}
