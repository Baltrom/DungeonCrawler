package game;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

// Nur ein Rauchtest, damit wir sehen, dass JUnit laeuft. Wird spaeter durch echte Tests ersetzt.
class MainTest {

    @Test
    void titleIsSet() {
        assertEquals("DUNGEON CRAWLER", Main.TITLE);
    }
}
