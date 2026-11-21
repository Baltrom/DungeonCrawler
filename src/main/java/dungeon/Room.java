package dungeon;

import item.Item;
import java.util.List;

/**
 * Ein erzeugter Raum.
 *
 * @param template        Raumvorlage, null bei Eingang und Endgegner-Raum
 * @param wayOptions      Wege in den naechsten Raum, leer bei Eingang und Endgegner-Raum
 * @param loot            Beute-Stelle, null bei Eingang (dort haengt sie von der Klasse ab) und Endgegner-Raum
 * @param enemy           Gegner in diesem Raum
 * @param enemyItem       Beute des besonderen Gegners, sonst null
 * @param encounterTarget Zielwert fuer Schleichen/Spaehen bzw. Untersuchen der Statue, 0 ohne Gegner
 */
public record Room(int index, RoomKind kind, RoomTemplate template, List<WayOption> wayOptions,
                   LootSpot loot, EnemyKind enemy, Item enemyItem, int encounterTarget) {
}
