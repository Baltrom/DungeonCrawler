package dungeon;

import item.Item;
import java.util.List;

/**
 * Ein erzeugter Raum.
 *
 * @param template        Raumvorlage, null bei Eingang und Endgegner-Raum
 * @param blocked         ob der Durchgang versperrt ist (nur dann gibt es Weg-Checks)
 * @param wayOptions      Wege durch das Hindernis, leer bei freiem Durchgang
 * @param interactions    Dinge zum Untersuchen
 * @param enemy           Gegner in diesem Raum
 * @param enemyItem       Beute des besonderen Gegners, sonst null
 * @param encounterTarget Zielwert fuer Schleichen/Spaehen bzw. Untersuchen der Statue, 0 ohne Gegner
 */
public record Room(int index, RoomKind kind, RoomTemplate template, boolean blocked, List<WayOption> wayOptions,
                   List<Interaction> interactions, EnemyKind enemy, Item enemyItem, int encounterTarget) {

    /** Name der Skriptdatei dieses Raums (src/main/resources/story/rooms/). */
    public String scriptName() {
        return switch (kind) {
            case ENTRANCE -> "room0";
            case BOSS -> "room5";
            case NORMAL -> template.getTextName();
        };
    }

    public RoomScript script() {
        return RoomScript.load(scriptName());
    }
}
