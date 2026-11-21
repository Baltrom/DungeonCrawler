package game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import character.CharacterClass;
import character.Player;
import highscore.HighscoreEntry;
import highscore.Highscores;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class MainMenuTest {

    @TempDir
    Path dir;

    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private final List<Player> launchedPlayers = new ArrayList<>();

    private Highscores highscores() {
        return new Highscores(dir.resolve("highscores.txt"));
    }

    private void runWithInput(GameResult resultOfTheGame, String... lines) {
        String input = String.join("\n", lines) + "\n";
        ConsoleIO io = new ConsoleIO(
                new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)),
                new PrintStream(output, true, StandardCharsets.UTF_8));
        new MainMenu(io, player -> {
            launchedPlayers.add(player);
            return resultOfTheGame;
        }, highscores()).run();
    }

    private void runWithInput(String... lines) {
        runWithInput(new GameResult(GameOutcome.DEATH, 0), lines);
    }

    private String printed() {
        return output.toString(StandardCharsets.UTF_8);
    }

    @Test
    void menuShowsAllEntriesAndTheTitleAndQuits() {
        runWithInput("4");
        String text = printed();
        assertTrue(text.contains("D U N G E O N"));
        assertTrue(text.contains("[1] Neues Spiel"));
        assertTrue(text.contains("[2] Spiel laden"));
        assertTrue(text.contains("[3] Rangliste anzeigen"));
        assertTrue(text.contains("[4] Beenden"));
        assertTrue(text.contains("Auf Wiedersehen!"));
    }

    @Test
    void newGameStartsTheGameForTheChosenCharacterAndShowsTheResult() {
        // Neues Spiel, Magier, Ja, Name, (Spiel endet), Enter, Beenden
        runWithInput(new GameResult(GameOutcome.DEATH, 3), "1", "1", "1", "Ria", "", "4");
        assertEquals(1, launchedPlayers.size());
        assertEquals("Ria", launchedPlayers.get(0).getName());
        assertEquals(CharacterClass.MAGE, launchedPlayers.get(0).getCharacterClass());
        String text = printed();
        assertTrue(text.contains("DU BIST GESTORBEN"));
        assertTrue(text.contains("Ria der Magier erreichte Raum 3."));
        assertTrue(text.contains("Auf Wiedersehen!"));
    }

    @Test
    void aVictoryShowsTheVictoryScreen() {
        runWithInput(new GameResult(GameOutcome.VICTORY, 5), "1", "2", "1", "Bob", "", "4");
        String text = printed();
        assertTrue(text.contains("SIEG"));
        assertTrue(text.contains("Bob der Schurke erreichte Raum 5."));
    }

    @Test
    void deathAndVictoryAreWrittenToTheHighscores() {
        runWithInput(new GameResult(GameOutcome.DEATH, 3), "1", "1", "1", "Ria", "", "4");
        runWithInput(new GameResult(GameOutcome.VICTORY, 5), "1", "2", "1", "Bob", "", "4");
        List<HighscoreEntry> entries = highscores().load();
        assertEquals(2, entries.size());
        assertEquals(new HighscoreEntry("Bob", CharacterClass.ROGUE, 5), entries.get(0));
        assertEquals(new HighscoreEntry("Ria", CharacterClass.MAGE, 3), entries.get(1));
        assertTrue(printed().contains("Platz 1 in der Rangliste!"));
    }

    @Test
    void savingAndQuittingDoesNotWriteAnEntry() {
        runWithInput(new GameResult(GameOutcome.SAVED, 2), "1", "1", "1", "Ria", "", "4");
        assertTrue(highscores().load().isEmpty());
        assertTrue(printed().contains("SPIEL GESPEICHERT"));
    }

    @Test
    void theHighscoreScreenListsTheEntriesInOrder() {
        highscores().add(new HighscoreEntry("Bob", CharacterClass.ROGUE, 4));
        highscores().add(new HighscoreEntry("Ria", CharacterClass.MAGE, 5));
        runWithInput("3", "", "4");
        String text = printed();
        assertTrue(text.contains("RANGLISTE - Top 10"));
        assertTrue(text.indexOf(" 1. Ria (Magier) - 5 Raeume") < text.indexOf(" 2. Bob (Schurke) - 4 Raeume"));
    }

    @Test
    void anEmptyHighscoreScreenSaysSo() {
        runWithInput("3", "", "4");
        assertTrue(printed().contains("Noch keine Eintraege"));
    }

    @Test
    void loadGameIsNotAvailableYetAndReturnsToTheMenu() {
        runWithInput("2", "", "4");
        assertTrue(printed().contains("Spiel laden ist noch nicht verfuegbar."));
        assertTrue(printed().contains("Auf Wiedersehen!"));
    }

    @Test
    void invalidMenuInputIsAskedAgain() {
        runWithInput("x", "7", "4");
        assertTrue(printed().contains("Bitte eine Zahl von 1 bis 4 eingeben."));
    }

    @Test
    void outputStaysAscii() {
        highscores().add(new HighscoreEntry("Bob", CharacterClass.ROGUE, 4));
        runWithInput("3", "", "1", "2", "1", "1", "Bob", "", "4");
        assertTrue(printed().chars().allMatch(c -> c < 128), "Output contains non-ASCII characters");
    }
}
