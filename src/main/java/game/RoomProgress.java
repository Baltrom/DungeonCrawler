package game;

/** Was im aktuellen Raum schon passiert ist (wird beim Speichern mitgespeichert). */
class RoomProgress {

    boolean lootTried;
    boolean specialDone;
    boolean ordinaryDefeated;

    RoomProgress() {
    }

    RoomProgress(boolean lootTried, boolean specialDone, boolean ordinaryDefeated) {
        this.lootTried = lootTried;
        this.specialDone = specialDone;
        this.ordinaryDefeated = ordinaryDefeated;
    }
}
