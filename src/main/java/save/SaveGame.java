package save;

import character.CharacterClass;
import character.Player;
import character.PlayerName;
import dungeon.Dungeon;
import item.Inventory;
import item.Item;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Alles, was zum Weiterspielen gebraucht wird: Seed (der Dungeon wird daraus neu erzeugt), Raum,
 * Lebenspunkte, Items, Name und Klasse, dazu was im Raum schon passiert ist.
 *
 * @param doneInteractions Ids der Interaktionen, die im Raum schon gemacht wurden
 * @param obstacleExamined ob das Hindernis im Raum schon untersucht wurde
 * @param insightBonus     +2, wenn eine Erkenntnis aktiv ist, sonst 0
 */
public record SaveGame(long seed, String name, CharacterClass characterClass, int roomIndex,
                       int currentHp, int spellCharges, List<Item> items,
                       List<String> doneInteractions, boolean obstacleExamined,
                       boolean specialDone, boolean ordinaryDefeated, int insightBonus,
                       boolean arcaneSight) {

    private static final Pattern VALID_ID = Pattern.compile("[a-z0-9.]+");

    public SaveGame {
        if (!PlayerName.isValid(name)) {
            throw new IllegalArgumentException("Invalid name: " + name);
        }
        // Gespeichert wird nur in Raum 0-4, im Endgegner-Raum gibt es kein Menue zum Speichern
        if (roomIndex < 0 || roomIndex >= Dungeon.LAST_ROOM) {
            throw new IllegalArgumentException("Invalid room: " + roomIndex);
        }
        if (items.size() > Inventory.CAPACITY) {
            throw new IllegalArgumentException("Too many items: " + items.size());
        }
        if (insightBonus != 0 && insightBonus != 2) {
            throw new IllegalArgumentException("Invalid insight bonus: " + insightBonus);
        }
        for (String id : doneInteractions) {
            if (!VALID_ID.matcher(id).matches()) {
                throw new IllegalArgumentException("Invalid interaction id: " + id);
            }
        }
        items = List.copyOf(items);
        doneInteractions = List.copyOf(doneInteractions);
    }

    /** Baut den Spieler wieder auf. Wirft IllegalArgumentException, wenn die Werte nicht zusammenpassen. */
    public Player toPlayer() {
        Player player = new Player(name, characterClass);
        for (Item item : items) {
            if (!player.addItem(item)) {
                throw new IllegalArgumentException("Inventory overflow");
            }
        }
        player.restoreState(currentHp, spellCharges);
        return player;
    }
}
