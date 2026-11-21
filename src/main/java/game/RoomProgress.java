package game;

import java.util.LinkedHashSet;
import java.util.Set;

/** Was im aktuellen Raum schon passiert ist (wird beim Speichern mitgespeichert). */
class RoomProgress {

    /** Ids der Interaktionen, die schon gemacht wurden (jede nur einmal). */
    final Set<String> done = new LinkedHashSet<>();
    boolean obstacleExamined;
    boolean specialDone;
    boolean ordinaryDefeated;
    /** +2 auf den naechsten Check in diesem Raum, 0 wenn keine Erkenntnis aktiv ist. */
    int insightBonus;

    RoomProgress() {
    }

    RoomProgress(Set<String> done, boolean obstacleExamined, boolean specialDone, boolean ordinaryDefeated,
                 int insightBonus) {
        this.done.addAll(done);
        this.obstacleExamined = obstacleExamined;
        this.specialDone = specialDone;
        this.ordinaryDefeated = ordinaryDefeated;
        this.insightBonus = insightBonus;
    }
}
