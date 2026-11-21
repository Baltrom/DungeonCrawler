package dungeon;

public enum EnemyKind {
    NONE,
    /** Gewoehnlicher Gegner, steht im Raum, wenn man ihn betritt. */
    ORDINARY,
    /** Besonderer Gegner (Gargoyle), wird freiwillig geweckt und gibt immer ein Item. */
    SPECIAL,
    BOSS
}
