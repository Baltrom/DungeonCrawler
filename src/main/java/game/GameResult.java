package game;

/** Ergebnis eines Spiels: wie es endete und wie viele Raeume (ohne Raum 0) erreicht wurden. */
public record GameResult(GameOutcome outcome, int roomsReached) {
}
