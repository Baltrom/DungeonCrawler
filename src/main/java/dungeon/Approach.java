package dungeon;

import character.Attribute;

/** Ein Ansatz, um in den naechsten Raum zu kommen. Der schwerere Ansatz hat eine Stufe mehr Schwierigkeit. */
public record Approach(Attribute attribute, String label, boolean harder) {
}
