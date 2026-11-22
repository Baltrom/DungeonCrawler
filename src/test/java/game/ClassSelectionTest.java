package game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import character.CharacterClass;
import character.Player;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class ClassSelectionTest {

    private final ByteArrayOutputStream output = new ByteArrayOutputStream();

    // Jede Zeile ist eine Eingabe: Klasse, Bestaetigung (1 = Ja, 2 = Nein), Name
    private Player runWithInput(String... lines) {
        String input = String.join("\n", lines) + "\n";
        ConsoleIO io = new ConsoleIO(
                new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)),
                new PrintStream(output, true, StandardCharsets.UTF_8));
        return new ClassSelection(io).run();
    }

    private String printed() {
        return output.toString(StandardCharsets.UTF_8);
    }

    @Test
    void choosingMageConfirmingAndNamingCreatesAMage() {
        Player player = runWithInput("1", "1", "Ria");
        assertEquals(CharacterClass.MAGE, player.getCharacterClass());
        assertEquals("Ria", player.getName());
    }

    @Test
    void choosingRogueCreatesARogue() {
        Player player = runWithInput("2", "1", "Bob");
        assertEquals(CharacterClass.ROGUE, player.getCharacterClass());
        assertEquals("Bob", player.getName());
    }

    @Test
    void sayingNoGoesBackToTheSelection() {
        // Magier waehlen, mit Nein ablehnen, dann Schurke waehlen und bestaetigen
        Player player = runWithInput("1", "2", "2", "1", "Bob");
        assertEquals(CharacterClass.ROGUE, player.getCharacterClass());
        assertEquals("Bob", player.getName());
    }

    @Test
    void invalidNamesAreRejectedUntilAValidOneIsGiven() {
        Player player = runWithInput("1", "1", "Max Muster", "", "a_b", "Max");
        assertEquals("Max", player.getName());
        assertTrue(printed().contains("Ungueltiger Name"));
    }

    @Test
    void invalidMenuInputIsAskedAgain() {
        Player player = runWithInput("9", "abc", "0", "1", "1", "Ria");
        assertEquals(CharacterClass.MAGE, player.getCharacterClass());
        assertTrue(printed().contains("Bitte eine Zahl von 1 bis 2 eingeben."));
    }

    @Test
    void classesAreShownSideBySideAndFitIn80Columns() {
        runWithInput("1", "1", "Ria");
        String[] lines = printed().split("\\R");
        boolean sideBySide = false;
        boolean optionsOnSameLine = false;
        for (String line : lines) {
            assertTrue(line.length() <= 80, "Line longer than 80 characters: " + line);
            if (line.contains("MAGIER") && line.contains("SCHURKE")) {
                sideBySide = true;
            }
            if (line.contains("[1] Magier") && line.contains("[2] Schurke")) {
                optionsOnSameLine = true;
            }
        }
        assertTrue(sideBySide, "MAGIER and SCHURKE are not on the same line");
        assertTrue(optionsOnSameLine, "The options [1] and [2] are not on the same line");
    }

    @Test
    void screenShowsBothClassesWithValuesAndStaysAscii() {
        runWithInput("1", "1", "Ria");
        String text = printed();
        assertTrue(text.contains("MAGIER"));
        assertTrue(text.contains("SCHURKE"));
        assertTrue(text.contains("Lebenspunkte"));
        assertTrue(text.contains("Bist du sicher?"));
        assertTrue(text.chars().noneMatch(c -> Character.isISOControl(c) && c != '\n' && c != '\r' && c != 27),
                "Output contains control characters");
    }
}
