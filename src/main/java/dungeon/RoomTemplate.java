package dungeon;

import static character.Attribute.DEXTERITY;
import static character.Attribute.INTELLIGENCE;
import static character.Attribute.STRENGTH;

import java.util.List;

/** Die acht Raumvorlagen. Raum 1-4 werden aus diesem Pool gezogen. */
public enum RoomTemplate {

    LIBRARY("Die Bibliothek", "library",
            List.of(new Approach(INTELLIGENCE, "Die Runen am Tor entziffern", false),
                    new Approach(DEXTERITY, "Das Schloss des Tores knacken", true)),
            new LootAction(INTELLIGENCE, "Die alten Runen auf den Seiten lesen")),

    CRYPT("Die Gruft", "crypt",
            List.of(new Approach(INTELLIGENCE, "Die Siegelrunen am Sarkophag entziffern", false),
                    new Approach(STRENGTH, "Den Sarkophag zur Seite stemmen", true)),
            new LootAction(DEXTERITY, "Den Toten am Boden durchsuchen")),

    STATUE_HALL("Der Statuensaal", "statue_hall",
            List.of(new Approach(DEXTERITY, "Das Schloss der Tuer knacken", false),
                    new Approach(STRENGTH, "Die Tuer aufbrechen", true)),
            new LootAction(DEXTERITY, "Die kleine Schatulle oeffnen")),

    TRAP_CORRIDOR("Der Fallengang", "trap_corridor",
            List.of(new Approach(DEXTERITY, "Ueber die Druckplatten springen", false),
                    new Approach(INTELLIGENCE, "Das Muster der Platten erkennen", true)),
            new LootAction(INTELLIGENCE, "Das geheime Fach in der Wand suchen")),

    ALCHEMY_LAB("Das Alchemielabor", "alchemy_lab",
            List.of(new Approach(DEXTERITY, "Das Schloss der Giftgas-Tuer knacken", false),
                    new Approach(STRENGTH, "Die Tuer aufbrechen", true)),
            new LootAction(DEXTERITY, "Die verschlossene Schatulle auf dem Tisch oeffnen")),

    PRISON("Das Verlies", "prison",
            List.of(new Approach(DEXTERITY, "Das Schloss des Gitters knacken", false),
                    new Approach(STRENGTH, "Das Gitter aufbrechen", true)),
            new LootAction(INTELLIGENCE, "Das geheime Fach in der Zellenwand suchen")),

    CISTERN("Die Zisterne", "cistern",
            List.of(new Approach(DEXTERITY, "Ueber die morsche Bruecke balancieren", false),
                    new Approach(STRENGTH, "Eine neue Bruecke aus Truemmern bauen", true)),
            new LootAction(INTELLIGENCE, "Den glaenzenden Gegenstand im Schlamm untersuchen")),

    ARMORY("Die Waffenkammer", "armory",
            List.of(new Approach(INTELLIGENCE, "Den Einsturz der Decke genau untersuchen", false),
                    new Approach(STRENGTH, "Die Truemmer wegraeumen", true)),
            new LootAction(STRENGTH, "Die schwere Truhe aufbrechen"));

    private final String displayName;
    private final String textName;
    private final List<Approach> approaches;
    private final LootAction lootAction;

    RoomTemplate(String displayName, String textName, List<Approach> approaches, LootAction lootAction) {
        this.displayName = displayName;
        this.textName = textName;
        this.approaches = approaches;
        this.lootAction = lootAction;
    }

    public String getDisplayName() {
        return displayName;
    }

    /** Name der Textdatei mit der Raumbeschreibung (src/main/resources/story/). */
    public String getTextName() {
        return textName;
    }

    public List<Approach> getApproaches() {
        return approaches;
    }

    public LootAction getLootAction() {
        return lootAction;
    }
}
