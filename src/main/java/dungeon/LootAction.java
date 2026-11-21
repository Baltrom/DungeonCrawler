package dungeon;

import character.Attribute;

/** Die Beute-Moeglichkeit einer Raumvorlage (Schatulle oeffnen, Runen lesen, ...). */
public record LootAction(Attribute attribute, String label) {
}
