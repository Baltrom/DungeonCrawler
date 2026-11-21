package game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import character.CharacterClass;
import character.Player;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class MainMenuTest {

    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private final List<Player> launchedPlayers = new ArrayList<>();

    private void runWithInput(GameResult resultOfTheGame, String... lines) {
        String input = String.join("\n", lines) + "\n";
        ConsoleIO io = new ConsoleIO(
                new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)),
                new PrintStream(output, true, StandardCharsets.UTF_8));
        new MainMenu(io, player -> {
            launchedPlayers.add(player);
            return resultOfTheGame;
        }).run();
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
    void loadGameAndHighscoresAreNotAvailableYetAndReturnToTheMenu() {
        runWithInput("2", "", "3", "", "4");
        String text = printed();
        assertTrue(text.contains("Spiel laden ist noch nicht verfuegbar."));
        assertTrue(text.contains("Rangliste ist noch nicht verfuegbar."));
        assertTrue(text.contains("Auf Wiedersehen!"));
    }

    @Test
    void invalidMenuInputIsAskedAgain() {
        runWithInput("x", "7", "4");
        assertTrue(printed().contains("Bitte eine Zahl von 1 bis 4 eingeben."));
    }

    @Test
    void outputStaysAscii() {
        runWithInput("1", "2", "1", "1", "Bob", "", "4");
        assertTrue(printed().chars().allMatch(c -> c < 128), "Output contains non-ASCII characters");
    }
}
