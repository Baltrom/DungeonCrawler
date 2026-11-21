package dungeon;

import character.Attribute;
import check.Difficulty;
import item.Item;

/** Eine Beute-Stelle mit Check. item ist null, wenn das Versteck leer ist. */
public record LootSpot(Attribute attribute, String label, Difficulty difficulty, int target, Item item) {
}
