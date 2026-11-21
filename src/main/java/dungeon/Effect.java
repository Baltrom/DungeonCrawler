package dungeon;

/** Was eine gelungene Interaktion bewirkt. */
public enum Effect {
    /** Nur Stimmung. */
    NONE,
    /** Gibt das Item dieser Stelle (oder ist leer). */
    ITEM,
    /** Verraet, was im naechsten Raum wartet. */
    HINT,
    /** Gibt +2 auf den naechsten Check in diesem Raum. */
    INSIGHT
}
