package highscore;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import character.CharacterClass;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class HighscoresTest {

    @TempDir
    Path dir;

    private Highscores highscores() {
        return new Highscores(dir.resolve("highscores.txt"));
    }

    private static HighscoreEntry entry(String name, int rooms) {
        return new HighscoreEntry(name, CharacterClass.MAGE, rooms);
    }

    @Test
    void anEmptyOrMissingFileGivesAnEmptyList() {
        assertTrue(highscores().load().isEmpty());
    }

    @Test
    void entriesAreSortedByRoomsDescending() {
        Highscores highscores = highscores();
        highscores.add(entry("Ria", 2));
        highscores.add(entry("Bob", 5));
        highscores.add(entry("Max", 3));
        List<HighscoreEntry> entries = highscores.load();
        assertEquals(List.of("Bob", "Max", "Ria"), entries.stream().map(HighscoreEntry::name).toList());
    }

    @Test
    void whoWasThereFirstRanksHigherOnATie() {
        Highscores highscores = highscores();
        highscores.add(entry("Erste", 3));
        highscores.add(entry("Zweite", 3));
        Highscores.AddResult result = highscores.add(entry("Dritte", 3));
        assertEquals(3, result.rank());
        assertEquals(List.of("Erste", "Zweite", "Dritte"),
                result.entries().stream().map(HighscoreEntry::name).toList());
    }

    @Test
    void theRankOfTheNewEntryIsReturned() {
        Highscores highscores = highscores();
        highscores.add(entry("Bob", 5));
        assertEquals(2, highscores.add(entry("Ria", 5)).rank());
        assertEquals(1, highscores.add(entry("Chef", 6)).rank());
    }

    @Test
    void onlyTheTopTenAreKeptAndAnEntryBelowThemGetsNoRank() {
        Highscores highscores = highscores();
        for (int i = 0; i < 10; i++) {
            highscores.add(entry("Spieler" + i, 4));
        }
        Highscores.AddResult tooLow = highscores.add(entry("Zuspaet", 4));
        assertEquals(-1, tooLow.rank());
        assertEquals(10, highscores.load().size());
        assertTrue(highscores.load().stream().noneMatch(e -> e.name().equals("Zuspaet")));

        Highscores.AddResult better = highscores.add(entry("Besser", 5));
        assertEquals(1, better.rank());
        assertEquals(10, highscores.load().size());
        assertTrue(highscores.load().stream().noneMatch(e -> e.name().equals("Spieler9")));
    }

    @Test
    void theListSurvivesANewInstance() {
        highscores().add(new HighscoreEntry("Bob", CharacterClass.ROGUE, 4));
        List<HighscoreEntry> entries = highscores().load();
        assertEquals(1, entries.size());
        assertEquals(new HighscoreEntry("Bob", CharacterClass.ROGUE, 4), entries.get(0));
    }

    @Test
    void playersWithTheSameNameGetSeparateEntries() {
        Highscores highscores = highscores();
        highscores.add(entry("Ria", 3));
        highscores.add(entry("Ria", 2));
        assertEquals(2, highscores.load().size());
    }

    @Test
    void brokenLinesAreIgnored() throws IOException {
        Path file = dir.resolve("highscores.txt");
        Files.write(file, List.of("Ria;MAGE;3", "kaputt", "Bob;WIZARD;2", "Max;ROGUE;abc", "Lena;ROGUE;1"),
                StandardCharsets.UTF_8);
        List<HighscoreEntry> entries = new Highscores(file).load();
        assertEquals(List.of("Ria", "Lena"), entries.stream().map(HighscoreEntry::name).toList());
    }

    @Test
    void describeShowsNameClassAndRooms() {
        assertEquals("Ria (Magier) - 5 Raeume", entry("Ria", 5).describe());
        assertEquals("Bob (Schurke) - 1 Raum", new HighscoreEntry("Bob", CharacterClass.ROGUE, 1).describe());
    }
}
