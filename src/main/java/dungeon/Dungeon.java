package dungeon;

import java.util.List;

/** Ein komplett erzeugter Dungeon: Raum 0 (draussen) bis Raum 5 (Endgegner). */
public record Dungeon(long seed, List<Room> rooms) {

    public static final int LAST_ROOM = 5;

    public Room room(int index) {
        return rooms.get(index);
    }
}
