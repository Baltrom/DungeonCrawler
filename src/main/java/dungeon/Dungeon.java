package dungeon;

import static character.Attribute.DEXTERITY;
import static character.Attribute.INTELLIGENCE;

import character.CharacterClass;
import check.Difficulty;
import item.Item;
import java.util.List;

/** Ein komplett erzeugter Dungeon: Raum 0 (draussen) bis Raum 5 (Endgegner). */
public record Dungeon(long seed, List<Room> rooms, int startHelpTarget) {

    public static final int LAST_ROOM = 5;

    public Room room(int index) {
        return rooms.get(index);
    }

    /** Die Starthilfe vor dem Tor haengt von der Klasse ab: Magier Ladungstrank, Schurke Schutzamulett. */
    public LootSpot startHelp(CharacterClass characterClass) {
        return switch (characterClass) {
            case MAGE -> new LootSpot(INTELLIGENCE, "Die Runen am Tor lesen",
                    Difficulty.EASY, startHelpTarget, Item.CHARGE_POTION);
            case ROGUE -> new LootSpot(DEXTERITY, "Das verrostete Gitter absuchen",
                    Difficulty.EASY, startHelpTarget, Item.PROTECTION_AMULET);
        };
    }
}
