package game;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class MainMenuTest {

    private final ByteArrayOutputStream output = new ByteArrayOutputStream();

    private void runWithInput(String... lines) {
        String input = String.join("\n", lines) + "\n";
        ConsoleIO io = new ConsoleIO(
                new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)),
                new PrintStream(output, true, StandardCharsets.UTF_8));
        new MainMenu(io).run();
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
    void newGameLeadsThroughClassSelectionAndBackToTheMenu() {
        // Neues Spiel, Magier, Ja, Name, Enter, dann Beenden
        runWithInput("1", "1", "1", "Ria", "", "4");
        String text = printed();
        assertTrue(text.contains("Willkommen, Ria der Magier!"));
        assertTrue(text.contains("Auf Wiedersehen!"));
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
