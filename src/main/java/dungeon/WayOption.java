package dungeon;

import check.Difficulty;

/** Ein Weg in den naechsten Raum mit festem Zielwert. */
public record WayOption(Approach approach, Difficulty difficulty, int target) {
}
