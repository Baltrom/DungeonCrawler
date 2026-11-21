package game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class ConsoleIOTest {

    private final ByteArrayOutputStream output = new ByteArrayOutputStream();

    private ConsoleIO io(String... lines) {
        String input = String.join("\n", lines) + "\n";
        return new ConsoleIO(new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8)),
                new PrintStream(output, true, StandardCharsets.UTF_8));
    }

    private String printed() {
        return output.toString(StandardCharsets.UTF_8);
    }

    private static int count(String text, String part) {
        int count = 0;
        int index = 0;
        while ((index = text.indexOf(part, index)) >= 0) {
            count++;
            index += part.length();
        }
        return count;
    }

    @Test
    void aReportedResultIsPrintedAtOnce() {
        io().report("Wurf: 14 -> ERFOLG");
        assertTrue(printed().contains("Wurf: 14 -> ERFOLG"));
    }

    @Test
    void afterClearingTheScreenTheResultsAreTheFirstBlock() {
        ConsoleIO io = io();
        io.report("Wurf: 14 -> ERFOLG");
        io.report("Du kommst durch.");
        io.clearScreen();
        io.println("RAUM 2");

        String text = printed();
        int afterBlankLines = text.lastIndexOf("--- Was zuletzt geschah ---");
        assertTrue(afterBlankLines > 0, "The results block is missing after the blank lines");
        String lastBlock = text.substring(afterBlankLines);
        assertTrue(lastBlock.contains("Wurf: 14 -> ERFOLG"));
        assertTrue(lastBlock.indexOf("Du kommst durch.") < lastBlock.indexOf("RAUM 2"));
    }

    @Test
    void resultsCanBeShownAfterTheRoomIntro() {
        ConsoleIO io = io();
        io.report("Du kommst durch.");
        io.clearScreenOnly();
        io.println("RAUM 2");
        io.showRecentResults();

        String text = printed();
        int afterBlankLines = text.lastIndexOf("RAUM 2");
        assertTrue(text.indexOf("--- Was zuletzt geschah ---", afterBlankLines) > afterBlankLines);
        io.clearScreenOnly();
        io.showRecentResults();
        assertEquals(1, count(text.substring(afterBlankLines), "--- Was zuletzt geschah ---"));
    }

    @Test
    void theResultsAreShownOnlyOnceAfterTheNextClear() {
        ConsoleIO io = io();
        io.report("Treffer");
        io.clearScreen();
        io.clearScreen();
        assertEquals(1, count(printed(), "--- Was zuletzt geschah ---"));
    }

    @Test
    void clearingWithoutResultsPrintsOnlyBlankLines() {
        ConsoleIO io = io();
        io.clearScreen();
        assertFalse(printed().contains("Was zuletzt geschah"));
        assertEquals(50, printed().split("\\R", -1).length - 1);
    }

    @Test
    void askChoiceAcceptsOnlyNumbersInRange() {
        ConsoleIO io = io("x", "9", "2");
        assertEquals(2, io.askChoice("Wahl: ", 1, 3));
        assertTrue(printed().contains("Bitte eine Zahl von 1 bis 3 eingeben."));
    }
}
