package game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import character.CharacterClass;
import character.Player;
import highscore.HighscoreEntry;
import highscore.Highscores;
import item.Item;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import save.SaveGame;
import save.SaveManager;

class MainMenuTest {

    /** Ersetzt das echte Spiel: merkt sich, wer gestartet wurde, und liefert ein festes Ergebnis. */
    private static class StubLauncher implements GameLauncher {
        final List<Player> newGames = new ArrayList<>();
        final List<SaveGame> resumedGames = new ArrayList<>();
        GameResult result = new GameResult(GameOutcome.DEATH, 0);

        @Override
        public GameResult play(Player player) {
            newGames.add(player);
            return result;
        }

        @Override
        public GameResult resume(SaveGame save) {
            resumedGames.add(save);
            return result;
        }
    }

    @TempDir
    Path dir;

    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private final StubLauncher launcher = new StubLauncher();

    private Highscores highscores() {
        return new Highscores(dir.resolve("highscores.txt"));
    }

    private SaveManager saves() {
        return new SaveManager(dir.resolve("savegame.txt"));
    }

    private static SaveGame sampleSave() {
        return new SaveGame(42L, "Lena", CharacterClass.ROGUE, 2, 11, 0, List.of(Item.IRON_KEY),
                List.of("poi1"), false, false, false, 0, false);
    }

    private void runWithInput(String... lines) {
        String input = String.join("\n", lines) + "\n";
        ConsoleIO io = new ConsoleIO(
                new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)),
                new PrintStream(output, true, StandardCharsets.UTF_8));
        new MainMenu(io, launcher, highscores(), saves()).run();
    }

    private String printed() {
        return output.toString(StandardCharsets.UTF_8);
    }

    // --- Menue ---

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
    void invalidMenuInputIsAskedAgain() {
        runWithInput("x", "7", "4");
        assertTrue(printed().contains("Bitte eine Zahl von 1 bis 4 eingeben."));
    }

    // --- Neues Spiel und Ende ---

    @Test
    void newGameStartsTheGameForTheChosenCharacterAndShowsTheResult() {
        launcher.result = new GameResult(GameOutcome.DEATH, 3);
        // Neues Spiel, Magier, Ja, Name, (Spiel endet), Enter, Beenden
        runWithInput("1", "1", "1", "Ria", "", "4");
        assertEquals(1, launcher.newGames.size());
        assertEquals("Ria", launcher.newGames.get(0).getName());
        assertEquals(CharacterClass.MAGE, launcher.newGames.get(0).getCharacterClass());
        String text = printed();
        assertTrue(text.contains("DU BIST GESTORBEN"));
        assertTrue(text.contains("Ria der Magier erreichte Raum 3."));
        assertTrue(text.contains("Auf Wiedersehen!"));
    }

    @Test
    void aVictoryShowsTheVictoryScreen() {
        launcher.result = new GameResult(GameOutcome.VICTORY, 5);
        runWithInput("1", "2", "1", "Bob", "", "4");
        assertTrue(printed().contains("SIEG"));
        assertTrue(printed().contains("Bob der Schurke erreichte Raum 5."));
    }

    @Test
    void deathAndVictoryAreWrittenToTheHighscores() {
        launcher.result = new GameResult(GameOutcome.DEATH, 3);
        runWithInput("1", "1", "1", "Ria", "", "4");
        launcher.result = new GameResult(GameOutcome.VICTORY, 5);
        runWithInput("1", "2", "1", "Bob", "", "4");
        List<HighscoreEntry> entries = highscores().load();
        assertEquals(2, entries.size());
        assertEquals(new HighscoreEntry("Bob", CharacterClass.ROGUE, 5), entries.get(0));
        assertEquals(new HighscoreEntry("Ria", CharacterClass.MAGE, 3), entries.get(1));
        assertTrue(printed().contains("Platz 1 in der Rangliste!"));
    }

    @Test
    void afterDeathOrVictoryTheHighscoreEntryComesFirstAndThenTheSaveIsDeleted() {
        saves().save(sampleSave());
        launcher.result = new GameResult(GameOutcome.DEATH, 2);
        runWithInput("1", "1", "1", "Ria", "", "4");
        assertEquals(1, highscores().load().size());
        assertFalse(saves().exists());
    }

    @Test
    void savingAndQuittingWritesNoEntryAndKeepsTheSave() {
        saves().save(sampleSave());
        launcher.result = new GameResult(GameOutcome.SAVED, 2);
        runWithInput("1", "1", "1", "Ria", "", "4");
        assertTrue(highscores().load().isEmpty());
        assertTrue(saves().exists());
        assertTrue(printed().contains("SPIEL GESPEICHERT"));
    }

    // --- Rangliste ---

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

    // --- Spiel laden ---

    @Test
    void loadingWithoutASaveSaysSo() {
        runWithInput("2", "", "4");
        assertTrue(printed().contains("Es gibt keinen gespeicherten Spielstand."));
        assertTrue(launcher.resumedGames.isEmpty());
    }

    @Test
    void loadingResumesTheSavedGameAndDeletesTheSave() {
        saves().save(sampleSave());
        launcher.result = new GameResult(GameOutcome.SAVED, 2);
        runWithInput("2", "", "4");
        assertEquals(List.of(sampleSave()), launcher.resumedGames);
        assertFalse(saves().exists(), "Loading must delete the old save");
    }

    @Test
    void aLoadedGameThatEndsInDeathIsAddedToTheHighscoresWithTheSavedNameAndClass() {
        saves().save(sampleSave());
        launcher.result = new GameResult(GameOutcome.DEATH, 4);
        runWithInput("2", "", "4");
        assertEquals(List.of(new HighscoreEntry("Lena", CharacterClass.ROGUE, 4)), highscores().load());
        assertTrue(printed().contains("Lena der Schurke erreichte Raum 4."));
    }

    @Test
    void aBrokenSaveIsReportedAndNotDeleted() throws IOException {
        Files.writeString(dir.resolve("savegame.txt"), "kaputt", StandardCharsets.UTF_8);
        runWithInput("2", "", "4");
        assertTrue(printed().contains("beschaedigt"));
        assertTrue(saves().exists());
        assertTrue(launcher.resumedGames.isEmpty());
    }

    @Test
    void outputStaysAscii() {
        highscores().add(new HighscoreEntry("Bob", CharacterClass.ROGUE, 4));
        launcher.result = new GameResult(GameOutcome.DEATH, 1);
        runWithInput("3", "", "1", "2", "1", "Bob", "", "2", "", "4");
        assertTrue(printed().chars().allMatch(c -> c < 128), "Output contains non-ASCII characters");
    }
}
