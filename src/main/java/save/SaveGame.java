package save;

import character.CharacterClass;
import character.Player;
import character.PlayerName;
import dungeon.Dungeon;
import item.Inventory;
import item.Item;
import java.util.List;

/**
 * Alles, was zum Weiterspielen gebraucht wird: Seed (der Dungeon wird daraus neu erzeugt), Raum,
 * Lebenspunkte, Items, Name und Klasse, dazu was im Raum schon passiert ist.
 */
public record SaveGame(long seed, String name, CharacterClass characterClass, int roomIndex,
                       int currentHp, int spellCharges, List<Item> items,
                       boolean lootTried, boolean specialDone, boolean ordinaryDefeated,
                       boolean arcaneSight) {

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
        items = List.copyOf(items);
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
