package dungeon;

import check.Difficulty;
import item.Item;

/**
 * Eine Interaktion mit den vom Generator gewuerfelten Werten.
 *
 * @param difficulty Schwierigkeit des Checks, null ohne Check
 * @param target     Zielwert des Checks, 0 ohne Check
 * @param item       Item bei Effekt ITEM, null wenn das Versteck leer ist
 * @param follow     Folge-Interaktion oder null
 */
public record Interaction(InteractionDef def, Difficulty difficulty, int target, Item item, Interaction follow) {

    public String id() {
        return def.id();
    }
}
